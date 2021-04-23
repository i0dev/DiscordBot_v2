package com.i0dev.utility;

import com.i0dev.InitilizeBot;
import com.i0dev.utility.util.FormatUtil;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public final class LogsFile {
    private static final List<String> toLog = new LinkedList<>();

    public static void initPoints() {
        CompletableFuture.runAsync(() -> {
            while (true) {
                if (!toLog.isEmpty()) {
                    List<String> cache = new LinkedList<>(toLog);
                    try {
                        FileWriter fw = new FileWriter(InitilizeBot.get().getPointLogPath(), true);
                        BufferedWriter bw = new BufferedWriter(fw);
                        PrintWriter out = new PrintWriter(bw);

                        for (String line : cache) {
                            out.println(line);
                        }
                        bw.close();
                        out.close();
                        fw.close();

                    } catch (IOException ex) {
                        ex.printStackTrace();

                    }
                    toLog.removeAll(cache);
                }
                try {
                    Thread.sleep(5000);
                } catch (InterruptedException ignored) {
                }
            }
        });
    }

    public static void logPoints(String message) {
        System.out.println(message);
        toLog.add("[LOG] " + FormatUtil.formatDate(System.currentTimeMillis()) + " : " + message);
    }
}