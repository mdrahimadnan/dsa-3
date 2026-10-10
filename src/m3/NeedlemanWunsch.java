package m3;

import java.util.Scanner;

public class NeedlemanWunsch {
    // Global Alignment using Needleman-Wunsch Dynamic Programming with Traceback
    public static void run(Scanner scanner) {
        System.out.println("========== NEEDLEMAN-WUNSCH GLOBAL ALIGNMENT ==========");
        System.out.print("Enter first sequence (or press enter for default 'GCATGCG'): ");
        String a = scanner.nextLine().trim().toUpperCase();
        if (a.isEmpty()) a = "GCATGCG";

        System.out.print("Enter second sequence (or press enter for default 'GATTACA'): ");
        String b = scanner.nextLine().trim().toUpperCase();
        if (b.isEmpty()) b = "GATTACA";

        int matchScore = 1;
        int mismatchScore = -1;
        int gapPenalty = -1;

        int n = a.length();
        int m = b.length();
        int[][] dp = new int[n + 1][m + 1];

        // Initialization
        for (int i = 0; i <= n; i++) dp[i][0] = i * gapPenalty;
        for (int j = 0; j <= m; j++) dp[0][j] = j * gapPenalty;

        // DP Matrix Computation
        for (int i = 1; i <= n; i++) {
            for (int j = 1; j <= m; j++) {
                int match = dp[i - 1][j - 1] + (a.charAt(i - 1) == b.charAt(j - 1) ? matchScore : mismatchScore);
                int del = dp[i - 1][j] + gapPenalty; // gap in b
                int ins = dp[i][j - 1] + gapPenalty; // gap in a
                dp[i][j] = Math.max(match, Math.max(del, ins));
            }
        }

        // Traceback to reconstruct alignment
        StringBuilder alignA = new StringBuilder();
        StringBuilder alignB = new StringBuilder();
        StringBuilder middle = new StringBuilder();

        int i = n, j = m;
        int matches = 0, mismatches = 0, gaps = 0;

        while (i > 0 || j > 0) {
            if (i > 0 && j > 0 && dp[i][j] == dp[i - 1][j - 1] + (a.charAt(i - 1) == b.charAt(j - 1) ? matchScore : mismatchScore)) {
                alignA.append(a.charAt(i - 1));
                alignB.append(b.charAt(j - 1));
                if (a.charAt(i - 1) == b.charAt(j - 1)) {
                    middle.append("|");
                    matches++;
                } else {
                    middle.append(".");
                    mismatches++;
                }
                i--;
                j--;
            } else if (i > 0 && dp[i][j] == dp[i - 1][j] + gapPenalty) {
                alignA.append(a.charAt(i - 1));
                alignB.append("-");
                middle.append(" ");
                gaps++;
                i--;
            } else {
                alignA.append("-");
                alignB.append(b.charAt(j - 1));
                middle.append(" ");
                gaps++;
                j--;
            }
        }

        alignA.reverse();
        alignB.reverse();
        middle.reverse();

        System.out.println("\n--- OPTIMAL GLOBAL ALIGNMENT ---");
        System.out.println("Seq 1:  " + alignA);
        System.out.println("Match:  " + middle);
        System.out.println("Seq 2:  " + alignB);
        System.out.println("\nAlignment Statistics:");
        System.out.println("Optimal Score: " + dp[n][m]);
        System.out.println("Matches:       " + matches);
        System.out.println("Mismatches:    " + mismatches);
        System.out.println("Gaps:          " + gaps);
        double identity = ((double) matches / alignA.length()) * 100;
        System.out.printf("Identity:      %.2f%%%n", identity);
        System.out.println("Time Complexity:  O(n * m)");
        System.out.println("Space Complexity: O(n * m)");
    }
}
