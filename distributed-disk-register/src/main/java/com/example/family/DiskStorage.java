

package com.example.family;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.*;
import java.util.stream.Stream;

public class DiskStorage {

    private final Path baseDir;


    public DiskStorage(String nodeId) {
        this.baseDir = Paths.get("messages", nodeId);
        try {
            Files.createDirectories(baseDir);
        } catch (IOException e) {
            throw new RuntimeException("Cannot create base dir: " + baseDir, e);
        }
    }

    public void write(int id, String message) throws IOException {
        Path path = baseDir.resolve(id + ".msg");
        try (BufferedWriter writer = Files.newBufferedWriter(path)) {
            writer.write(message);
        }
    }

    public String read(int id) throws IOException {
        Path path = baseDir.resolve(id + ".msg");
        if (!Files.exists(path)) return null;
        return Files.readString(path);
    }

    public void delete(int id) throws IOException {
        Path path = baseDir.resolve(id + ".msg");
        Files.deleteIfExists(path);
    }

    public int countMessages() {
        try (Stream<Path> s = Files.list(baseDir)) {
            return (int) s.filter(p -> p.getFileName().toString().endsWith(".msg")).count();
        } catch (IOException e) {
            return -1;
        }
    }

    public Path getBaseDir() {
        return baseDir;
    }
}
