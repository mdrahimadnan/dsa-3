package m1;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class RabinKarp {
    private static final int PRIME = 101;
    private static final int MOD = 1_000_000_007;

    public static void run(Scanner scanner) {
        System.out.println("========== RABIN-KARP RESULT ==========");
        System.out.print("Enter DNA sequence: ");
        String text = scanner.nextLine().trim().toUpperCase();
        System.out.print("Enter pattern: ");
        String pat = scanner.nextLine().trim().toUpperCase();

        List<Integer> matches = search(text, pat);
        System.out.println("Pattern Hash (mod " + MOD + "): " + hash(pat));
        System.out.print("Matches at positions: ");
        if (matches.isEmpty()) {
            System.out.println("None");
        } else {
            for (int idx : matches) {
                System.out.print(idx + " ");
            }
            System.out.println();
        }
        System.out.println("Total Matches: " + matches.size());
    }

    private static long hash(String s) {
        long h = 0;
        for (int i = 0; i < s.length(); i++) {
            h = (h * PRIME + s.charAt(i)) % MOD;
        }
        return h;
    }

    private static List<Integer> search(String text, String pat) {
        List<Integer> result = new ArrayList<>();
        int n = text.length();
        int m = pat.length();
        if (m == 0 || n < m) return result;

        long patHash = hash(pat);
        long txtHash = hash(text.substring(0, m));
        long h = 1;
        for (int i = 0; i < m - 1; i++) {
            h = (h * PRIME) % MOD;
        }
        for (int i = 0; i <= n - m; i++) {
            if (patHash == txtHash) {
                if (text.substring(i, i + m).equals(pat)) {
                    result.add(i);
                }
            }
            if (i < n - m) {
                txtHash = (txtHash - text.charAt(i) * h) % MOD;
                txtHash = (txtHash * PRIME + text.charAt(i + m)) % MOD;
                if (txtHash < 0) txtHash += MOD;
            }
        }
        return result;
    }
}
