package com.butreik.ipcounter.counters;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

public class HashSetCounter implements Counter {

    @Override
    public long count(String fileName) throws IOException {
        Set<String> set = new HashSet<>();

        try (BufferedReader reader = new BufferedReader(new FileReader(fileName))) {
            String line;
            while ((line = reader.readLine()) != null) {
                set.add(line);
            }
        }
        return set.size();
    }
}
