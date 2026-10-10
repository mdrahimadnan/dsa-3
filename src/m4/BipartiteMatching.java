package m4;

import java.util.*;

public class BipartiteMatching {
    // Maximum Bipartite Matching & Assignment via Network Flow Reduction
    // Solves Orthologous Gene Pairing & Enzyme-Substrate Assignment
    // Demonstrates König's Theorem: |Max Matching| == |Min Vertex Cover|

    public static class BipartiteGraph {
        public List<String> leftNodes = new ArrayList<>();
        public List<String> rightNodes = new ArrayList<>();
        public List<int[]> edges = new ArrayList<>(); // (u, v) left -> right

        public void addLeft(String name) { leftNodes.add(name); }
        public void addRight(String name) { rightNodes.add(name); }
        public void addEdge(int u, int v) { edges.add(new int[]{u, v}); }
    }

    public static class MatchingResult {
        public int matchCount;
        public List<String> matchedPairs = new ArrayList<>();
        public List<String> unmatchedLeft = new ArrayList<>();
        public List<String> unmatchedRight = new ArrayList<>();
        public List<String> minVertexCover = new ArrayList<>();
    }

    public static MatchingResult solveViaMaxFlow(BipartiteGraph bg) {
        int nL = bg.leftNodes.size();
        int nR = bg.rightNodes.size();
        int totalNodes = nL + nR + 2;
        int source = 0;
        int sink = totalNodes - 1;

        // Flow network:
        // 0: Source
        // 1 .. nL: Left partition
        // nL + 1 .. nL + nR: Right partition
        // sink: Sink
        EdmondsKarp.FlowNetwork net = new EdmondsKarp.FlowNetwork(totalNodes);
        net.nodeNames.put(source, "Source(S)");
        net.nodeNames.put(sink, "Sink(T)");

        for (int i = 0; i < nL; i++) {
            net.addEdge(source, i + 1, 1, "Source(S)", bg.leftNodes.get(i));
        }

        for (int[] edge : bg.edges) {
            int u = edge[0] + 1;
            int v = nL + edge[1] + 1;
            net.addEdge(u, v, 1, bg.leftNodes.get(edge[0]), bg.rightNodes.get(edge[1]));
        }

        for (int j = 0; j < nR; j++) {
            int v = nL + j + 1;
            net.addEdge(v, sink, 1, bg.rightNodes.get(j), "Sink(T)");
        }

        EdmondsKarp.FlowResult flowRes = EdmondsKarp.computeMaxFlow(net, source, sink);

        MatchingResult res = new MatchingResult();
        res.matchCount = flowRes.maxFlow;

        boolean[] matchedL = new boolean[nL];
        boolean[] matchedR = new boolean[nR];

        for (int[] edge : bg.edges) {
            int u = edge[0] + 1;
            int v = nL + edge[1] + 1;
            if (net.flow[u][v] == 1) {
                res.matchedPairs.add(bg.leftNodes.get(edge[0]) + "  <--->  " + bg.rightNodes.get(edge[1]));
                matchedL[edge[0]] = true;
                matchedR[edge[1]] = true;
            }
        }

        for (int i = 0; i < nL; i++) {
            if (!matchedL[i]) res.unmatchedLeft.add(bg.leftNodes.get(i));
        }
        for (int j = 0; j < nR; j++) {
            if (!matchedR[j]) res.unmatchedRight.add(bg.rightNodes.get(j));
        }

        // König's theorem min vertex cover extraction:
        // L \ S (Left nodes not reachable in residual graph from S)
        // R ∩ S (Right nodes reachable in residual graph from S)
        for (int i = 0; i < nL; i++) {
            int u = i + 1;
            if (!flowRes.minCutSourceSide.contains(u)) {
                res.minVertexCover.add(bg.leftNodes.get(i) + " [Left Partition]");
            }
        }
        for (int j = 0; j < nR; j++) {
            int v = nL + j + 1;
            if (flowRes.minCutSourceSide.contains(v)) {
                res.minVertexCover.add(bg.rightNodes.get(j) + " [Right Partition]");
            }
        }

        return res;
    }

