package m4;

import java.util.*;

public class EdmondsKarp {
    // Ford-Fulkerson implementation using Breadth-First Search (Edmonds-Karp)
    // Time Complexity: O(V * E^2), Space Complexity: O(V^2)

    public static class FlowNetwork {
        public int n;
        public int[][] capacity;
        public int[][] flow;
        public Map<Integer, String> nodeNames;

        public FlowNetwork(int n) {
            this.n = n;
            capacity = new int[n][n];
            flow = new int[n][n];
            nodeNames = new HashMap<>();
            for (int i = 0; i < n; i++) {
                nodeNames.put(i, "Node-" + i);
            }
        }

        public void addEdge(int u, int v, int cap, String nameU, String nameV) {
            capacity[u][v] += cap;
            if (nameU != null) nodeNames.put(u, nameU);
            if (nameV != null) nodeNames.put(v, nameV);
        }
    }

    public static class FlowResult {
        public int maxFlow;
        public List<String> augmentingPaths = new ArrayList<>();
        public Set<Integer> minCutSourceSide = new HashSet<>();
        public List<String> minCutEdges = new ArrayList<>();
        public int minCutCapacity = 0;
    }

    public static FlowResult computeMaxFlow(FlowNetwork net, int source, int sink) {
        int n = net.n;
        int[][] capacity = net.capacity;
        int[][] flow = new int[n][n];
        FlowResult result = new FlowResult();
        int totalFlow = 0;

        while (true) {
            int[] parent = new int[n];
            Arrays.fill(parent, -1);
            parent[source] = source;
            Queue<Integer> q = new ArrayDeque<>();
            q.add(source);

            while (!q.isEmpty() && parent[sink] == -1) {
                int u = q.poll();
                for (int v = 0; v < n; v++) {
                    // Residual capacity = capacity - flow
                    if (parent[v] == -1 && capacity[u][v] - flow[u][v] > 0) {
                        parent[v] = u;
                        q.add(v);
                    }
                }
            }

            if (parent[sink] == -1) break; // No augmenting path found

            // Find bottleneck capacity along augmenting path
            int push = Integer.MAX_VALUE;
            int curr = sink;
            List<Integer> path = new ArrayList<>();
            while (curr != source) {
                path.add(curr);
                int prev = parent[curr];
                push = Math.min(push, capacity[prev][curr] - flow[prev][curr]);
                curr = prev;
            }
            path.add(source);
            Collections.reverse(path);

            // Update residual flows
            curr = sink;
            while (curr != source) {
                int prev = parent[curr];
                flow[prev][curr] += push;
                flow[curr][prev] -= push; // back-edge
                curr = prev;
            }

            totalFlow += push;

            StringBuilder pathStr = new StringBuilder();
            for (int i = 0; i < path.size(); i++) {
                pathStr.append(net.nodeNames.get(path.get(i)));
                if (i < path.size() - 1) pathStr.append(" -> ");
            }
            pathStr.append("  [Bottleneck: ").append(push).append("]");
            result.augmentingPaths.add(pathStr.toString());
        }

        result.maxFlow = totalFlow;
        net.flow = flow;

        // Compute Min-Cut via BFS on residual graph from source
        boolean[] visited = new boolean[n];
        Queue<Integer> q = new ArrayDeque<>();
        q.add(source);
        visited[source] = true;

        while (!q.isEmpty()) {
            int u = q.poll();
            result.minCutSourceSide.add(u);
            for (int v = 0; v < n; v++) {
                if (!visited[v] && capacity[u][v] - flow[u][v] > 0) {
                    visited[v] = true;
                    q.add(v);
                }
            }
        }

        // Identify bottleneck cut edges from S-side to T-side
        for (int u : result.minCutSourceSide) {
            for (int v = 0; v < n; v++) {
                if (!result.minCutSourceSide.contains(v) && capacity[u][v] > 0) {
                    result.minCutEdges.add(net.nodeNames.get(u) + " -> " + net.nodeNames.get(v) + " (Cap: " + capacity[u][v] + ")");
                    result.minCutCapacity += capacity[u][v];
                }
            }
        }

        return result;
    }

