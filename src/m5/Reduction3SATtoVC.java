package m5;

import java.util.*;

public class Reduction3SATtoVC {
    // Canonical Polynomial-Time Reduction Engine: 3-SAT -> Vertex Cover -> Independent Set
    // Demonstrates Cook-Levin Theorem corollaries, Gadget Constructions, and NP Verification

    public static class Clause {
        public int[] literals; // positive for x_i, negative for not x_i (1-indexed)
        public Clause(int l1, int l2, int l3) {
            literals = new int[]{l1, l2, l3};
        }
        @Override
        public String toString() {
            return "(" + litStr(literals[0]) + " v " + litStr(literals[1]) + " v " + litStr(literals[2]) + ")";
        }
        private String litStr(int l) {
            return (l > 0 ? "x" + l : "~x" + (-l));
        }
    }

    public static class Graph {
        public List<String> nodeNames = new ArrayList<>();
        public List<int[]> edges = new ArrayList<>();

        public int addNode(String name) {
            nodeNames.add(name);
            return nodeNames.size() - 1;
        }

        public void addEdge(int u, int v) {
            edges.add(new int[]{u, v});
        }
    }

    public static class ReductionResult {
        public Graph graph;
        public int numVariables;
        public int numClauses;
        public int vertexCoverBudgetK;
        public int independentSetTargetK;
        public Map<String, Integer> literalNodeMap = new HashMap<>();
    }

    public static ReductionResult reduce(int numVars, List<Clause> clauses) {
        ReductionResult res = new ReductionResult();
        res.graph = new Graph();
        res.numVariables = numVars;
        res.numClauses = clauses.size();
        res.vertexCoverBudgetK = numVars + 2 * clauses.size();
        res.independentSetTargetK = numVars + clauses.size();

        // 1. Variable Gadgets: For each x_i, create pair (x_i, ~x_i) with edge between them
        int[] posNode = new int[numVars + 1];
        int[] negNode = new int[numVars + 1];
        for (int i = 1; i <= numVars; i++) {
            posNode[i] = res.graph.addNode("Var_x" + i);
            negNode[i] = res.graph.addNode("Var_~x" + i);
            res.graph.addEdge(posNode[i], negNode[i]);
            res.literalNodeMap.put("x" + i, posNode[i]);
            res.literalNodeMap.put("~x" + i, negNode[i]);
        }

        // 2. Clause Gadgets: For each clause C_j, create a triangle of 3 nodes
        for (int j = 0; j < clauses.size(); j++) {
            Clause c = clauses.get(j);
            int cNode1 = res.graph.addNode("C" + (j + 1) + "_L1(" + litName(c.literals[0]) + ")");
            int cNode2 = res.graph.addNode("C" + (j + 1) + "_L2(" + litName(c.literals[1]) + ")");
            int cNode3 = res.graph.addNode("C" + (j + 1) + "_L3(" + litName(c.literals[2]) + ")");

            // Triangle edges (require at least 2 vertices in any vertex cover)
            res.graph.addEdge(cNode1, cNode2);
            res.graph.addEdge(cNode2, cNode3);
            res.graph.addEdge(cNode3, cNode1);

            // Connect clause literal node to its corresponding variable literal node
            int vTarget1 = c.literals[0] > 0 ? posNode[c.literals[0]] : negNode[-c.literals[0]];
            int vTarget2 = c.literals[1] > 0 ? posNode[c.literals[1]] : negNode[-c.literals[1]];
            int vTarget3 = c.literals[2] > 0 ? posNode[c.literals[2]] : negNode[-c.literals[2]];

            res.graph.addEdge(cNode1, vTarget1);
            res.graph.addEdge(cNode2, vTarget2);
            res.graph.addEdge(cNode3, vTarget3);
        }

        return res;
    }

    private static String litName(int l) {
        return l > 0 ? "x" + l : "~x" + (-l);
    }

    // Polynomial-time Certificate Verifier (Proves problem is in NP)
    public static boolean verifyAssignment(int numVars, List<Clause> clauses, boolean[] assignment) {
        for (Clause c : clauses) {
            boolean clauseSatisfied = false;
            for (int lit : c.literals) {
                int var = Math.abs(lit);
                boolean val = assignment[var];
                if ((lit > 0 && val) || (lit < 0 && !val)) {
                    clauseSatisfied = true;
                    break;
                }
            }
            if (!clauseSatisfied) return false;
        }
        return true;
    }

