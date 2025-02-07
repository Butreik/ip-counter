package com.butreik.ipcounter.counters;

public interface Counter {
    long count(String fileName) throws Exception;
}