    public static void run(Scanner scanner) {
        System.out.println("========== EDMONDS-KARP MAX-FLOW & MIN-CUT DUALITY ==========");
        System.out.println("Biological Context: Modeling Metabolic Flux & Gene Pathway Bottlenecks");
        System.out.println("Use Default Sample Metabolic Network? (Y/N, default Y): ");
        String choice = scanner.hasNextLine() ? scanner.nextLine().trim() : "";

        FlowNetwork net;
        int src = 0, sink = 5;

        if (choice.equalsIgnoreCase("N")) {
            System.out.print("Enter number of vertices: ");
            int n = Integer.parseInt(scanner.nextLine().trim());
            net = new FlowNetwork(n);
            System.out.print("Enter number of directed edges: ");
            int m = Integer.parseInt(scanner.nextLine().trim());
            System.out.println("Enter edges in format: u v capacity (0-indexed):");
            for (int i = 0; i < m; i++) {
                String[] parts = scanner.nextLine().trim().split("\\s+");
                int u = Integer.parseInt(parts[0]);
                int v = Integer.parseInt(parts[1]);
                int c = Integer.parseInt(parts[2]);
                net.addEdge(u, v, c, "Node-" + u, "Node-" + v);
            }
            System.out.print("Enter source vertex: ");
            src = Integer.parseInt(scanner.nextLine().trim());
            System.out.print("Enter sink vertex: ");
            sink = Integer.parseInt(scanner.nextLine().trim());
        } else {
            // Default: Glycolysis to Krebs Cycle Metabolic Reaction Network
            // 0: Glucose (Source)
            // 1: Glucose-6P (G6P)
            // 2: Fructose-1,6BP (F1,6BP)
            // 3: Phosphoenolpyruvate (PEP)
            // 4: Pyruvate
            // 5: ATP Biomass Yield (Sink)
            net = new FlowNetwork(6);
            net.addEdge(0, 1, 16, "Glucose(Source)", "G6P");
            net.addEdge(0, 2, 13, "Glucose(Source)", "F1,6BP");
            net.addEdge(1, 2, 10, "G6P", "F1,6BP");
            net.addEdge(1, 3, 12, "G6P", "PEP");
            net.addEdge(2, 4, 14, "F1,6BP", "Pyruvate");
            net.addEdge(3, 2, 9, "PEP", "F1,6BP");
            net.addEdge(3, 5, 20, "PEP", "Biomass(Sink)");
            net.addEdge(4, 3, 7, "Pyruvate", "PEP");
            net.addEdge(4, 5, 4, "Pyruvate", "Biomass(Sink)");
            src = 0;
            sink = 5;
            System.out.println("\nLoaded default metabolic pathway (6 metabolites, 9 biochemical reactions).");
        }

        System.out.println("\n--- RUNNING EDMONDS-KARP ALGORITHM ---");
        long tStart = System.nanoTime();
        FlowResult res = computeMaxFlow(net, src, sink);
        long tEnd = System.nanoTime();

        System.out.println("\n[Augmenting Paths Discovered via BFS]");
        for (int i = 0; i < res.augmentingPaths.size(); i++) {
            System.out.println("  Path " + (i + 1) + ": " + res.augmentingPaths.get(i));
        }

        System.out.println("\n================ MAX-FLOW RESULT ================");
        System.out.println("Maximum Metabolic Flux (Max-Flow): " + res.maxFlow + " units");
        System.out.println("Execution Time: " + (tEnd - tStart) + " ns");
        System.out.println("Time Complexity: O(V * E^2)");

        System.out.println("\n============= MIN-CUT DUALITY ANALYSIS =============");
        System.out.println("Source Partition S (Reachable in residual graph):");
        System.out.print("  { ");
        for (int u : res.minCutSourceSide) {
            System.out.print(net.nodeNames.get(u) + " ");
        }
        System.out.println("}");

        System.out.println("Bottleneck Cut Edges (Min-Cut):");
        for (String edge : res.minCutEdges) {
            System.out.println("  * " + edge);
        }
        System.out.println("Capacity of Min-Cut: " + res.minCutCapacity + " units");
        System.out.println("Max-Flow == Min-Cut Duality Verified: " + (res.maxFlow == res.minCutCapacity));
        System.out.println("Biological Impact: Inhibiting these critical bottleneck enzymes eliminates pathway throughput.");
    }
}
