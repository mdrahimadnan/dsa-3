package m2;

import java.util.Arrays;
import java.util.Scanner;

public class SuffixArray {
    // Suffix Array construction, Kasai's LCP Array, and Longest Repeated Substring detection
    public static void run(Scanner scanner) {
        System.out.println("========== SUFFIX ARRAY & KASAI LCP INDEXING ==========");
        System.out.print("Enter DNA sequence (or press enter for default 'GATAGACA$'): ");
        String s = scanner.nextLine().trim().toUpperCase();
        if (s.isEmpty()) s = "GATAGACA$";
        if (!s.endsWith("$")) s = s + "$";

        int n = s.length();

        // 1. Build Suffix Array
        Integer[] sa = new Integer[n];
        for (int i = 0; i < n; i++) sa[i] = i;
        final String text = s;
        Arrays.sort(sa, (a, b) -> text.substring(a).compareTo(text.substring(b)));

        // 2. Compute LCP Array using Kasai's Algorithm in O(n) time
        int[] rank = new int[n];
        for (int i = 0; i < n; i++) rank[sa[i]] = i;

        int[] lcp = new int[n];
        int h = 0;
        int maxLcp = 0;
        int maxLcpIdx = -1;

        for (int i = 0; i < n; i++) {
            if (rank[i] > 0) {
                int j = sa[rank[i] - 1];
                while (i + h < n && j + h < n && s.charAt(i + h) == s.charAt(j + h)) {
                    h++;
                }
                lcp[rank[i]] = h;
                if (h > maxLcp) {
                    maxLcp = h;
                    maxLcpIdx = rank[i];
                }
                if (h > 0) h--;
            }
        }

        // 3. Display Formatted Suffix & LCP Table
        System.out.println("\n--- SUFFIX ARRAY & LCP TABLE ---");
        System.out.printf("%-4s | %-6s | %-4s | %-20s%n", "Rank", "SA[i]", "LCP", "Suffix");
        System.out.println("-----+--------+------+---------------------");
        for (int i = 0; i < n; i++) {
            String suffix = s.substring(sa[i]);
            if (suffix.length() > 20) suffix = suffix.substring(0, 17) + "...";
            System.out.printf("%-4d | %-6d | %-4d | %-20s%n", i, sa[i], lcp[i], suffix);
        }

        System.out.println("\n--- GENOME ANALYSIS INSIGHTS ---");
        if (maxLcp > 0 && maxLcpIdx != -1) {
            String repeat = s.substring(sa[maxLcpIdx], sa[maxLcpIdx] + maxLcp);
            System.out.println("Longest Repeated DNA Substring (LRS): \"" + repeat + "\" (Length: " + maxLcp + ")");
            System.out.println("Occurs at indices: " + sa[maxLcpIdx - 1] + " and " + sa[maxLcpIdx]);
        } else {
            System.out.println("No non-trivial repeated substrings found.");
        }
        System.out.println("Kasai's LCP Construction Time: O(n)");
        System.out.println("Suffix Array Search Time:     O(m log n)");
    }
}
