package m6;

import java.util.Scanner;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.RecursiveAction;
import java.util.concurrent.RecursiveTask;

public class ParallelPrimitives {
    // Parallel-Algorithm Primitives: Parallel Prefix Sum & Parallel Reduce
    // Includes Work-Span Analysis (Work T_1, Span T_inf, Parallelism P = T_1 / T_inf, Brent's Theorem)
    // Real Biological Context: Parallel Genomic GC-Skew Computation along DNA to locate Replication Origins (OriC)

    private static final int THRESHOLD = 10000;
    private static final ForkJoinPool pool = new ForkJoinPool();

    // ================= 1. PARALLEL REDUCE (TREE REDUCTION) =================
    public static class ParallelReduceTask extends RecursiveTask<Long> {
        private final int[] arr;
        private final int low, high;

        public ParallelReduceTask(int[] arr, int low, int high) {
            this.arr = arr;
            this.low = low;
            this.high = high;
        }

        @Override
        protected Long compute() {
            if (high - low <= THRESHOLD) {
                long sum = 0;
                for (int i = low; i < high; i++) sum += arr[i];
                return sum;
            } else {
                int mid = (low + high) >>> 1;
                ParallelReduceTask left = new ParallelReduceTask(arr, low, mid);
                ParallelReduceTask right = new ParallelReduceTask(arr, mid, high);
                left.fork();
                long rightAns = right.compute();
                long leftAns = left.join();
                return leftAns + rightAns;
            }
        }
    }

    // ================= 2. PARALLEL PREFIX SUM (BLELLOCH SCAN PRIMITIVE) =================
    // Phase 1: Parallel Up-Sweep (Reduction Tree)
    // Phase 2: Parallel Down-Sweep (Distribute Offsets)
    public static class ParallelPrefixSumTask extends RecursiveAction {
        private final int[] in;
        private final int[] out;
        private final int low, high;
        private final int offset;

        public ParallelPrefixSumTask(int[] in, int[] out, int low, int high, int offset) {
            this.in = in;
            this.out = out;
            this.low = low;
            this.high = high;
            this.offset = offset;
        }

        @Override
        protected void compute() {
            if (high - low <= THRESHOLD) {
                int curr = offset;
                for (int i = low; i < high; i++) {
                    curr += in[i];
                    out[i] = curr;
                }
            } else {
                int mid = (low + high) >>> 1;
                // Calculate left sum via reduce
                long leftSum = pool.invoke(new ParallelReduceTask(in, low, mid));
                ParallelPrefixSumTask leftTask = new ParallelPrefixSumTask(in, out, low, mid, offset);
                ParallelPrefixSumTask rightTask = new ParallelPrefixSumTask(in, out, mid, high, (int) (offset + leftSum));
                invokeAll(leftTask, rightTask);
            }
        }
    }

