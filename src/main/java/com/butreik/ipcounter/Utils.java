package com.butreik.ipcounter;

import com.butreik.ipcounter.counters.Counter;
import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.PriorityQueue;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

public class Utils {

    public static final int NEXT_LINE = '\n';

    public static long sortWithMonitoring(String fileName, Counter counter) throws ExecutionException, InterruptedException {
        long startTime = System.currentTimeMillis();

        Future<Long> future = CompletableFuture.supplyAsync(() -> {
            try {
                return counter.count(fileName);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });

        while (!future.isDone()) {
            printMemoryUsage();
            System.out.println("Processing... Please wait. " + printMemoryUsage());
            Thread.sleep(2000);
        }

        long result = future.get();
        System.out.println("Total time: " + Duration.ofMillis(System.currentTimeMillis() - startTime));
        return result;
    }

    private static String printMemoryUsage() {
        Runtime runtime = Runtime.getRuntime();
        long totalMemory = runtime.totalMemory() / (1024 * 1024);
        long freeMemory = runtime.freeMemory() / (1024 * 1024);
        long usedMemory = totalMemory - freeMemory;

        return "Memory Usage: " + usedMemory + " MB / " + totalMemory + " MB";
    }

    public static File sortAndWriteChunkToFile(List<String> chunk) throws IOException {
        chunk.sort(String::compareTo);
        File tempFile = File.createTempFile("sorted_chunk_", ".txt");
        tempFile.deleteOnExit();

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(tempFile))) {
            for (String ip : chunk) {
                writer.write(ip);
                writer.newLine();
            }
        }
        return tempFile;
    }

    public static File mergeSortedFiles(List<File> sortedFiles) throws IOException {
        if (sortedFiles.size() == 1) {
            return sortedFiles.getFirst();
        }
        PriorityQueue<Cursor> pq = new PriorityQueue<>();

        List<BufferedReader> readers = new ArrayList<>();
        for (File file : sortedFiles) {
            BufferedReader reader = new BufferedReader(new FileReader(file));
            readers.add(reader);
            String line = reader.readLine();
            if (line != null) {
                pq.add(new Cursor(line, reader));
            }
        }

        File outputFile = File.createTempFile("sorted_chunk_", ".txt");
        outputFile.deleteOnExit();
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(outputFile))) {
            String prevIP = null;
            while (!pq.isEmpty()) {
                Cursor cursor = pq.poll();
                if (!cursor.line.equals(prevIP)) {
                    writer.write(cursor.line);
                    writer.newLine();
                    prevIP = cursor.line;
                }

                String nextLine = cursor.reader.readLine();
                if (nextLine != null) {
                    pq.add(new Cursor(nextLine, cursor.reader));
                }
            }
        }
        for (BufferedReader reader : readers) {
            reader.close();
        }
        for (File file : sortedFiles) {
            //noinspection ResultOfMethodCallIgnored
            file.delete();
        }
        return outputFile;
    }

    public static long countLines(File file) throws IOException {
        try (BufferedInputStream bis = new BufferedInputStream(new FileInputStream(file))) {
            byte[] buffer = new byte[8192];
            long lineCount = 0;
            int bytesRead;

            while ((bytesRead = bis.read(buffer)) != -1) {
                for (int i = 0; i < bytesRead; i++) {
                    if (buffer[i] == '\n') {
                        lineCount++;
                    }
                }
            }
            return lineCount;
        }
    }

    private record Cursor(String line, BufferedReader reader) implements Comparable<Cursor> {

        @Override
        public int compareTo(Cursor other) {
            return this.line.compareTo(other.line);
        }
    }

}
