package com.butreik.ipcounter.utils;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.time.Duration;
import java.util.HashSet;
import java.util.Random;
import java.util.Set;

public class IpFileGenerator {

    public enum TestFiles {
        S("test_ip_3m_10m.txt", 3_000_000, 10_000_000),
        M("test_ip_100m_300m.txt", 100_000_000, 300_000_000),
        L("test_ip_100m_2g.txt", 100_000_000, 2_000_000_000),
        XL("ip_addresses", 1_000_000_000, 8_000_000_000L);
        public final String fileName;
        public final int uniqueIPCount;
        public final long totalLines;

        TestFiles(String fileName, int uniqueIPCount, long totalLines) {
            this.fileName = fileName;
            this.uniqueIPCount = uniqueIPCount;
            this.totalLines = totalLines;
        }
    }

    public static void generateTestFilesIfNeeded() {
        for (TestFiles testFile : TestFiles.values()) {
            if (fileNotExist(testFile.fileName)) {
                generateFile(testFile.fileName, testFile.uniqueIPCount, testFile.totalLines);
            }
        }
    }

    private static void generateFile(String filePath, int uniqueIPCount, long totalLines) {

        long start = System.currentTimeMillis();
        long lastLog = start;
        System.out.println("Start generation file " + filePath);

        Set<String> uniqueIPs = new HashSet<>(uniqueIPCount);
        Random random = new Random();
        while (uniqueIPs.size() < uniqueIPCount) {
            String ip = generateRandomIP(random);
            uniqueIPs.add(ip);
            if (System.currentTimeMillis() - lastLog > 2000) {
                lastLog = System.currentTimeMillis();
                System.out.println("Generate ips... ");
            }
        }
        String[] ipArray = uniqueIPs.toArray(new String[0]);

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filePath))) {
            for (int i = 0; i < uniqueIPCount; i++) {
                writer.write(ipArray[i]);
                writer.newLine();
                if (System.currentTimeMillis() - lastLog > 2000) {
                    lastLog = System.currentTimeMillis();
                    System.out.println("Write unique to file... ");
                }
            }

            for (long i = uniqueIPCount; i < totalLines; i++) {
                writer.write(ipArray[random.nextInt(uniqueIPCount)]);
                writer.newLine();
                if (System.currentTimeMillis() - lastLog > 3000) {
                    lastLog = System.currentTimeMillis();
                    System.out.println("Write additional to file... ");
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println("Finish: " + Duration.ofMillis(System.currentTimeMillis() - start));
    }

    private static String generateRandomIP(Random random) {
        return random.nextInt(256) + "." +
            random.nextInt(256) + "." +
            random.nextInt(256) + "." +
            random.nextInt(256);
    }

    public static boolean fileNotExist(String fileName) {
        File file = new File(fileName);
        return !file.exists() || !file.isFile();
    }
}
