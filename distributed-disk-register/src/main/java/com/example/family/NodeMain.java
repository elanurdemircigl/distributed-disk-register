package com.example.family;
import family.Empty;
import family.FamilyServiceGrpc;
import family.FamilyView;
import family.NodeInfo;
import family.ChatMessage;

import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.Server;
import io.grpc.ServerBuilder;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.Socket;
import java.io.IOException;
import java.net.ServerSocket;
import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.*;

import java.io.BufferedWriter;
import java.io.OutputStreamWriter;

import com.hatokuse.grpc.MemberServiceGrpc;
import com.hatokuse.grpc.MessageRequest;
import com.hatokuse.grpc.MessageResponse;
import com.hatokuse.grpc.RetrieveRequest;
import com.hatokuse.grpc.RetrieveResponse;

import com.hatokuse.grpc.DeleteRequest;
import com.hatokuse.grpc.DeleteResponse;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.Files;


public class NodeMain {

    private static final int START_PORT = 5555;
    private static final int PRINT_INTERVAL_SECONDS = 10;

    private static DiskStorage diskStorage;
    private static CommandParser commandParser;


    private static final java.util.concurrent.ConcurrentHashMap<Integer, List<NodeInfo>> placement = new java.util.concurrent.ConcurrentHashMap<>();



    public static void main(String[] args) throws Exception {
        String host = "127.0.0.1";
        int port = findFreePort(START_PORT);

        diskStorage = new DiskStorage(String.valueOf(port));
        commandParser = new CommandParser(diskStorage);


        NodeInfo self = NodeInfo.newBuilder()
                .setHost(host)
                .setPort(port)
                .build();

        NodeRegistry registry = new NodeRegistry();
        FamilyServiceImpl service = new FamilyServiceImpl(registry, self);

        Server server = ServerBuilder
                .forPort(port)
                .addService(service)
                .addService(new MemberServiceImpl(diskStorage))
                .build()
                .start();

        System.out.printf("Node started on %s:%d%n", host, port);

        // Eğer bu ilk node ise (port 5555), TCP 6666'da text dinlesin
        if (port == START_PORT) {
            startLeaderTextListener(registry, self);
            startClusterStatsPrinter(registry, self);
        }

        discoverExistingNodes(host, port, registry, self);
        startFamilyPrinter(registry, self);
        startHealthChecker(registry, self);

        startLocalDiskCountPrinter(self, diskStorage);

        if (port == START_PORT) {
            startPlacementMapReportPrinter(registry, self);
        }

        server.awaitTermination();
    }

    private static void startLeaderTextListener(NodeRegistry registry, NodeInfo self) {
        // Sadece lider (5555 portlu node) bu methodu çağırmalı
        new Thread(() -> {
            try (ServerSocket serverSocket = new ServerSocket(6666)) {
                System.out.printf("Leader listening for text on TCP %s:%d%n",
                        self.getHost(), 6666);

                while (true) {
                    Socket client = serverSocket.accept();
                    new Thread(() -> handleClientTextConnection(client, registry, self)).start();
                }

            } catch (IOException e) {
                System.err.println("Error in leader text listener: " + e.getMessage());
            }
        }, "LeaderTextListener").start();
    }

    private static void handleClientTextConnection(Socket client,
                                                   NodeRegistry registry,
                                                   NodeInfo self) {
        System.out.println("New TCP client connected: " + client.getRemoteSocketAddress());
        try (
                BufferedReader reader = new BufferedReader(
                        new InputStreamReader(client.getInputStream()));
                BufferedWriter writer = new BufferedWriter(
                        new OutputStreamWriter(client.getOutputStream()))
        ) {

            String line;
            while ((line = reader.readLine()) != null) {
                String text = line.trim();
                if (text.isEmpty()) continue;

                long ts = System.currentTimeMillis();
                System.out.println("📝 Received from TCP: " + text);



                String result;

                String upper = text.toUpperCase();

                if (upper.startsWith("SET ")) {
                    int tolerance = readTolerance();


                    String[] parts = text.trim().split("\\s+", 3);
                    if (parts.length < 3) {
                        result = "ERROR";
                    } else {
                        int id;
                        try { id = Integer.parseInt(parts[1]); }
                        catch (NumberFormatException e) { id = -1; }

                        if (id < 0) {
                            result = "ERROR";
                        } else {

                            List<NodeInfo> replicas = replicateToMembers(text, registry, self, tolerance);


                            if (replicas.size() < tolerance) {
                                rollbackReplicas(id, replicas);
                                result = "ERROR";
                            } else {

                                placement.put(id, java.util.List.copyOf(replicas));
                                result = "OK";
                            }
                        }
                    }
                }
                else if (upper.startsWith("GET ")) {

                    String fallback = tryRetrieveFromMembers(text, registry, self);
                    result = (fallback != null) ? fallback : "NOT_FOUND";
                }
                else {
                    result = "ERROR: Unknown command";
                }


                if (text.toUpperCase().startsWith("SET ") && "OK".equals(result)) {

                }
                System.out.println("➡️ Command result: " + result);
                writer.write(result);
                writer.newLine();
                writer.flush();

                ChatMessage msg = ChatMessage.newBuilder()
                        .setText(text)
                        .setFromHost(self.getHost())
                        .setFromPort(self.getPort())
                        .setTimestamp(ts)
                        .build();


                broadcastToFamily(registry, self, msg);
            }

        } catch (IOException e) {
            System.err.println("TCP client handler error: " + e.getMessage());
        } finally {
            try { client.close(); } catch (IOException ignored) {}
        }
    }

