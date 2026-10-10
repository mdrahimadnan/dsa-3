package m1;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class KMP {
    public static void run(Scanner scanner) {
        System.out.println("========== KMP DNA MOTIF SEARCH ==========");
        System.out.print("Enter DNA sequence: ");
        String text = scanner.nextLine().trim().toUpperCase();
        System.out.print("Enter motif: ");
        String pattern = scanner.nextLine().trim().toUpperCase();

        if (!isValidDNA(text) || !isValidDNA(pattern)) {
            System.out.println("Invalid DNA sequence or motif. Only A,T,G,C allowed.");
            return;
        }

        List<Integer> matches = search(text, pattern);
        System.out.println("KMP RESULT");
        System.out.println("-------------------------");
        System.out.println("Pattern: " + pattern);
        System.out.print("Occurrences: ");
        if (matches.isEmpty()) {
            System.out.println("None");
        } else {
            for (int idx : matches) {
                System.out.print(idx + " ");
            }
            System.out.println();
        }
        System.out.println("Total Matches: " + matches.size());
        System.out.println("Time Complexity: O(n + m)");
    }

    private static boolean isValidDNA(String s) {
        return s.matches("[ATGC]*");
    }

    private static List<Integer> search(String text, String pattern) {
        List<Integer> result = new ArrayList<>();
        int n = text.length();
        int m = pattern.length();
        if (m == 0 || n < m) return result;
        int[] lps = computeLPS(pattern);
        int i = 0, j = 0;
        while (i < n) {
            if (text.charAt(i) == pattern.charAt(j)) {
                i++; j++;
                if (j == m) {
                    result.add(i - j);
                    j = lps[j - 1];
                }
            } else {
                if (j != 0) {
                    j = lps[j - 1];
                } else {
                    i++;
                }
            }
        }
        return result;
    }

    private static int[] computeLPS(String pat) {
        int m = pat.length();
        int[] lps = new int[m];
        int len = 0;
        int i = 1;
        while (i < m) {
            if (pat.charAt(i) == pat.charAt(len)) {
                len++;
                lps[i] = len;
                i++;
            } else {
                if (len != 0) {
                    len = lps[len - 1];
                } else {
                    lps[i] = 0;
                    i++;
                }
            }
        }
        return lps;
    }
}
