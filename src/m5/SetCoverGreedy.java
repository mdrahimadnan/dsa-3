package m5;

import java.util.*;

public class SetCoverGreedy {
    // Greedy Approximation Algorithm for Minimum Set Cover
    // Provable Approximation Ratio: H(d) = sum_{i=1}^d (1/i) <= ln(n) + 1
    // Biological Application: Multiplex PCR Primer Pool Design covering multiple viral targets

    public static class SetCoverInstance {
        public List<String> universe = new ArrayList<>();
        public List<String> setNames = new ArrayList<>();
        public List<Set<Integer>> subsets = new ArrayList<>();

        public void addUniverseItem(String item) { universe.add(item); }
        public void addSubset(String name, Set<Integer> elements) {
            setNames.add(name);
            subsets.add(elements);
        }
    }

    public static class GreedyResult {
        public List<Integer> chosenSetIndices = new ArrayList<>();
        public double harmonicBound;
        public long runtimeNs;
    }

    public static GreedyResult solveGreedy(SetCoverInstance inst) {
        GreedyResult res = new GreedyResult();
        long t1 = System.nanoTime();

        int universeSize = inst.universe.size();
        Set<Integer> uncovered = new HashSet<>();
        for (int i = 0; i < universeSize; i++) uncovered.add(i);

        int maxSubsetSize = 0;
        for (Set<Integer> s : inst.subsets) maxSubsetSize = Math.max(maxSubsetSize, s.size());

        // Calculate Harmonic number H(d)
        double harmonic = 0.0;
        for (int k = 1; k <= maxSubsetSize; k++) harmonic += 1.0 / k;
        res.harmonicBound = harmonic;

        Set<Integer> picked = new HashSet<>();

        while (!uncovered.isEmpty()) {
            int bestSetIdx = -1;
            int bestCoverCount = 0;

            for (int i = 0; i < inst.subsets.size(); i++) {
                if (picked.contains(i)) continue;
                int count = 0;
                for (int elem : inst.subsets.get(i)) {
                    if (uncovered.contains(elem)) count++;
                }
                if (count > bestCoverCount) {
                    bestCoverCount = count;
                    bestSetIdx = i;
                }
            }

            if (bestSetIdx == -1 || bestCoverCount == 0) break; // Cannot cover remaining

            picked.add(bestSetIdx);
            res.chosenSetIndices.add(bestSetIdx);
            for (int elem : inst.subsets.get(bestSetIdx)) {
                uncovered.remove(elem);
            }
        }

        res.runtimeNs = System.nanoTime() - t1;
        return res;
    }

    public static void run(Scanner scanner) {
        System.out.println("========== GREEDY SET COVER APPROXIMATION ALGORITHM ==========");
        System.out.println("Biological Context: Multiplex PCR Primer Multiplexing (Covering Target Pathogen Genes)");
        System.out.println("Use Default Pathogen Target Gene Set? (Y/N, default Y): ");
        String choice = scanner.hasNextLine() ? scanner.nextLine().trim() : "";

        SetCoverInstance inst = new SetCoverInstance();

        if (choice.equalsIgnoreCase("N")) {
            System.out.print("Enter universe size (number of target mutations): ");
            int uSize = Integer.parseInt(scanner.nextLine().trim());
            for (int i = 0; i < uSize; i++) {
                inst.addUniverseItem("Target-Mutation-" + (i + 1));
            }
            System.out.print("Enter number of candidate primer pools: ");
            int numSets = Integer.parseInt(scanner.nextLine().trim());
            for (int i = 0; i < numSets; i++) {
                System.out.print("Primer pool name " + (i + 1) + ": ");
                String name = scanner.nextLine().trim();
                System.out.print("Enter space-separated target indices (0-indexed, up to " + (uSize - 1) + "): ");
                String[] parts = scanner.nextLine().trim().split("\\s+");
                Set<Integer> setElems = new HashSet<>();
                for (String p : parts) setElems.add(Integer.parseInt(p));
                inst.addSubset(name, setElems);
            }
        } else {
            // Universe of 7 pathogen diagnostic mutations:
            inst.addUniverseItem("Target_Spike_D614G");
            inst.addUniverseItem("Target_Spike_N501Y");
            inst.addUniverseItem("Target_Spike_E484K");
            inst.addUniverseItem("Target_ORF1a_P323L");
            inst.addUniverseItem("Target_Nucleocapsid_R203K");
            inst.addUniverseItem("Target_Membrane_I82T");
            inst.addUniverseItem("Target_Envelope_T9I");

            // Candidate Primer Pools:
            inst.addSubset("PrimerPool_A [Spike RBD focus]", new HashSet<>(Arrays.asList(0, 1, 2)));
            inst.addSubset("PrimerPool_B [Structural focus]", new HashSet<>(Arrays.asList(2, 4, 5, 6)));
            inst.addSubset("PrimerPool_C [Non-structural core]", new HashSet<>(Arrays.asList(0, 3, 4)));
            inst.addSubset("PrimerPool_D [Broad envelope]", new HashSet<>(Arrays.asList(1, 5, 6)));
            inst.addSubset("PrimerPool_E [Polymerase focus]", new HashSet<>(Arrays.asList(3)));

            System.out.println("\nLoaded 7 target mutations and 5 multiplex primer pools.");
        }

        GreedyResult res = solveGreedy(inst);

        System.out.println("\n================ GREEDY SET COVER RESULT ================");
        System.out.println("Selected Primer Pools Count: " + res.chosenSetIndices.size());
        for (int idx : res.chosenSetIndices) {
            System.out.println("  [CHOSEN] " + inst.setNames.get(idx) + " -> Covers elements: " + inst.subsets.get(idx));
        }
        System.out.println("Execution Time: " + res.runtimeNs + " ns (Polynomial O(|U| * |S|))");

        System.out.println("\n--- PROVABLE APPROXIMATION RATIO GUARANTEE ---");
        System.out.printf("Chvátal-Lovász Harmonic Bound H(d): %.3f%n", res.harmonicBound);
        System.out.println("Mathematical Guarantee: Greedy Set Cover achieves approximation ratio of H(max|S_i|),");
        System.out.println("ensuring Cost(Greedy) <= (ln |U| + 1) * Cost(OPT).");
    }
}
