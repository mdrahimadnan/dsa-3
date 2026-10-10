package m1;

import java.util.*;
import java.util.Scanner;

public class AhoCorasick {
    private static class Node {
        Map<Character, Node> next = new HashMap<>();
        Node fail;
        List<Integer> output = new ArrayList<>();
    }

    public static void run(Scanner scanner) {
        System.out.println("========== AHO-CORASICK MULTI MOTIF SEARCH ==========");
        System.out.print("Enter DNA sequence: ");
        String text = scanner.nextLine().trim().toUpperCase();
        System.out.print("How many motifs? ");
        int k = Integer.parseInt(scanner.nextLine().trim());
        List<String> patterns = new ArrayList<>();
        for (int i = 0; i < k; i++) {
            System.out.print("Enter motif " + (i + 1) + ": ");
            String pat = scanner.nextLine().trim().toUpperCase();
            if (!pat.matches("[ATGC]*")) {
                System.out.println("Invalid motif. Only A,T,G,C allowed.");
                i--;
                continue;
            }
            patterns.add(pat);
        }
        // Build trie
        Node root = new Node();
        for (int i = 0; i < patterns.size(); i++) {
            String pat = patterns.get(i);
            Node node = root;
            for (char ch : pat.toCharArray()) {
                node = node.next.computeIfAbsent(ch, c -> new Node());
            }
            node.output.add(i);
        }
        // Build failure links
        Queue<Node> q = new ArrayDeque<>();
        for (Node child : root.next.values()) {
            child.fail = root;
            q.add(child);
        }
        while (!q.isEmpty()) {
            Node cur = q.poll();
            for (Map.Entry<Character, Node> entry : cur.next.entrySet()) {
                char ch = entry.getKey();
                Node child = entry.getValue();
                Node f = cur.fail;
                while (f != null && !f.next.containsKey(ch)) {
                    f = f.fail;
                }
                child.fail = (f == null) ? root : f.next.get(ch);
                child.output.addAll(child.fail.output);
                q.add(child);
            }
        }
        // Search
        Map<Integer, List<Integer>> occurrences = new HashMap<>();
        Node state = root;
        for (int i = 0; i < text.length(); i++) {
            char ch = text.charAt(i);
            while (state != root && !state.next.containsKey(ch)) {
                state = state.fail;
            }
            state = state.next.getOrDefault(ch, root);
            for (int idx : state.output) {
                occurrences.computeIfAbsent(idx, x -> new ArrayList<>()).add(i - patterns.get(idx).length() + 1);
            }
        }
        System.out.println("Aho-Corasick Result");
        System.out.println("-------------------------");
        for (int i = 0; i < patterns.size(); i++) {
            System.out.print(patterns.get(i) + " -> positions ");
            List<Integer> pos = occurrences.getOrDefault(i, Collections.emptyList());
            if (pos.isEmpty()) {
                System.out.println("None");
            } else {
                for (int p : pos) System.out.print(p + " ");
                System.out.println();
            }
        }
    }
}