    private static void broadcastToFamily(NodeRegistry registry,
                                          NodeInfo self,
                                          ChatMessage msg) {

        List<NodeInfo> members = registry.snapshot();

        for (NodeInfo n : members) {
            if (n.getHost().equals(self.getHost()) && n.getPort() == self.getPort()) {
                continue;
            }

            ManagedChannel channel = null;
            try {
                channel = ManagedChannelBuilder
                        .forAddress(n.getHost(), n.getPort())
                        .usePlaintext()
                        .build();

                FamilyServiceGrpc.FamilyServiceBlockingStub stub =
                        FamilyServiceGrpc.newBlockingStub(channel);

                stub.receiveChat(msg);

                System.out.printf("Broadcasted message to %s:%d%n", n.getHost(), n.getPort());

            } catch (Exception e) {
                System.err.printf("Failed to send to %s:%d (%s)%n",
                        n.getHost(), n.getPort(), e.getMessage());
            } finally {
                if (channel != null) channel.shutdownNow();
            }
        }
    }

    private static int findFreePort(int startPort) {
        int port = startPort;
        while (true) {
            try (ServerSocket ignored = new ServerSocket(port)) {
                return port;
            } catch (IOException e) {
                port++;
            }
        }
    }

    private static void discoverExistingNodes(String host,
                                              int selfPort,
                                              NodeRegistry registry,
                                              NodeInfo self) {

        for (int port = START_PORT; port < selfPort; port++) {
            ManagedChannel channel = null;
            try {
                channel = ManagedChannelBuilder
                        .forAddress(host, port)
                        .usePlaintext()
                        .build();

                FamilyServiceGrpc.FamilyServiceBlockingStub stub =
                        FamilyServiceGrpc.newBlockingStub(channel);

                FamilyView view = stub.join(self);
                registry.addAll(view.getMembersList());

                System.out.printf("Joined through %s:%d, family size now: %d%n",
                        host, port, registry.snapshot().size());

            } catch (Exception ignored) {
            } finally {
                if (channel != null) channel.shutdownNow();
            }
        }
    }

    private static void startFamilyPrinter(NodeRegistry registry, NodeInfo self) {
        ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();

        scheduler.scheduleAtFixedRate(() -> {
            List<NodeInfo> members = registry.snapshot();
            System.out.println("======================================");
            System.out.printf("Family at %s:%d (me)%n", self.getHost(), self.getPort());
            System.out.println("Time: " + LocalDateTime.now());
            System.out.println("Members:");

            for (NodeInfo n : members) {
                boolean isMe = n.getHost().equals(self.getHost()) && n.getPort() == self.getPort();
                System.out.printf(" - %s:%d%s%n",
                        n.getHost(),
                        n.getPort(),
                        isMe ? " (me)" : "");
            }
            System.out.println("======================================");
        }, 3, PRINT_INTERVAL_SECONDS, TimeUnit.SECONDS);
    }

    private static void startHealthChecker(NodeRegistry registry, NodeInfo self) {
        ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();

        scheduler.scheduleAtFixedRate(() -> {
            List<NodeInfo> members = registry.snapshot();

            for (NodeInfo n : members) {
                if (n.getHost().equals(self.getHost()) && n.getPort() == self.getPort()) {
                    continue;
                }

                ManagedChannel channel = null;
                try {
                    channel = ManagedChannelBuilder
                            .forAddress(n.getHost(), n.getPort())
                            .usePlaintext()
                            .build();

                    FamilyServiceGrpc.FamilyServiceBlockingStub stub =
                            FamilyServiceGrpc.newBlockingStub(channel);

                    stub.getFamily(Empty.newBuilder().build());

                } catch (Exception e) {
                    System.out.printf("Node %s:%d unreachable, removing from family%n",
                            n.getHost(), n.getPort());
                    registry.remove(n);
                } finally {
                    if (channel != null) {
                        channel.shutdownNow();
                    }
                }
            }

        }, 5, 10, TimeUnit.SECONDS);
    }

