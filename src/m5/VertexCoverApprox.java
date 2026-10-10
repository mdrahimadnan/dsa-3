package m5;

import java.util.*;

public class VertexCoverApprox {
    // 2-Approximation Algorithm for Minimum Vertex Cover (Gavril / Bar-Yehuda)
    // Provable Approximation Ratio: |C_approx| <= 2 * |C_optimal|
    // Real Biological Context: Minimum PCR Primer Selection to cover all pathogen mutant strains

    public static class Edge {
        public int u, v;
        public Edge(int u, int v) { this.u = u; this.v = v; }
    }

    public static class Graph {
        public int n;
        public List<String> nodeNames = new ArrayList<>();
        public List<Edge> edges = new ArrayList<>();

        public Graph(int n) {
            this.n = n;
            for (int i = 0; i < n; i++) nodeNames.add("Node-" + i);
        }

        public void setNodeName(int i, String name) { nodeNames.set(i, name); }
        public void addEdge(int u, int v) { edges.add(new Edge(u, v)); }
    }

    // 2-Approximation using Maximal Matching
    // Time Complexity: O(V + E)
    public static List<Integer> approximateVertexCover(Graph g, List<Edge> maximalMatching) {
        boolean[] inCover = new boolean[g.n];
        boolean[] edgeRemoved = new boolean[g.edges.size()];

        for (int i = 0; i < g.edges.size(); i++) {
            if (edgeRemoved[i]) continue;
            Edge e = g.edges.get(i);
            if (!inCover[e.u] && !inCover[e.v]) {
                // Add both endpoints to cover
                inCover[e.u] = true;
                inCover[e.v] = true;
                maximalMatching.add(e);

                // Discard all incident edges
                for (int j = 0; j < g.edges.size(); j++) {
                    Edge ej = g.edges.get(j);
                    if (ej.u == e.u || ej.u == e.v || ej.v == e.u || ej.v == e.v) {
                        edgeRemoved[j] = true;
                    }
                }
            }
        }

        List<Integer> cover = new ArrayList<>();
        for (int i = 0; i < g.n; i++) {
            if (inCover[i]) cover.add(i);
        }
        return cover;
    }

    // Exact Optimal Vertex Cover via Branch and Bound / Backtracking (for comparison)
    public static List<Integer> exactOptimalVertexCover(Graph g) {
        List<Integer> bestCover = new ArrayList<>();
        for (int i = 0; i < g.n; i++) bestCover.add(i); // Initial trivial cover

        boolean[] currentCover = new boolean[g.n];
        findExact(g, 0, currentCover, 0, bestCover);
        return bestCover;
    }

    private static void findExact(Graph g, int edgeIdx, boolean[] currentCover, int currentCount, List<Integer> bestCover) {
        if (currentCount >= bestCover.size()) return; // Prune

        // Find next uncovered edge
        while (edgeIdx < g.edges.size()) {
            Edge e = g.edges.get(edgeIdx);
            if (!currentCover[e.u] && !currentCover[e.v]) break;
            edgeIdx++;
        }

        if (edgeIdx == g.edges.size()) {
            // All edges covered!
            bestCover.clear();
            for (int i = 0; i < g.n; i++) {
                if (currentCover[i]) bestCover.add(i);
            }
            return;
        }

        Edge e = g.edges.get(edgeIdx);

        // Branch 1: Pick u
        currentCover[e.u] = true;
        findExact(g, edgeIdx + 1, currentCover, currentCount + 1, bestCover);
        currentCover[e.u] = false;

        // Branch 2: Pick v
        currentCover[e.v] = true;
        findExact(g, edgeIdx + 1, currentCover, currentCount + 1, bestCover);
        currentCover[e.v] = false;
    }

