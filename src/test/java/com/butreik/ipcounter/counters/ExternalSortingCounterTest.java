package com.butreik.ipcounter.counters;

import static com.butreik.ipcounter.utils.CounterTestUtils.testCounter;

import com.butreik.ipcounter.utils.IpFileGenerator;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

public class ExternalSortingCounterTest {

    @BeforeAll
    static void setUp() {
        IpFileGenerator.generateTestFilesIfNeeded();
    }

    @Test
    public void sTest() throws Exception {
        Counter counter = new ExternalSortingCounter(1_000_000);
        testCounter(counter, IpFileGenerator.TestFiles.S);
    }

    @Test
    @Disabled
    public void mTest() throws Exception {
        Counter counter = new ExternalSortingCounter(10_000_000);
        testCounter(counter, IpFileGenerator.TestFiles.M);
    }

    @Test
    @Disabled
    public void lTest() throws Exception {
        Counter counter = new ExternalSortingCounter(10_000_000);
        testCounter(counter, IpFileGenerator.TestFiles.L);
    }

    @Test
    @Disabled
    public void xlTest() throws Exception {
        Counter counter = new ExternalSortingCounter(10_000_000);
        testCounter(counter, IpFileGenerator.TestFiles.XL);
    }
}