    private static void startLocalDiskCountPrinter(NodeInfo self, DiskStorage storage) {
        ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();

        scheduler.scheduleAtFixedRate(() -> {
            int count = storage.countMessages();
            System.out.printf("🗂️ Local disk count at %s:%d => %d messages%n",
                    self.getHost(), self.getPort(), count);
        }, 5, 10, TimeUnit.SECONDS);
    }


    private static void startClusterStatsPrinter(NodeRegistry registry, NodeInfo self) {
        ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();

        scheduler.scheduleAtFixedRate(() -> {
            //int leaderCount = diskStorage.countMessages();

            System.out.println("===== CLUSTER STATS =====");
            //System.out.printf("Leader %s:%d holds: %d messages%n",
                    //self.getHost(), self.getPort(), leaderCount);

            int totalMembersCount = 0;

            for (NodeInfo n : registry.snapshot()) {
                if (n.getHost().equals(self.getHost()) && n.getPort() == self.getPort()) continue;

                ManagedChannel channel = null;
                try {
                    channel = ManagedChannelBuilder
                            .forAddress(n.getHost(), n.getPort())
                            .usePlaintext()
                            .build();

                    MemberServiceGrpc.MemberServiceBlockingStub stub =
                            MemberServiceGrpc.newBlockingStub(channel);


                    int c = stub.withDeadlineAfter(1, TimeUnit.SECONDS)
                            .getLocalCount(com.hatokuse.grpc.CountRequest.newBuilder().build())
                            .getCount();

                    totalMembersCount += c;

                    System.out.printf("Member %s:%d holds: %d messages%n",
                            n.getHost(), n.getPort(), c);

                } catch (Exception e) {

                    registry.remove(n);
                } finally {
                    if (channel != null) channel.shutdownNow();
                }
            }

            System.out.printf("TOTAL: %d%n", totalMembersCount);
            System.out.println("=========================");

        }, 5, 10, TimeUnit.SECONDS);
    }


    private static List<NodeInfo> replicateToMembers(String command,
                                                     NodeRegistry registry,
                                                     NodeInfo self,
                                                     int tolerance) {
        String[] parts = command.trim().split("\\s+", 3);
        if (parts.length < 3) return List.of();

        int id;
        try { id = Integer.parseInt(parts[1]); }
        catch (NumberFormatException e) { return List.of(); }

        String content = parts[2];
        long ts = System.currentTimeMillis();

        List<NodeInfo> targets = pickReplicaMembers(id, registry.snapshot(), self, tolerance);
        List<NodeInfo> success = new java.util.ArrayList<>();

        for (NodeInfo n : targets) {
            ManagedChannel channel = null;
            try {
                channel = ManagedChannelBuilder
                        .forAddress(n.getHost(), n.getPort())
                        .usePlaintext()
                        .build();

                MemberServiceGrpc.MemberServiceBlockingStub stub =
                        MemberServiceGrpc.newBlockingStub(channel);

                MessageRequest req = MessageRequest.newBuilder()
                        .setId(id)
                        .setContent(content)
                        .setTimestamp(ts)
                        .build();

                MessageResponse res = stub.storeMessage(req);
                if (res.getSuccess()) success.add(n);

            } catch (Exception e) {
                System.err.printf("Replication failed to %s:%d (%s)%n",
                        n.getHost(), n.getPort(), e.getMessage());
            } finally {
                if (channel != null) channel.shutdownNow();
            }
        }

        return success;
    }

    private static String tryRetrieveFromMembers(String command,
                                                 NodeRegistry registry,
                                                 NodeInfo self) {
        String[] parts = command.trim().split("\\s+");
        if (parts.length < 2) return null;

        int id;
        try {
            id = Integer.parseInt(parts[1]);
        } catch (NumberFormatException e) {
            return null;
        }

        List<NodeInfo> targets = placement.get(id);
        if (targets == null || targets.isEmpty()) {
            targets = registry.snapshot();
        }

        for (NodeInfo n : targets) {
            if (n.getHost().equals(self.getHost()) && n.getPort() == self.getPort()) continue;

            ManagedChannel channel = null;
            try {
                channel = ManagedChannelBuilder
                        .forAddress(n.getHost(), n.getPort())
                        .usePlaintext()
                        .build();

                MemberServiceGrpc.MemberServiceBlockingStub stub =
                        MemberServiceGrpc.newBlockingStub(channel);

                RetrieveRequest req = RetrieveRequest.newBuilder()
                        .setId(id)
                        .build();

                RetrieveResponse res = stub.retrieveMessage(req);

                if (res.getFound()) {
                    System.out.printf("GET %d found at %s:%d%n", id, n.getHost(), n.getPort());
                    return res.getContent();
                }
            } catch (Exception e) {
                System.err.printf("GET fallback failed from %s:%d (%s)%n",
                        n.getHost(), n.getPort(), e.getMessage());

                System.out.printf("Node %s:%d unreachable during GET, removing from family%n",
                        n.getHost(), n.getPort());
                registry.remove(n);
            } finally {
                if (channel != null) channel.shutdownNow();
            }
        }
        return null;
    }

