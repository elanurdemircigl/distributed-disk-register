package com.example.family;

import java.util.HashMap;
import java.util.Map;

public class CommandParser {

    // Verileri geçici olarak hafızada tutmak için bir Map
    private Map<Integer, String> database = new HashMap<>();

    public String parseAndExecute(String commandLine) {
        // Gelen komutu boşluklara göre ayır
        // Örn: "SET 100 Merhaba" -> ["SET", "100", "Merhaba"]
        String[] parts = commandLine.trim().split("\\s+", 3);

        if (parts.length == 0) {
            return "ERROR: Bos komut";
        }

        String command = parts[0].toUpperCase();

        // --- SET KOMUTU ---
        if (command.equals("SET")) {
            if (parts.length < 3) {
                return "ERROR: Eksik parametre. Kullanim: SET <id> <mesaj>";
            }
            try {
                int id = Integer.parseInt(parts[1]);
                String message = parts[2];
                database.put(id, message);
                return "OK";
            } catch (NumberFormatException e) {
                return "ERROR: ID bir sayi olmali.";
            }
        }

        // --- GET KOMUTU ---
        else if (command.equals("GET")) {
            if (parts.length < 2) {
                return "ERROR: Eksik parametre. Kullanim: GET <id>";
            }
            try {
                int id = Integer.parseInt(parts[1]);
                String message = database.get(id);

                if (message != null) {
                    return message;
                } else {
                    return "NOT_FOUND";
                }
            } catch (NumberFormatException e) {
                return "ERROR: ID bir sayi olmali.";
            }
        }

        return "ERROR: Bilinmeyen komut";
    }

    // Test etmek için main metodu
    public static void main(String[] args) {
        CommandParser parser = new CommandParser();
        System.out.println("Test 1 (SET): " + parser.parseAndExecute("SET 100 SistemOdevi"));
        System.out.println("Test 2 (GET): " + parser.parseAndExecute("GET 100"));
    }
}