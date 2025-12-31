package com.example.family;

import java.io.*;
import java.nio.file.*;

public class DiskStorage {

    private static final String BASE_DIR = "messages";

    public DiskStorage() {
        File dir = new File(BASE_DIR);
        if (!dir.exists()) dir.mkdirs();
    }

    public void write(int id, String message) throws IOException {
        Path path = Paths.get(BASE_DIR, id + ".msg");
        try (BufferedWriter writer = Files.newBufferedWriter(path)) {
            writer.write(message);
        }
    }

    public String read(int id) throws IOException {
        Path path = Paths.get(BASE_DIR, id + ".msg");
        if (!Files.exists(path)) return null;
        return Files.readString(path);
    }

    public int countMessages() {
        try {
            Path dir = Paths.get(BASE_DIR);
            if (!Files.exists(dir)) return 0;
            try (var stream = Files.list(dir)) {
                return (int) stream.filter(p -> p.getFileName().toString().endsWith(".msg")).count();
            }
        } catch (Exception e) {
            return 0;
        }
    }

}