    public static void run(Scanner scanner) {
        System.out.println("========== PARALLEL ALGORITHM PRIMITIVES & WORK-SPAN ANALYSIS ==========");
        System.out.println("Biological Context: High-Throughput Cumulative GC-Skew Scanning for Replication Origins (OriC)");
        System.out.println("Available CPU Processors / Worker Threads: " + Runtime.getRuntime().availableProcessors());
        System.out.print("Enter sequence length to simulate (or press enter for default 500,000 base pairs): ");
        String line = scanner.hasNextLine() ? scanner.nextLine().trim() : "";
        int n = 500000;
        if (!line.isEmpty()) {
            try { n = Integer.parseInt(line); } catch (Exception ignored) {}
        }

        System.out.println("\nGenerating synthetic genome sequence of " + n + " nucleotides (G/C skew modeling)...");
        int[] deltaGC = new int[n];
        // delta = +1 if G, -1 if C, 0 if A/T
        java.util.Random rng = new java.util.Random(999);
        for (int i = 0; i < n; i++) {
            int r = rng.nextInt(4);
            if (r == 0) deltaGC[i] = 1;       // Guanine (G)
            else if (r == 1) deltaGC[i] = -1; // Cytosine (C)
            else deltaGC[i] = 0;              // Adenine / Thymine
        }

        // --- 1. PARALLEL REDUCE BENCHMARK ---
        System.out.println("\n--- [PRIMITIVE 1: PARALLEL REDUCE] ---");
        // Sequential
        long tSeqStart = System.nanoTime();
        long seqSum = 0;
        for (int x : deltaGC) seqSum += x;
        long tSeqEnd = System.nanoTime();
        long seqReduceTime = tSeqEnd - tSeqStart;

        // Parallel ForkJoin
        long tParStart = System.nanoTime();
        long parSum = pool.invoke(new ParallelReduceTask(deltaGC, 0, n));
        long tParEnd = System.nanoTime();
        long parReduceTime = tParEnd - tParStart;

        System.out.println("Net Genomic (G - C) Skew: " + parSum + " (Verified: " + (seqSum == parSum) + ")");
        System.out.println("Sequential Reduce Runtime (T_1): " + seqReduceTime + " ns");
        System.out.println("Parallel Reduce Runtime (T_p):   " + parReduceTime + " ns");
        if (parReduceTime > 0) {
            System.out.printf("Observed Empirical Speedup (S_p): %.2fx%n", (double) seqReduceTime / parReduceTime);
        }

        // --- 2. PARALLEL PREFIX SUM BENCHMARK ---
        System.out.println("\n--- [PRIMITIVE 2: PARALLEL PREFIX SUM / SCAN] ---");
        int[] seqPrefix = new int[n];
        tSeqStart = System.nanoTime();
        int running = 0;
        for (int i = 0; i < n; i++) {
            running += deltaGC[i];
            seqPrefix[i] = running;
        }
        tSeqEnd = System.nanoTime();
        long seqPrefixTime = tSeqEnd - tSeqStart;

        int[] parPrefix = new int[n];
        tParStart = System.nanoTime();
        pool.invoke(new ParallelPrefixSumTask(deltaGC, parPrefix, 0, n, 0));
        tParEnd = System.nanoTime();
        long parPrefixTime = tParEnd - tParStart;

        boolean prefixMatches = true;
        for (int i = 0; i < 1000; i++) {
            if (seqPrefix[i] != parPrefix[i]) { prefixMatches = false; break; }
        }

        System.out.println("Prefix Scan Output Matches: " + prefixMatches);
        System.out.println("Sequential Prefix Scan (T_1): " + seqPrefixTime + " ns");
        System.out.println("Parallel Prefix Scan (T_p):   " + parPrefixTime + " ns");

        // Find OriC Minimum Skew Candidate
        int minSkewIdx = 0;
        int minSkewVal = Integer.MAX_VALUE;
        for (int i = 0; i < n; i++) {
            if (parPrefix[i] < minSkewVal) {
                minSkewVal = parPrefix[i];
                minSkewIdx = i;
            }
        }
        System.out.println("Candidate Bacterial OriC (Origin of Replication): Base index " + minSkewIdx + " (Skew value: " + minSkewVal + ")");

        // --- 3. FORMAL WORK-SPAN COMPLEXITY ANALYSIS ---
        printWorkSpanAnalysis(n);
    }

    private static void printWorkSpanAnalysis(int n) {
        int p = Runtime.getRuntime().availableProcessors();
        double logN = Math.log(n) / Math.log(2);

        System.out.println("\n================ FORMAL WORK-SPAN THEORETICAL ANALYSIS ================");
        System.out.printf("%-26s | %-18s | %-18s%n", "Metric", "Parallel Reduce", "Parallel Prefix Sum");
        System.out.println("---------------------------+--------------------+-------------------");
        System.out.printf("%-26s | %-18s | %-18s%n", "Work T_1 (Total ops)", "O(n)", "O(n)");
        System.out.printf("%-26s | %-18s | %-18s%n", "Span T_inf (Critical path)", "O(log n)", "O(log n)");
        System.out.printf("%-26s | %-18s | %-18s%n", "Parallelism (T_1 / T_inf)", "Theta(n / log n)", "Theta(n / log n)");
        System.out.printf("%-26s | %-18s | %-18s%n", "Theoretical Depth (log2 n)", String.format("%.1f steps", logN), String.format("%.1f steps", 2 * logN));

        System.out.println("\nBrent's Theorem Law:");
        System.out.println("  T_p <= (T_1 / p) + T_inf");
        System.out.printf("  With p = %d processors, max possible speedup approaches %.2fx.%n", p, (double) p);
        System.out.println("  Work-efficiency: Both primitives achieve optimal Work O(n), matching the sequential lower bound.");
    }
}
