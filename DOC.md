# BioGenome: Quick Reference Tables (Modules 1–6)

---

## 1. Project Purpose (In Short)

| Purpose | Real-World Application | Course Outcome |
| :--- | :--- | :--- |
| **Fast DNA Search** | Finds virus signatures & disease-causing genes in seconds instead of hours. | **CO2** (String Matching) |
| **Genome Indexing** | Indexes 3 billion base pairs in memory to find repeated DNA patterns. | **CO2** (Suffix Structures) |
| **Sequence Alignment** | Compares human vs. animal genes to detect mutations and evolutionary links. | **CO3** (Advanced DP) |
| **Metabolic Network Flow** | Models biochemical throughput and locates critical drug-target enzyme bottlenecks. | **CO4** (Network Flow) |
| **NP Analysis & Approximation** | Maps NP-hard genomic problems (primer selection) and solves with bounded ratios. | **CO5** (NP-Completeness) |
| **Randomized & Parallel Scan** | Accelerates whole-genome replication origin (OriC) scans using multi-core processors. | **CO6** (Random & Parallel) |

---

## 2. Module 1: DNA Motif Matching (CO2)

| Algorithm | Why We Use It? (Why this?) | What Does It Do in Project? | Time Complexity | Real Bio Example |
| :--- | :--- | :--- | :--- | :--- |
| **KMP** | Never backtracks in text pointer using LPS table. | Finds exact position of 1 motif in DNA sequence. | $O(n + m)$ | Locating a specific cancer gene mutation. |
| **Rabin-Karp** | Compares integer hashes; slides window in $O(1)$ time. | Scans DNA stream using rolling hash matching. | $O(n + m)$ avg | Detecting repeated viral sequence blocks. |
| **Aho-Corasick** | Searches all $k$ patterns together in 1 single pass (Trie + failure links). | Finds multiple motifs at once without rescanning. | $O(n + L)$ | Scanning 50+ CRISPR-Cas9 PAM target sites. |

---

## 3. Module 2: Suffix & Indexing Structures (CO2)

| Algorithm | Why We Use It? (Why this?) | What Does It Do in Project? | Time Complexity | Real Bio Example |
| :--- | :--- | :--- | :--- | :--- |
| **Suffix Array** | Uses 80% less RAM than Suffix Trees (only 4 bytes/char). | Sorts all suffixes to enable fast binary search. | $O(n \log n)$ build, $O(m \log n)$ search | Full-text indexing of whole chromosome. |
| **Kasai's LCP** | Computes Longest Common Prefix in linear time. | Finds the Longest Repeated Substring (LRS). | $O(n)$ | Detecting transposable repeat elements (transposons). |

---

## 4. Module 3: Sequence Alignment via DP (CO3)

| Algorithm | Why We Use It? (Why this?) | What Does It Do in Project? | Time Complexity | Real Bio Example |
| :--- | :--- | :--- | :--- | :--- |
| **Needleman-Wunsch** | DP guarantees optimal global end-to-end alignment. | Aligns full sequences with matches, mismatches, gaps. | $O(n \times m)$ | Comparing Human vs. Chimp Beta-Globin genes. |
| **Smith-Waterman** | Uses $\max(0, \dots)$ floor to ignore non-matching ends. | Isolates best matching local conserved sub-region. | $O(n \times m)$ | Finding an active protein domain in unknown bacteria. |

---

## 5. Module 4: Network Flow & Min-Cut Duality (CO4)

| Algorithm | Why We Use It? (Why this?) | What Does It Do in Project? | Time Complexity | Real Bio Example |
| :--- | :--- | :--- | :--- | :--- |
| **Edmonds-Karp** | Implements Ford-Fulkerson using BFS shortest paths to avoid infinite loops. | Computes maximum biochemical flux through cell pathway. | $O(V \cdot E^2)$ | Maximum ATP synthesis capacity in glycolysis. |
| **Min-Cut Duality** | Reachability in residual graph isolates critical network bottleneck edges. | Identifies bottleneck reactions where Max-Flow == Min-Cut. | $O(V + E)$ after flow | Targeting essential antimicrobial drug enzymes. |
| **Dinic's Algorithm** | Uses BFS Level Graphs + DFS Blocking Flows to eliminate path re-scans. | High-throughput signal routing through complex cascade nets. | $O(V^2 E)$ general, $O(E \sqrt{V})$ unit | Multi-tier kinase cascade signal transduction. |
| **Bipartite Matching** | Reduces 1-to-1 matching to Max-Flow with source $S$ and sink $T$. | Finds maximum orthologous gene pairing and minimum vertex cover. | $O(V \cdot E)$ | Pairing Human oncogenes to Mouse functional homologs. |

---

## 6. Module 5: NP-Completeness & Approximation (CO5)

