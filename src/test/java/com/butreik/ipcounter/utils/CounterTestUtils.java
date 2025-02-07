package com.butreik.ipcounter.utils;

import static com.butreik.ipcounter.Utils.sortWithMonitoring;
import static org.junit.jupiter.api.Assertions.assertEquals;

import com.butreik.ipcounter.counters.Counter;

public class CounterTestUtils {
    public static void testCounter(Counter counter, IpFileGenerator.TestFiles testFile) throws Exception {
        long count = sortWithMonitoring(testFile.fileName, counter);
        assertEquals(testFile.uniqueIPCount, count);
    }
}