    public static void run(Scanner scanner) {
        System.out.println("========== CANONICAL NP REDUCTION: 3-SAT -> VERTEX COVER -> INDEPENDENT SET ==========");
        System.out.println("Complexity Classes Analyzed: P, NP, NP-Complete, NP-Hard");
        System.out.println("Use Default Biomedical Regulatory Constraint 3-SAT Formula? (Y/N, default Y): ");
        String choice = scanner.hasNextLine() ? scanner.nextLine().trim() : "";

        int numVars = 3;
        List<Clause> clauses = new ArrayList<>();

        if (choice.equalsIgnoreCase("N")) {
            System.out.print("Enter number of boolean variables (n): ");
            numVars = Integer.parseInt(scanner.nextLine().trim());
            System.out.print("Enter number of 3-literal clauses (m): ");
            int m = Integer.parseInt(scanner.nextLine().trim());
            System.out.println("Enter each clause as 3 integers (e.g. '1 -2 3' means x1 v ~x2 v x3):");
            for (int i = 0; i < m; i++) {
                String[] parts = scanner.nextLine().trim().split("\\s+");
                clauses.add(new Clause(Integer.parseInt(parts[0]), Integer.parseInt(parts[1]), Integer.parseInt(parts[2])));
            }
        } else {
            // Default: Transcription factor co-activation rules
            // Clause 1: (x1 v ~x2 v x3)
            // Clause 2: (~x1 v x2 v ~x3)
            // Clause 3: (x1 v x2 v x3)
            clauses.add(new Clause(1, -2, 3));
            clauses.add(new Clause(-1, 2, -3));
            clauses.add(new Clause(1, 2, 3));
            System.out.println("\nLoaded default 3-SAT formula with n = 3 variables, m = 3 clauses:");
            for (int i = 0; i < clauses.size(); i++) {
                System.out.println("  C" + (i + 1) + ": " + clauses.get(i));
            }
        }

        System.out.println("\n--- PERFORMING POLYNOMIAL REDUCTION 3-SAT -> VERTEX COVER ---");
        long tStart = System.nanoTime();
        ReductionResult red = reduce(numVars, clauses);
        long tEnd = System.nanoTime();

        System.out.println("Constructed Reduced Graph G = (V, E):");
        System.out.println("  * Total Vertices |V|: " + red.graph.nodeNames.size() + " [2n variable nodes + 3m clause nodes]");
        System.out.println("  * Total Edges |E|:    " + red.graph.edges.size() + " [n variable edges + 3m triangle edges + 3m connecting edges]");
        System.out.println("  * Vertex Cover Target Budget k: " + red.vertexCoverBudgetK + " [k = n + 2m]");
        System.out.println("  * Reduction Runtime: " + (tEnd - tStart) + " ns  (Strictly O(n + m) Polynomial Time)");

        System.out.println("\n--- CANONICAL CHAIN: VERTEX COVER -> INDEPENDENT SET ---");
        System.out.println("Mathematical Duality Theorem: S is a Vertex Cover of G <=> (V \\ S) is an Independent Set of G.");
        System.out.println("  * Independent Set Target Size k' = |V| - k = (" + red.graph.nodeNames.size() + " - " + red.vertexCoverBudgetK + ") = " + red.independentSetTargetK);
        System.out.println("  * Independent Set Target Budget k' = n + m = " + red.independentSetTargetK);

        System.out.println("\n--- NP CERTIFICATE POLYNOMIAL-TIME VERIFIER ---");
        System.out.println("Testing sample satisfying assignment: x1 = TRUE, x2 = TRUE, x3 = FALSE");
        boolean[] testAssign = new boolean[numVars + 1];
        testAssign[1] = true;
        testAssign[2] = true;
        testAssign[3] = false;

        boolean satisfied = verifyAssignment(numVars, clauses, testAssign);
        System.out.println("Is Certificate Valid: " + satisfied);
        System.out.println("Verification Time: O(m) linear operations, confirming membership in NP.");
        System.out.println("\nConclusion: Because 3-SAT <=p Vertex Cover <=p Independent Set, and 3-SAT is NP-Complete,");
        System.out.println("both Vertex Cover and Independent Set are proven NP-Complete.");
    }
}
