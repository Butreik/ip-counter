package com.butreik.ipcounter;

import static com.butreik.ipcounter.Utils.sortWithMonitoring;

import com.butreik.ipcounter.counters.BitSetCounter;
import com.butreik.ipcounter.counters.Counter;
import java.util.concurrent.ExecutionException;

public class SortingApp {
    public static void main(String[] args) throws ExecutionException, InterruptedException {
        if (args.length < 1) {
            System.out.println("Usage: java -jar ./target/ip-counter-jar-with-dependencies.jar test_ip_5_10.txt <fileName>");
            return;
        }

        String fileName = args[0];
        Counter counter = new BitSetCounter();

        long uniqueCount = sortWithMonitoring(fileName, counter);

        System.out.println("Unique IP count: " + uniqueCount);
    }
}