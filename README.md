IPv4 Address Counter
=====

Problem statement
-----
You have a simple text file with IPv4 addresses. One line is one address, line by line:

```
145.67.23.4
8.34.5.23
89.54.3.124
89.54.3.124
3.45.71.5
...
```

The file is unlimited in size and can occupy tens and hundreds of gigabytes.

You should calculate the number of __unique addresses__ in this file using as little memory and time as possible.
There is a "naive" algorithm for solving this problem (read line by line, put lines into HashSet).
It's better if your implementation is more complicated and faster than this naive algorithm.

Solutions
-----

#### Build
```
mvn clean install
```

#### Run
```
java -jar ./target/ip-counter-jar-with-dependencies.jar test_ip_3m_10m.txt
```

### [HashSetCounter.java](src/main/java/com/butreik/ipcounter/counters/HashSetCounter.java)
Using a HashSet for uniqueness. Each line (IPv4 address) is stored in a `HashSet`.

### [BitSetCounter.java](src/main/java/com/butreik/ipcounter/counters/BitSetCounter.java)
This implementation improves memory efficiency by using a BitSet instead of a HashSet

### [ExternalSortingCounter.java](src/main/java/com/butreik/ipcounter/counters/ExternalSortingCounter.java)
This implementation is optimized for handling extremely large files using external sorting:
- Splitting into sorted chunks – The file is processed in chunks, each chunk is sorted in memory and written to a temporary file.
- Merging sorted chunks and counting unique IPs – All chunks are merged using a priority queue (k-way merge), removing duplicates efficiently.

### [ConcurrentExternalSortingCounter.java](src/main/java/com/butreik/ipcounter/counters/ConcurrentExternalSortingCounter.java)
Similar to ExternalSortingCounter, but the file is split into parts and each part is processed in a separate thread.

Algorithm comparison
-----
N - total count IP
M - count unique IP
C - chunk size in external sorting

|                 | Memory     | Time Complexity |
|-----------------|------------|-----------------|
| HashSet         | O(M)       | O(N)            |
| BitSet          | O(M)       | O(N)            |
| ExternalSorting | O(C + N/C) | O(NlogN)        |



## Test data
| File name             | Size                                                      | Source                                                                                 |
|-----------------------|-----------------------------------------------------------|----------------------------------------------------------------------------------------|
| test_ip_100m_300m.txt | 100 000 000 unique IP, 300 000 000 total count, 4GB       | [IpFileGenerator.java](src/test/java/com/butreik/ipcounter/utils/IpFileGenerator.java) |
| test_ip_100m_2g.txt   | 100 000 000 unique IP, 2 000 000 000 total count, 28GB    | [IpFileGenerator.java](src/test/java/com/butreik/ipcounter/utils/IpFileGenerator.java) |
| ip_addresses          | 1 000 000 000 unique IP, 8 000 000 000 total count, 114GB | [file](https://ecwid-vgv-storage.s3.eu-central-1.amazonaws.com/ip_addresses.zip)       |

## Benchmarks

**Note:** Benchmark results depend on the environment in which tests are run. However, they still provide a valid basis for comparing different algorithms.

### Time / Memory:

|                                    | test_ip_100m_300m.txt | test_ip_100m_2g.txt | ip_addresses (1g_8g) |
|------------------------------------|-----------------------|---------------------|----------------------|
| HashSet                            | 54s / 11GB            | 7m12s / 11GB        | -                    |
| BitSet                             | 46s / 1GB             | 5m56s / 1GB         | 9m11s / 1GB          | 
| ExternalSorting (C = 10 000 000)   | 2m20s / 1GB           | 22m52s / 1.5GB      | -                    | 
| ConcurrentExternalSorting (N = 4)  | 1m4s / 4GB            | 6m45s / 3GB         | 19m15s / 4GB         | 
| ConcurrentExternalSorting (N = 8)  | 50s / 8GB             | 4m24s / 8GB         | 18m12s / 10GB        | 
| ConcurrentExternalSorting (N = 16) | 52s / 10GB            | 4m17s / 11GB        | 17m16s / 15GB        | 


Conclusions
-----
For finding unique IP addresses, external sorting offers little advantage on modern hardware compared to leveraging 
the structured nature of IPv4. Since IPv4 addresses are fixed 32-bit values, efficient in-memory techniques 
like bitmaps outperform general external sorting. 
With optimized algorithms and sufficient RAM, processing can be faster without disk-based operations, 
making external sorting unnecessary for this task.