| Algorithm / Concept | Why We Use It? (Why this?) | What Does It Do in Project? | Complexity / Ratio | Real Bio Example |
| :--- | :--- | :--- | :--- | :--- |
| **3-SAT $\to$ VC $\to$ IS Reduction** | Canonical polynomial gadgets prove NP-completeness of biological selection. | Transforms genomic boolean constraints to Vertex Cover & Independent Set. | $O(n + m)$ poly reduction | Proving primer selection and motif packing are NP-hard. |
| **NP Certificate Verifier** | Verifies prospective solution in polynomial time. | Confirms candidate gene state satisfies regulatory rules in $O(m)$ time. | $O(m)$ polynomial | Certifying co-expression gene regulatory networks. |
| **Vertex Cover 2-Approx** | Maximal matching selects disjoint edges; provable ratio $|C| \le 2|C^*|$. | Selects minimal PCR primer set covering all pathogen mutant strains. | Ratio $\le 2.0$, $O(V + E)$ time | Diagnostic primer multiplexing for COVID/Flu variants. |
| **Greedy Set Cover Approx** | Picks pool covering most uncovered targets per step; provably bounded by $H_d$. | Selects minimal primer pools to cover broad target gene sets. | Ratio $\le \ln n + 1$, $O(|U| \cdot |S|)$ | Minimum multiplex assay library construction. |

---

## 7. Module 6: Randomized & Parallel Algorithms (CO6)

| Algorithm / Concept | Why We Use It? (Why this?) | What Does It Do in Project? | Time / Work-Span | Real Bio Example |
| :--- | :--- | :--- | :--- | :--- |
| **Las Vegas (QuickSelect)** | Always guaranteed 100% exact result; random pivot guarantees expected linear time. | Finds median gene expression level across tens of thousands of RNA-seq reads. | Expected $O(n)$, zero error | Normalizing single-cell RNA-seq expression profiles. |
| **Monte Carlo (Karger Min-Cut)** | Running time is strictly deterministic; error bounded and reduced exponentially by repeats. | Partitions protein-protein interaction (PPI) networks into functional clusters. | $O(V^2)$ per trial, error $\le \delta$ | Detecting modular protein complexes in interactomes. |
| **Parallel Reduce** | Binary tree reduction across multi-core processors using ForkJoin. | Computes total base composition or GC balance across full genome. | Work $T_1 = O(n)$, Span $T_\infty = O(\log n)$ | Instantaneous genome-wide GC-content calculation. |
| **Parallel Prefix Sum (Scan)** | Blelloch Up-Sweep (reduce) + Down-Sweep (offsets) primitive. | Computes cumulative $(G - C)$ skew along chromosomes to pinpoint OriC. | Work $T_1 = O(n)$, Span $T_\infty = O(\log n)$ | Locating bacterial replication origin (OriC) in seconds. |

---

## 8. One-Line Viva Cheat Sheet (M1–M6)

| Question | Short Answer (Direct for Examiner) |
| :--- | :--- |
| **Why KMP over Naive?** | KMP uses LPS array to avoid text backtracking, guaranteeing strict $O(n+m)$ time. |
| **Why Aho-Corasick over KMP?** | KMP scans text $k$ times for $k$ patterns; Aho-Corasick scans text only once ($O(n)$) via Trie + failure links. |
| **Why Suffix Array over Suffix Tree?** | Suffix Array takes $4n$ bytes (integers), saving over 80% RAM compared to Suffix Trees. |
| **Needleman-Wunsch vs Smith-Waterman?** | Needleman-Wunsch = Global alignment (end-to-end); Smith-Waterman = Local alignment ($\max(0,\dots)$ floor for conserved domains). |
| **Edmonds-Karp vs Dinic?** | Edmonds-Karp uses BFS paths in $O(V E^2)$; Dinic uses BFS Level Graphs + DFS Blocking Flows in $O(V^2 E)$ (or $O(E \sqrt{V})$ for unit networks). |
| **What is Max-Flow / Min-Cut Duality?** | The maximum net flow from $S$ to $T$ equals the minimum capacity among all cuts partitioning $S$ and $T$. |
| **Why is Vertex Cover NP-Complete?** | Because 3-SAT reduces to Vertex Cover in polynomial time ($3\text{-SAT} \le_p \text{Vertex Cover}$) using variable gadgets and clause triangles. |
| **Why is Maximal Matching Vertex Cover 2-Approx?** | Any vertex cover must pick at least 1 endpoint per edge in matching $M$ ($|C^*| \ge |M|$); the heuristic picks both endpoints ($|C| = 2|M| \le 2|C^*|$). |
| **Las Vegas vs Monte Carlo?** | Las Vegas is always 100% correct with random runtime (QuickSelect); Monte Carlo has fixed runtime with bounded error probability (Karger Min-Cut). |
| **What is Work and Span in Parallel Algorithms?** | Work ($T_1$) is total operations executed on 1 processor; Span ($T_\infty$) is the critical path depth on $\infty$ processors. Parallelism is $T_1 / T_\infty$. |
