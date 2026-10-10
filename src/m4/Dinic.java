package m4;

import java.util.*;

public class Dinic {
    // Dinic's Algorithm using BFS Level Graph & DFS Blocking Flows
    // Time Complexity: O(V^2 * E) general, O(E * sqrt(V)) for unit networks
    // Space Complexity: O(V + E)

    public static class Edge {
        public int to;
        public int rev;
        public int cap;
        public int flow;

        public Edge(int to, int rev, int cap) {
            this.to = to;
            this.rev = rev;
            this.cap = cap;
            this.flow = 0;
        }
    }

    public static class DinicGraph {
        public int n;
        public List<List<Edge>> adj;
        public int[] level;
        public int[] ptr;
        public Map<Integer, String> names;

        public DinicGraph(int n) {
            this.n = n;
            adj = new ArrayList<>();
            for (int i = 0; i < n; i++) adj.add(new ArrayList<>());
            level = new int[n];
            ptr = new int[n];
            names = new HashMap<>();
            for (int i = 0; i < n; i++) names.put(i, "Node-" + i);
        }

        public void addEdge(int from, int to, int cap, String nameFrom, String nameTo) {
            Edge forward = new Edge(to, adj.get(to).size(), cap);
            Edge backward = new Edge(from, adj.get(from).size(), 0);
            adj.get(from).add(forward);
            adj.get(to).add(backward);
            if (nameFrom != null) names.put(from, nameFrom);
            if (nameTo != null) names.put(to, nameTo);
        }

        // BFS to construct Level Graph
        public boolean bfsLevelGraph(int s, int t) {
            Arrays.fill(level, -1);
            level[s] = 0;
            Queue<Integer> q = new ArrayDeque<>();
            q.add(s);

            while (!q.isEmpty()) {
                int u = q.poll();
                for (Edge e : adj.get(u)) {
                    if (e.cap - e.flow > 0 && level[e.to] == -1) {
                        level[e.to] = level[u] + 1;
                        q.add(e.to);
                    }
                }
            }
            return level[t] != -1;
        }

        // DFS to push Blocking Flow along shortest path dag
        public int dfsBlockingFlow(int u, int t, int pushed) {
            if (pushed == 0 || u == t) return pushed;

            for (; ptr[u] < adj.get(u).size(); ptr[u]++) {
                Edge e = adj.get(u).get(ptr[u]);
                int tr = e.to;
                if (level[u] + 1 != level[tr] || e.cap - e.flow == 0) continue;

                int trPushed = dfsBlockingFlow(tr, t, Math.min(pushed, e.cap - e.flow));
                if (trPushed == 0) continue;

                e.flow += trPushed;
                adj.get(tr).get(e.rev).flow -= trPushed;
                return trPushed;
            }
            return 0;
        }

        public int maxFlow(int s, int t, List<String> phaseLogs) {
            int flow = 0;
            int phase = 1;
            while (bfsLevelGraph(s, t)) {
                Arrays.fill(ptr, 0);
                int phaseFlow = 0;
                while (true) {
                    int pushed = dfsBlockingFlow(s, t, Integer.MAX_VALUE);
                    if (pushed == 0) break;
                    phaseFlow += pushed;
                }
                phaseLogs.add("Phase " + phase + ": Level Graph constructed (Sink Level = " + level[t] + "), Blocking Flow pushed = " + phaseFlow);
                flow += phaseFlow;
                phase++;
            }
            return flow;
        }
    }

    public static void run(Scanner scanner) {
        System.out.println("========== DINIC'S FAST NETWORK FLOW ALGORITHM ==========");
        System.out.println("Biological Context: High-Throughput Gene Regulatory & Protein Flux Routing");
        System.out.println("Use Default Multi-Layer Cellular Network? (Y/N, default Y): ");
        String choice = scanner.hasNextLine() ? scanner.nextLine().trim() : "";

        DinicGraph graph;
        int s = 0, t = 7;

        if (choice.equalsIgnoreCase("N")) {
            System.out.print("Enter number of nodes: ");
            int n = Integer.parseInt(scanner.nextLine().trim());
            graph = new DinicGraph(n);
            System.out.print("Enter number of directed edges: ");
            int m = Integer.parseInt(scanner.nextLine().trim());
            System.out.println("Enter edges: u v capacity (0-indexed):");
            for (int i = 0; i < m; i++) {
                String[] parts = scanner.nextLine().trim().split("\\s+");
                graph.addEdge(Integer.parseInt(parts[0]), Integer.parseInt(parts[1]), Integer.parseInt(parts[2]), null, null);
            }
            System.out.print("Enter source: ");
            s = Integer.parseInt(scanner.nextLine().trim());
            System.out.print("Enter sink: ");
            t = Integer.parseInt(scanner.nextLine().trim());
        } else {
            // Layered Cellular Signal Cascade Network
            // Source: Extracellular Receptor
            // Layer 1: Kinases (MAP3K1, RAF1)
            // Layer 2: Mediators (MEK1, MEK2)
            // Layer 3: Transcription Factors (ERK1, ERK2)
            // Sink: Gene Expression Target
            graph = new DinicGraph(8);
            graph.addEdge(0, 1, 10, "Receptor(Source)", "MAP3K1");
            graph.addEdge(0, 2, 10, "Receptor(Source)", "RAF1");
            graph.addEdge(1, 3, 4, "MAP3K1", "MEK1");
            graph.addEdge(1, 4, 8, "MAP3K1", "MEK2");
            graph.addEdge(2, 4, 9, "RAF1", "MEK2");
            graph.addEdge(3, 5, 10, "MEK1", "ERK1");
            graph.addEdge(4, 5, 6, "MEK2", "ERK1");
            graph.addEdge(4, 6, 10, "MEK2", "ERK2");
            graph.addEdge(5, 7, 10, "ERK1", "TargetGene(Sink)");
            graph.addEdge(6, 7, 10, "ERK2", "TargetGene(Sink)");
            s = 0;
            t = 7;
            System.out.println("\nLoaded Multi-Tier Phosphorylation Signal Cascade (8 nodes, 10 regulatory edges).");
        }

        List<String> logs = new ArrayList<>();
        long tStart = System.nanoTime();
        int maxFlow = graph.maxFlow(s, t, logs);
        long tEnd = System.nanoTime();

        System.out.println("\n--- DINIC EXECUTION PHASES (LEVEL GRAPH & BLOCKING FLOW) ---");
        for (String log : logs) {
            System.out.println("  " + log);
        }

        System.out.println("\n================= DINIC'S MAX-FLOW RESULT =================");
        System.out.println("Peak Signal Transmission (Max-Flow): " + maxFlow + " units");
        System.out.println("Elapsed Time: " + (tEnd - tStart) + " ns");
        System.out.println("Algorithmic Advantage: Dinic uses level graphs to eliminate long path oscillations,");
        System.out.println("reaching blocking flow in O(V^2 * E) vs O(V * E^2) in Edmonds-Karp.");
    }
}