    private static List<NodeInfo> pickReplicaMembers(int messageId,
                                                     List<NodeInfo> allMembers,
                                                     NodeInfo self,
                                                     int k) {
        List<NodeInfo> others = allMembers.stream()
                .filter(n -> !(n.getHost().equals(self.getHost()) && n.getPort() == self.getPort()))
                .sorted((a, b) -> {
                    int c = a.getHost().compareTo(b.getHost());
                    return (c != 0) ? c : Integer.compare(a.getPort(), b.getPort());
                })
                .toList();

        if (k <= 0 || others.isEmpty()) return List.of();
        if (k >= others.size()) return others;

        int start = Math.floorMod(messageId, others.size());
        List<NodeInfo> picked = new java.util.ArrayList<>();
        for (int i = 0; i < k; i++) {
            picked.add(others.get((start + i) % others.size()));
        }
        return picked;
    }


    private static int readTolerance() {
        Path p = Paths.get("tolerance.conf");
        try {
            if (!Files.exists(p)) return 1;
            for (String line : Files.readAllLines(p)) {
                line = line.trim();
                if (line.startsWith("TOLERANCE=")) {
                    int v = Integer.parseInt(line.substring("TOLERANCE=".length()).trim());
                    if (v < 1) return 1;
                    if (v > 7) return 7;
                    return v;
                }
            }
        } catch (Exception ignored) {}
        return 1;
    }


    private static void startPlacementMapReportPrinter(NodeRegistry registry, NodeInfo self) {
        ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();

        scheduler.scheduleAtFixedRate(() -> {
            java.util.Map<String, Integer> counts = new java.util.HashMap<>();

            int totalMessagesTracked = 0;

            for (java.util.Map.Entry<Integer, List<NodeInfo>> e : placement.entrySet()) {
                totalMessagesTracked++;

                List<NodeInfo> holders = e.getValue();
                if (holders == null) continue;

                for (NodeInfo n : holders) {
                    String key = n.getHost() + ":" + n.getPort();
                    counts.put(key, counts.getOrDefault(key, 0) + 1);
                }
            }

            System.out.println("========== 📊 Placement Map Report (leader) ==========");
            System.out.printf("Leader: %s:%d | tracked message ids: %d | family size: %d%n",
                    self.getHost(), self.getPort(), totalMessagesTracked, registry.snapshot().size());


            java.util.List<NodeInfo> members = registry.snapshot();
            for (NodeInfo n : members) {
                String key = n.getHost() + ":" + n.getPort();
                int c = counts.getOrDefault(key, 0);
                System.out.printf(" - %s => %d placements%n", key, c);
            }


            String leaderKey = self.getHost() + ":" + self.getPort();
            if (counts.containsKey(leaderKey) && members.stream().noneMatch(m -> (m.getHost()+":"+m.getPort()).equals(leaderKey))) {
                System.out.printf(" - %s => %d placements%n", leaderKey, counts.getOrDefault(leaderKey, 0));
            }


            System.out.println("======================================================");
        }, 7, 10, TimeUnit.SECONDS);
    }


    private static void rollbackReplicas(int id, List<NodeInfo> writtenNodes) {
        for (NodeInfo n : writtenNodes) {
            ManagedChannel channel = null;
            try {
                channel = ManagedChannelBuilder
                        .forAddress(n.getHost(), n.getPort())
                        .usePlaintext()
                        .build();

                MemberServiceGrpc.MemberServiceBlockingStub stub =
                        MemberServiceGrpc.newBlockingStub(channel);

                DeleteResponse res = stub.deleteMessage(
                        DeleteRequest.newBuilder().setId(id).build()
                );

                System.out.printf("ROLLBACK delete %d at %s:%d -> %s%n",
                        id, n.getHost(), n.getPort(), res.getMessage());

            } catch (Exception e) {
                System.err.printf("ROLLBACK failed at %s:%d (%s)%n",
                        n.getHost(), n.getPort(), e.getMessage());
            } finally {
                if (channel != null) channel.shutdownNow();
            }
        }
    }

}