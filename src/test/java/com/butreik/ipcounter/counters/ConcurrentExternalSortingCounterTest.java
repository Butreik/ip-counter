package com.butreik.ipcounter.counters;

import static com.butreik.ipcounter.utils.CounterTestUtils.testCounter;

import com.butreik.ipcounter.utils.IpFileGenerator;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

public class ConcurrentExternalSortingCounterTest {

    @BeforeAll
    static void setUp() {
        IpFileGenerator.generateTestFilesIfNeeded();
    }

    @Test
    public void sTest() throws Exception {
        Counter counter = new ConcurrentExternalSortingCounter(1);
        testCounter(counter, IpFileGenerator.TestFiles.S);
    }

    @Test
    @Disabled
    public void s4Test() throws Exception {
        Counter counter = new ConcurrentExternalSortingCounter(4);
        testCounter(counter, IpFileGenerator.TestFiles.S);
    }

    @Test
    @Disabled
    public void m4Test() throws Exception {
        Counter counter = new ConcurrentExternalSortingCounter(4);
        testCounter(counter, IpFileGenerator.TestFiles.M);
    }

    @Test
    @Disabled
    public void m8Test() throws Exception {
        Counter counter = new ConcurrentExternalSortingCounter(8);
        testCounter(counter, IpFileGenerator.TestFiles.M);
    }

    @Test
    @Disabled
    public void m16Test() throws Exception {
        Counter counter = new ConcurrentExternalSortingCounter(16);
        testCounter(counter, IpFileGenerator.TestFiles.M);
    }

    @Test
    @Disabled
    public void l4Test() throws Exception {
        Counter counter = new ConcurrentExternalSortingCounter(4);
        testCounter(counter, IpFileGenerator.TestFiles.L);
    }

    @Test
    @Disabled
    public void l8Test() throws Exception {
        Counter counter = new ConcurrentExternalSortingCounter(8);
        testCounter(counter, IpFileGenerator.TestFiles.L);
    }

    @Test
    @Disabled
    public void l16Test() throws Exception {
        Counter counter = new ConcurrentExternalSortingCounter(16);
        testCounter(counter, IpFileGenerator.TestFiles.L);
    }

    @Test
    @Disabled
    public void xl4Test() throws Exception {
        Counter counter = new ConcurrentExternalSortingCounter(4);
        testCounter(counter, IpFileGenerator.TestFiles.XL);
    }

    @Test
    @Disabled
    public void xl8Test() throws Exception {
        Counter counter = new ConcurrentExternalSortingCounter(8);
        testCounter(counter, IpFileGenerator.TestFiles.XL);
    }

    @Test
    @Disabled
    public void xl16Test() throws Exception {
        Counter counter = new ConcurrentExternalSortingCounter(16);
        testCounter(counter, IpFileGenerator.TestFiles.XL);
    }
}