    public static void run(Scanner scanner) {
        System.out.println("========== MAXIMUM BIPARTITE MATCHING VIA FLOW ==========");
        System.out.println("Biological Context: Orthologous Gene Matching (Human vs Mouse Homologs)");
        System.out.println("Use Default Human-Mouse Gene Homology Dataset? (Y/N, default Y): ");
        String choice = scanner.hasNextLine() ? scanner.nextLine().trim() : "";

        BipartiteGraph bg = new BipartiteGraph();

        if (choice.equalsIgnoreCase("N")) {
            System.out.print("Enter number of Left partition items (e.g. Species 1 Genes): ");
            int nL = Integer.parseInt(scanner.nextLine().trim());
            for (int i = 0; i < nL; i++) {
                System.out.print("Name of Left item " + (i + 1) + ": ");
                bg.addLeft(scanner.nextLine().trim());
            }
            System.out.print("Enter number of Right partition items (e.g. Species 2 Genes): ");
            int nR = Integer.parseInt(scanner.nextLine().trim());
            for (int j = 0; j < nR; j++) {
                System.out.print("Name of Right item " + (j + 1) + ": ");
                bg.addRight(scanner.nextLine().trim());
            }
            System.out.print("Enter number of compatible edges: ");
            int e = Integer.parseInt(scanner.nextLine().trim());
            System.out.println("Enter edges as 'u v' (0-indexed indices for left and right):");
            for (int i = 0; i < e; i++) {
                String[] parts = scanner.nextLine().trim().split("\\s+");
                bg.addEdge(Integer.parseInt(parts[0]), Integer.parseInt(parts[1]));
            }
        } else {
            // Default Ortholog pairing:
            // Human genes: TP53, BRCA1, EGFR, MYC, INS
            // Mouse genes: Trp53, Brca1, Egfr, Myc, Ins1, Ins2
            bg.addLeft("Human_TP53");
            bg.addLeft("Human_BRCA1");
            bg.addLeft("Human_EGFR");
            bg.addLeft("Human_MYC");
            bg.addLeft("Human_INS");

            bg.addRight("Mouse_Trp53");
            bg.addRight("Mouse_Brca1");
            bg.addRight("Mouse_Egfr");
            bg.addRight("Mouse_Myc");
            bg.addRight("Mouse_Ins1");
            bg.addRight("Mouse_Ins2");

            // Sequence homology similarity edges above 80% threshold:
            bg.addEdge(0, 0); // TP53 -> Trp53
            bg.addEdge(1, 1); // BRCA1 -> Brca1
            bg.addEdge(2, 2); // EGFR -> Egfr
            bg.addEdge(3, 3); // MYC -> Myc
            bg.addEdge(4, 4); // INS -> Ins1
            bg.addEdge(4, 5); // INS -> Ins2 (paralog duplicate)
            System.out.println("\nLoaded 5 Human genes and 6 Mouse genes with 6 sequence homology compatibility links.");
        }

        System.out.println("\n--- SOLVING VIA NETWORK FLOW REDUCTION ---");
        long t1 = System.nanoTime();
        MatchingResult res = solveViaMaxFlow(bg);
        long t2 = System.nanoTime();

        System.out.println("\n================ OPTIMAL MATCHING RESULTS ================");
        System.out.println("Maximum Cardinality Bipartite Matching: " + res.matchCount + " pairs");
        System.out.println("Elapsed Time: " + (t2 - t1) + " ns");
        System.out.println("\nAssigned 1-to-1 Ortholog Pairs:");
        for (String pair : res.matchedPairs) {
            System.out.println("  [MATCH] " + pair);
        }

        if (!res.unmatchedLeft.isEmpty()) {
            System.out.println("\nUnmatched Left Items: " + res.unmatchedLeft);
        }
        if (!res.unmatchedRight.isEmpty()) {
            System.out.println("Unmatched Right Items: " + res.unmatchedRight);
        }

        System.out.println("\n============= KÖNIG'S THEOREM DUALITY VERIFICATION =============");
        System.out.println("König's Theorem states: In bipartite graphs, |Max Matching| == |Min Vertex Cover|");
        System.out.println("Minimum Vertex Cover Size: " + res.minVertexCover.size());
        System.out.println("Covering Vertex Set:");
        for (String v : res.minVertexCover) {
            System.out.println("  * " + v);
        }
        System.out.println("Theorem Holds: " + (res.matchCount == res.minVertexCover.size()));
    }
}
