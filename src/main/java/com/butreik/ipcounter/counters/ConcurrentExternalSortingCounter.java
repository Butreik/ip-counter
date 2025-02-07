package com.butreik.ipcounter.counters;

import static com.butreik.ipcounter.Utils.NEXT_LINE;
import static com.butreik.ipcounter.Utils.countLines;
import static com.butreik.ipcounter.Utils.mergeSortedFiles;
import static com.butreik.ipcounter.Utils.sortAndWriteChunkToFile;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class ConcurrentExternalSortingCounter implements Counter {
    private static final int MIN_CHUNK_BYTES_SIZE = 100 * 1024 * 1024;
    private static final int MAX_FILE_COUNT = 1024;

    private final int countOfSorters;
    private final int countOfChunksInOneSorter;

    public ConcurrentExternalSortingCounter(int countOfSorters) {
        this.countOfSorters = countOfSorters;
        this.countOfChunksInOneSorter = MAX_FILE_COUNT / countOfSorters;
    }

    @Override
    public long count(String fileName) throws IOException, InterruptedException {
        try (ExecutorService executor = Executors.newFixedThreadPool(countOfSorters)) {
            File inputFile = new File(fileName);
            long fileSize = inputFile.length();
            long segmentSize = fileSize / countOfSorters;
            long chunkBytesSize = Math.max(segmentSize / countOfChunksInOneSorter, MIN_CHUNK_BYTES_SIZE);

            List<Future<File>> futures = new ArrayList<>();
            for (int i = 0; i < countOfSorters; i++) {
                long segmentStart = i * segmentSize;
                long segmentEnd = (i + 1) * segmentSize;
                futures.add(executor.submit(
                    () -> new ExternalSorter(fileName, segmentStart, segmentEnd, chunkBytesSize).sort()
                ));
            }

            List<File> sortedFiles = new ArrayList<>(countOfSorters);
            for (Future<File> future : futures) {
                sortedFiles.add(future.get());
            }

            File resultFile = sortedFiles.size() == 1 ? sortedFiles.getFirst() : mergeSortedFiles(sortedFiles);

            return countLines(resultFile);

        } catch (ExecutionException e) {
            throw new RuntimeException(e);
        }
    }

    public static class ExternalSorter {
        private final String fileName;
        private final long start;
        private final long end;
        private final long chunkBytesSize;

        public ExternalSorter(String fileName, long start, long end, long chunkBytesSize) {
            this.fileName = fileName;
            this.start = start;
            this.end = end;
            this.chunkBytesSize = chunkBytesSize;
        }

        public File sort() {
            try {
                List<File> sortedFiles = splitAndSortChunks();
                return mergeSortedFiles(sortedFiles);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

        private List<File> splitAndSortChunks() throws IOException {
            List<String> chunk = new ArrayList<>();
            List<File> sortedFiles = new ArrayList<>();

            try (BufferedInputStream bis = new BufferedInputStream(new FileInputStream(fileName))) {
                long currentPosition = start;
                long endChunk = start + chunkBytesSize;
                bis.skipNBytes(start);
                if (start > 0) {
                    int ch = bis.read();
                    while (ch != -1 && ch != NEXT_LINE) {
                        ch = bis.read();
                        currentPosition++;
                    }
                }

                ByteArrayOutputStream buffer = new ByteArrayOutputStream();
                int b;
                while ((b = bis.read()) != -1) {
                    currentPosition++;
                    if (b == NEXT_LINE) {
                        chunk.add(buffer.toString());
                        buffer = new ByteArrayOutputStream();
                        if (currentPosition >= endChunk) {
                            sortedFiles.add(sortAndWriteChunkToFile(chunk));
                            chunk.clear();
                            endChunk += chunkBytesSize;
                        }
                        if (currentPosition > end) {
                            break;
                        }
                    } else {
                        buffer.write(b);
                    }
                }
                if (buffer.size() > 0) {
                    chunk.add(buffer.toString());
                }
            }

            // last chunk
            if (!chunk.isEmpty()) {
                sortedFiles.add(sortAndWriteChunkToFile(chunk));
            }
            return sortedFiles;
        }
    }
}
