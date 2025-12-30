package com.example.family;

public class CommandParser {

    private final DiskStorage storage;

    public CommandParser(DiskStorage storage) {
        this.storage = storage;
    }

    public String parseAndExecute(String commandLine) {
        String[] parts = commandLine.trim().split("\\s+", 3);
        if (parts.length == 0) return "ERROR";

        String cmd = parts[0].toUpperCase();

        if (cmd.equals("SET")) {
            if (parts.length < 3) return "ERROR";
            try {
                int id = Integer.parseInt(parts[1]);
                storage.write(id, parts[2]);
                return "OK";
            } catch (Exception e) {
                return "ERROR";
            }
        }

        if (cmd.equals("GET")) {
            if (parts.length < 2) return "ERROR";
            try {
                int id = Integer.parseInt(parts[1]);
                String msg = storage.read(id);
                return (msg == null) ? "NOT_FOUND" : msg;
            } catch (Exception e) {
                return "ERROR";
            }
        }

        return "ERROR";
    }
}