    public static void run(Scanner scanner) {
        System.out.println("========== 2-APPROXIMATION ALGORITHM: MINIMUM VERTEX COVER ==========");
        System.out.println("Biological Context: Minimum Primer Set Selection for Viral Mutant Screening");
        System.out.println("Use Default Viral Mutation Variant Interaction Graph? (Y/N, default Y): ");
        String choice = scanner.hasNextLine() ? scanner.nextLine().trim() : "";

        Graph g;

        if (choice.equalsIgnoreCase("N")) {
            System.out.print("Enter number of vertices (|V|): ");
            int n = Integer.parseInt(scanner.nextLine().trim());
            g = new Graph(n);
            System.out.print("Enter number of edges (|E|): ");
            int m = Integer.parseInt(scanner.nextLine().trim());
            System.out.println("Enter edges as 'u v' (0-indexed):");
            for (int i = 0; i < m; i++) {
                String[] parts = scanner.nextLine().trim().split("\\s+");
                g.addEdge(Integer.parseInt(parts[0]), Integer.parseInt(parts[1]));
            }
        } else {
            // Default: 8 Viral mutant strains with mutation co-occurrence edges
            g = new Graph(8);
            g.setNodeName(0, "Strain_Alpha(P681H)");
            g.setNodeName(1, "Strain_Beta(K417N)");
            g.setNodeName(2, "Strain_Gamma(E484K)");
            g.setNodeName(3, "Strain_Delta(L452R)");
            g.setNodeName(4, "Strain_Omicron_BA1");
            g.setNodeName(5, "Strain_Omicron_BA2");
            g.setNodeName(6, "Strain_Omicron_BA5");
            g.setNodeName(7, "Strain_Omicron_XBB");

            // Edges representing cross-reactivity / co-occurring mutation targets
            g.addEdge(0, 1);
            g.addEdge(1, 2);
            g.addEdge(2, 3);
            g.addEdge(3, 4);
            g.addEdge(4, 5);
            g.addEdge(5, 6);
            g.addEdge(6, 7);
            g.addEdge(4, 7);
            g.addEdge(2, 4);
            System.out.println("\nLoaded viral strain network (8 strains, 9 cross-reactive co-mutation edges).");
        }

        // 1. Run 2-Approximation
        List<Edge> maximalMatching = new ArrayList<>();
        long t1 = System.nanoTime();
        List<Integer> approxCover = approximateVertexCover(g, maximalMatching);
        long t2 = System.nanoTime();

        // 2. Run Exact Solver for comparison
        long t3 = System.nanoTime();
        List<Integer> exactCover = exactOptimalVertexCover(g);
        long t4 = System.nanoTime();

        System.out.println("\n--- 2-APPROXIMATION (MAXIMAL MATCHING) RESULT ---");
        System.out.println("Disjoint Maximal Matching M selected (" + maximalMatching.size() + " edges):");
        for (Edge e : maximalMatching) {
            System.out.println("  * (" + g.nodeNames.get(e.u) + " <---> " + g.nodeNames.get(e.v) + ")");
        }
        System.out.println("Approximate Vertex Cover Size: " + approxCover.size());
        System.out.println("Selected Primers / Strains: ");
        for (int v : approxCover) {
            System.out.println("  [COVER] " + g.nodeNames.get(v));
        }
        System.out.println("Approximation Runtime: " + (t2 - t1) + " ns  (Linear O(V + E))");

        System.out.println("\n--- EXACT OPTIMAL VERTEX COVER (BRUTE-FORCE / B&B) ---");
        System.out.println("Optimal Vertex Cover Size (OPT): " + exactCover.size());
        System.out.println("Optimal Cover Elements: ");
        for (int v : exactCover) {
            System.out.println("  [OPT] " + g.nodeNames.get(v));
        }
        System.out.println("Exact Runtime: " + (t4 - t3) + " ns  (Exponential O(2^V))");

        System.out.println("\n--- PROVABLE APPROXIMATION RATIO PROOF ---");
        double ratio = (double) approxCover.size() / exactCover.size();
        System.out.printf("Observed Empirical Ratio: %.2f%n", ratio);
        System.out.println("Mathematical Guarantee Proof:");
        System.out.println("  1. M is an independent set of edges; any valid cover must pick at least 1 endpoint per edge in M.");
        System.out.println("     Therefore: |OPT| >= |M| = " + maximalMatching.size());
        System.out.println("  2. Algorithm picks both endpoints of every edge in M:");
        System.out.println("     Therefore: |Approx| = 2 * |M| = 2 * " + maximalMatching.size() + " = " + (2 * maximalMatching.size()));
        System.out.println("  3. |Approx| = 2 * |M| <= 2 * |OPT| => (|Approx| / |OPT|) <= 2.0 (Strictly bounded!)");
    }
}
