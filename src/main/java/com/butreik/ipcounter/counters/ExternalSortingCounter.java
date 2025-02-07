package com.butreik.ipcounter.counters;

import static com.butreik.ipcounter.Utils.sortAndWriteChunkToFile;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.PriorityQueue;

public class ExternalSortingCounter implements Counter {

    private final int chunkSize;

    public ExternalSortingCounter(int chunkSize) {
        this.chunkSize = chunkSize;
    }


    @Override
    public long count(String fileName) throws IOException {
        List<File> sortedChunks = splitAndSortChunks(fileName);
        return mergeSortedChunks(sortedChunks);
    }

    private List<File> splitAndSortChunks(String inputFile) throws IOException {
        List<File> sortedChunks = new ArrayList<>();
        List<String> chunk = new ArrayList<>(chunkSize);

        try (BufferedReader reader = new BufferedReader(new FileReader(inputFile))) {
            String line;
            while ((line = reader.readLine()) != null) {
                chunk.add(line);
                if (chunk.size() >= chunkSize) {
                    sortedChunks.add(sortAndWriteChunkToFile(chunk));
                    chunk.clear();
                }
            }
        }

        // last chunk
        if (!chunk.isEmpty()) {
            sortedChunks.add(sortAndWriteChunkToFile(chunk));
        }
        return sortedChunks;
    }

    private static long mergeSortedChunks(List<File> sortedChunks) throws IOException {
        PriorityQueue<Cursor> pq = new PriorityQueue<>();

        List<BufferedReader> readers = new ArrayList<>();
        for (File file : sortedChunks) {
            BufferedReader reader = new BufferedReader(new FileReader(file));
            readers.add(reader);
            String line = reader.readLine();
            if (line != null) {
                pq.add(new Cursor(line, reader));
            }
        }

        String prevIP = null;
        long result = 0;
        while (!pq.isEmpty()) {
            Cursor cursor = pq.poll();
            if (!cursor.line.equals(prevIP)) { // new unique ip
                result++;
                prevIP = cursor.line;
            }

            String nextLine = cursor.reader.readLine();
            if (nextLine != null) {
                pq.add(new Cursor(nextLine, cursor.reader));
            }
        }

        for (BufferedReader reader : readers) {
            reader.close();
        }
        return result;
    }

    private record Cursor(String line, BufferedReader reader) implements Comparable<Cursor> {

        @Override
        public int compareTo(Cursor other) {
            return this.line.compareTo(other.line);
        }
    }
}
