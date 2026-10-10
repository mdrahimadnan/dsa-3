package m3;

import java.util.Scanner;

public class SmithWaterman {
    // Local Alignment using Smith-Waterman Dynamic Programming with Traceback
    public static void run(Scanner scanner) {
        System.out.println("========== SMITH-WATERMAN LOCAL ALIGNMENT ==========");
        System.out.print("Enter first sequence (or press enter for default 'TGTTACGG'): ");
        String a = scanner.nextLine().trim().toUpperCase();
        if (a.isEmpty()) a = "TGTTACGG";

        System.out.print("Enter second sequence (or press enter for default 'GGTTGACTA'): ");
        String b = scanner.nextLine().trim().toUpperCase();
        if (b.isEmpty()) b = "GGTTGACTA";

        int matchScore = 2;
        int mismatchScore = -1;
        int gapPenalty = -1;

        int n = a.length();
        int m = b.length();
        int[][] dp = new int[n + 1][m + 1];

        int maxScore = 0;
        int maxI = 0, maxJ = 0;

        for (int i = 1; i <= n; i++) {
            for (int j = 1; j <= m; j++) {
                int match = dp[i - 1][j - 1] + (a.charAt(i - 1) == b.charAt(j - 1) ? matchScore : mismatchScore);
                int del = dp[i - 1][j] + gapPenalty;
                int ins = dp[i][j - 1] + gapPenalty;
                int score = Math.max(0, Math.max(match, Math.max(del, ins)));
                dp[i][j] = score;

                if (score > maxScore) {
                    maxScore = score;
                    maxI = i;
                    maxJ = j;
                }
            }
        }

        // Traceback from maximum cell until score reaches 0
        StringBuilder alignA = new StringBuilder();
        StringBuilder alignB = new StringBuilder();
        StringBuilder middle = new StringBuilder();

        int i = maxI, j = maxJ;
        int matches = 0, mismatches = 0, gaps = 0;

        while (i > 0 && j > 0 && dp[i][j] > 0) {
            int currentScore = dp[i][j];
            int diag = dp[i - 1][j - 1] + (a.charAt(i - 1) == b.charAt(j - 1) ? matchScore : mismatchScore);
            int up = dp[i - 1][j] + gapPenalty;

            if (currentScore == diag) {
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
            } else if (currentScore == up) {
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

        System.out.println("\n--- HIGHEST SCORING LOCAL ALIGNMENT REGION ---");
        System.out.println("Subsequence 1: " + alignA);
        System.out.println("Local Match:   " + middle);
        System.out.println("Subsequence 2: " + alignB);
        System.out.println("\nAlignment Statistics:");
        System.out.println("Peak Local Score:    " + maxScore);
        System.out.println("End Coordinates:     Seq1[" + (maxI - 1) + "], Seq2[" + (maxJ - 1) + "]");
        System.out.println("Local Matches:       " + matches);
        System.out.println("Local Mismatches:    " + mismatches);
        System.out.println("Local Gaps:          " + gaps);
        System.out.println("Time Complexity:     O(n * m)");
        System.out.println("Space Complexity:    O(n * m)");
    }
}
