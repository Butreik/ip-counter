package com.butreik.ipcounter.counters;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.BitSet;

public class BitSetCounter implements Counter {

    public long count(String fileName) throws IOException {
        BitSet[] bitSets = new BitSet[2];
        bitSets[0] = new BitSet(Integer.MAX_VALUE);
        bitSets[1] = new BitSet(Integer.MAX_VALUE);

        try (BufferedReader reader = new BufferedReader(new FileReader(fileName))) {
            String line;
            while ((line = reader.readLine()) != null) {
                int intIp = ipToIntIp(line);
                if (intIp < 0) {
                    bitSets[0].set(Math.abs(intIp + 1));
                } else {
                    bitSets[1].set(intIp);
                }
            }
        }
        return bitSets[0].cardinality() + bitSets[1].cardinality();
    }

    private int ipToIntIp(String ip) {
        String[] parts = ip.split("\\.");
        return (Integer.parseInt(parts[0]) << 24) |
            (Integer.parseInt(parts[1]) << 16) |
            (Integer.parseInt(parts[2]) << 8) |
            Integer.parseInt(parts[3]);
    }
}
