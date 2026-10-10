# BioGenome: Computational Biology & DSA Platform
> **Complete Implementation Covering Modules 1–6 (CO1–CO6)**

BioGenome is a high-performance computational genomics and data structures platform. It maps foundational and advanced algorithms in computer science directly onto critical biomedical problems—ranging from linear DNA motif matching and genome indexing, to network flow metabolic modeling, NP-completeness reductions, and multi-core parallel primitives.

---

## Folder Structure

```
dsaproject/
│
├── src/
│   ├── Main.java                 # Interactive CLI & 1-click comprehensive demonstration (M1–M6)
│   │
│   ├── m1/                       # Module 1: DNA Motif Matching (CO2)
│   │   ├── KMP.java              # Knuth-Morris-Pratt (LPS preprocessing, linear O(n+m))
│   │   ├── RabinKarp.java        # Rolling hash pattern matching (O(1) window shift)
│   │   └── AhoCorasick.java      # Multi-motif Trie automaton (CRISPR PAM scanning)
│   │
│   ├── m2/                       # Module 2: Suffix & Indexing Structures (CO2)
│   │   └── SuffixArray.java      # Suffix Array + Kasai's LCP Array + Longest Repeat Finder
│   │
│   ├── m3/                       # Module 3: Sequence Alignment via DP (CO3)
│   │   ├── NeedlemanWunsch.java  # Global sequence alignment with 2D matrix traceback
│   │   └── SmithWaterman.java    # Local alignment for conserved domain discovery
│   │
│   ├── m4/                       # Module 4: Network Flow & Min-Cut Duality (CO4)
│   │   ├── EdmondsKarp.java      # Ford-Fulkerson with BFS & Min-Cut metabolic bottleneck duality
│   │   ├── Dinic.java            # Dinic's Algorithm (Level Graph & Blocking Flow)
│   │   └── BipartiteMatching.java# Ortholog gene pairing via Max-Flow reduction & König's theorem
│   │
│   ├── m5/                       # Module 5: NP-Completeness & Approximation Algorithms (CO5)
│   │   ├── Reduction3SATtoVC.java# Canonical reduction (3-SAT -> Vertex Cover -> Independent Set)
│   │   ├── VertexCoverApprox.java# 2-Approximation Vertex Cover with provable ratio proof
│   │   └── SetCoverGreedy.java   # Greedy Set Cover with Harmonic H(d) bound for PCR primer pools
│   │
│   └── m6/                       # Module 6: Randomized & Parallel Algorithms (CO6)
│       ├── RandomizedAlgorithms.java # Las Vegas (QuickSelect) vs. Monte Carlo (Karger Min-Cut)
│       └── ParallelPrimitives.java   # Parallel Prefix Sum, Parallel Reduce & Work-Span analysis
│
├── bin/                          # Compiled bytecode (.class files)
│
├── docs/                         # Project Reports and Viva Documentation
│   ├── COMPLETE_PROJECT_REPORT.md# Comprehensive 6-Module report covering CO1 to CO6
│   ├── REVIEW_1_REPORT.md        # Original Review-1 milestone report (CO1 to CO3)
│   └── VIVA_QUESTIONS.md         # Complete Viva preparation Q&A guide for CO1–CO6
│
├── test_data/                    # Biological test datasets for live evaluation
│   ├── sars_cov2_spike.txt       # Spike protein receptor-binding motif dataset
│   ├── alignment_pairs.txt       # Homology test sequences for Needleman/Smith-Waterman
│   ├── crispr_motifs.txt         # Multi-motif PAM target dataset
│   ├── metabolic_flux_network.txt# Metabolic pathway reaction network for max-flow
│   ├── ortholog_gene_matching.txt# Human-Mouse orthologous gene pairs
│   ├── viral_primer_cover.txt    # 3-SAT genomic regulatory formula & viral mutant graph
│   └── gc_skew_parallel_sequence.txt # Chromosome segment for parallel OriC replication scan
│
├── run.bat                       # 1-Click Windows execution script (cmd)
└── run.ps1                       # 1-Click PowerShell launcher script
```

---

## How to Run

### Option 1: Double-click `run.bat` (Windows Command Prompt)
Double-click `run.bat` or run in terminal:
```cmd
.\run.bat
```

### Option 2: Using PowerShell `run.ps1`
In PowerShell terminal:
```powershell
.\run.ps1
```

---

## Complete Core Algorithms Matrix (CO1 to CO6)

| Module | Outcome | Algorithm | Complexity / Bounds | Real Biological Use Case |
| :--- | :--- | :--- | :--- | :--- |
| **M1** | **CO2** | **KMP** | $O(n + m)$ time, $O(m)$ space | Finding single oncogene mutation without text pointer backtracking |
| **M1** | **CO2** | **Rabin-Karp** | $O(n + m)$ avg, $O(1)$ space | Rolling-hash DNA stream scanning and fingerprinting |
| **M1** | **CO2** | **Aho-Corasick** | $O(n + L)$ time, $O(L)$ space | Concurrent multi-motif search for 50+ CRISPR-Cas9 PAM target sites |
| **M2** | **CO2** | **Suffix Array + Kasai LCP** | $O(n \log n)$ SA, $O(n)$ LCP | Full genome indexing (saving 80% RAM) & Longest Repeated Substring |
| **M3** | **CO3** | **Needleman-Wunsch** | $O(n \times m)$ time & space | End-to-end Global Sequence Homology alignment with 2D traceback |
| **M3** | **CO3** | **Smith-Waterman** | $O(n \times m)$ time & space | Local sequence alignment to locate conserved protein functional domains |
| **M4** | **CO4** | **Edmonds-Karp & Min-Cut** | $O(V \cdot E^2)$ time, $O(V^2)$ space | Cellular metabolic reaction flux maximization & bottleneck enzyme cut |
| **M4** | **CO4** | **Dinic's Algorithm** | $O(V^2 E)$ general, $O(E \sqrt{V})$ unit | High-throughput phosphorylation kinase signal cascade transmission |
| **M4** | **CO4** | **Bipartite Matching** | $O(V \cdot E)$ via flow reduction | 1-to-1 Orthologous gene pairing (Human vs Mouse) & König's cover |
| **M5** | **CO5** | **3-SAT $\to$ VC $\to$ IS Reduction** | $O(n + m)$ polynomial reduction | Proving NP-completeness of biomedical regulatory rule satisfaction |
| **M5** | **CO5** | **Vertex Cover 2-Approx** | Provable ratio $\le 2.0$, $O(V + E)$ | Selecting minimum PCR primer sets to cover all pathogen mutant strains |
| **M5** | **CO5** | **Greedy Set Cover Approx** | Bound $H(d) \le \ln n + 1$ | Multiplex diagnostic primer pool optimization covering target universe |
| **M6** | **CO6** | **Las Vegas (QuickSelect)** | Expected $O(n)$, zero error | Calculating exact median gene expression level across 50,000 RNA-seq reads |
| **M6** | **CO6** | **Monte Carlo (Karger Min-Cut)**| Fixed $O(V^2)$, error $\le \delta$ | Protein-protein interaction (PPI) network clustering via random contractions |
| **M6** | **CO6** | **Parallel Prefix Sum (Scan)** | Work $O(n)$, Span $O(\log n)$ | Rapid chromosome-scale cumulative GC-skew scanning to pinpoint bacterial OriC |
| **M6** | **CO6** | **Parallel Reduce** | Work $O(n)$, Span $O(\log n)$ | Multi-core parallel tree reduction for net genomic nucleotide composition |
