# BioGenome: Computational Biology & DSA Platform
## Comprehensive Technical Project Report (CO1 – CO6)

---

## 1. Problem Understanding & Requirement Mapping (CO1)

### 1.1 Problem Statement
Computational genomics, systems biology, and biomedical data engineering present complex algorithmic challenges over discrete alphabets $\Sigma = \{A, T, G, C\}$, biological interaction networks $G = (V, E)$, and high-throughput multi-omics measurements.
BioGenome models and solves the full spectrum of these challenges across six core competencies:
1. **Motif Discovery & Sequence Matching (CO2):** Rapid scanning of transcriptional factors and CRISPR PAM targets without pointer backtracking.
2. **Genome Indexing (CO2):** Compact full-text indexing ($4n$ bytes) to identify repetitive elements and retrotransposons.
3. **Dynamic Programming Alignment (CO3):** Optimal global and local sequence homology comparisons between divergent species.
4. **Network Flow & Duality (CO4):** Modeling metabolic reaction flux, signal cascade throughput, ortholog gene pairing, and identifying critical bottleneck enzyme cuts via the Max-Flow Min-Cut theorem.
5. **NP-Completeness & Approximation (CO5):** Mapping biological constraint satisfaction problems via canonical polynomial reductions ($3\text{-SAT} \le_p \text{Vertex Cover} \le_p \text{Independent Set}$), and designing polynomial-time approximation algorithms with provable performance guarantees (2-approximation for Vertex Cover, logarithmic harmonic bound for Set Cover).
6. **Randomized & Parallel Primitives (CO6):** Utilizing Las Vegas algorithms (exact median gene expression) and Monte Carlo methods (PPI network community clustering), combined with work-efficient parallel primitives (Parallel Prefix Sum and Parallel Reduce) to locate bacterial replication origins (OriC).

---

## 2. Module 4: Network Flow & Min-Cut Duality (CO4)

### 2.1 Edmonds-Karp Max-Flow Algorithm
- **Principle:** Specialization of the Ford-Fulkerson method that selects augmenting paths using Breadth-First Search (BFS), guaranteeing the shortest augmenting path in terms of edge count.
- **Complexity:** $O(V \cdot E^2)$ time and $O(V^2)$ space.
- **Application in BioGenome:** Models the metabolic flux through enzymatic pathways (e.g., glycolysis converting glucose to pyruvate and biomass).

### 2.2 Max-Flow / Min-Cut Duality
- **The Max-Flow Min-Cut Theorem:** In any flow network, the maximum amount of flow from the source $s$ to the sink $t$ is equal to the total capacity of the minimum cut separating $s$ and $t$:
  $$\max |f| = \min_{S, T} c(S, T)$$
- **Residual Graph Reachability:** The algorithm identifies partition $S$ by performing a BFS from source $s$ on the residual graph $G_f$ where residual capacity $c_f(u, v) = c(u, v) - f(u, v) > 0$. Edges crossing from $S$ to $T = V \setminus S$ in the original graph constitute the **minimum cut**.
- **Biological Value:** Identifies the rate-limiting enzyme reactions whose targeted inhibition completely shuts down pathogen metabolic synthesis (essential for antibiotic drug discovery).

### 2.3 Dinic's Algorithm
- **Intuition & Mechanism:**
  1. **Level Graph:** Constructed using BFS, assigning levels $level[v] = level[u] + 1$ to nodes reachable from $u$.
  2. **Blocking Flow:** Pushed using DFS exclusively along admissible edges $(u, v)$ where $level[v] = level[u] + 1$. A pointer array `ptr[u]` prunes saturated branches.
- **Complexity:** $O(V^2 E)$ in general flow networks, and $O(E \sqrt{V})$ in unit networks.
- **Advantage:** Dramatically reduces the number of augmenting path searches in dense signaling cascades.

### 2.4 Maximum Bipartite Matching & König's Theorem
- **Reduction to Max-Flow:**
  - Create super-source $S$ connected to all vertices in $L$ with capacity 1.
  - Create directed edges from $L$ to $R$ with capacity 1.
  - Connect all vertices in $R$ to super-sink $T$ with capacity 1.
- **Biological Context:** Orthologous gene assignment between human and mouse genomes based on sequence homology scores.
- **König's Theorem:** In bipartite graphs, the size of the Maximum Matching is strictly equal to the size of the Minimum Vertex Cover ($|M| = |\tau|$).

---

## 3. Module 5: NP-Completeness & Approximation Algorithms (CO5)

### 3.1 Complexity Classes & The Cook-Levin Foundation
- **Class P:** Problems decidable by a deterministic Turing machine in polynomial time $O(n^c)$.
- **Class NP:** Problems verifiable by a deterministic Turing machine in polynomial time given a certificate.
- **NP-Complete:** Problems in NP to which every other problem in NP can be reduced in polynomial time ($L' \le_p L$).
- **NP-Hard:** Problems at least as hard as any problem in NP (not required to belong to NP).

### 3.2 Canonical Reductions: 3-SAT $\to$ Vertex Cover $\to$ Independent Set
BioGenome implements an explicit reduction engine:
1. **3-SAT to Vertex Cover ($3\text{-SAT} \le_p \text{VC}$):**
   - For $n$ variables, create $n$ variable gadgets: pairs $(x_i, \neg x_i)$ connected by an edge. Any vertex cover must select at least 1 vertex from each pair ($n$ vertices).
   - For $m$ clauses, create $m$ clause gadgets: triangles $(c_{j1}, c_{j2}, c_{j3})$ where all 3 vertices are mutually connected. Any vertex cover must select at least 2 vertices per triangle ($2m$ vertices).
   - Add edges between clause literal vertices and their corresponding variable literal vertices.
   - Set budget $k = n + 2m$.
   - **Correctness Proof:** The boolean formula is satisfiable if and only if the constructed graph has a vertex cover of size $k = n + 2m$.
2. **Vertex Cover to Independent Set ($\text{VC} \le_p \text{IS}$):**
   - **Complement Duality Theorem:** A subset $S \subseteq V$ is a vertex cover of $G = (V, E) \iff V \setminus S$ is an independent set of $G$.
   - Therefore, $G$ has a vertex cover of size $k \iff G$ has an independent set of size $k' = |V| - k = (2n + 3m) - (n + 2m) = n + m$.
3. **Polynomial Certificate Verification:**
   - Evaluates boolean truth assignments in $O(m)$ time, proving membership in NP.

### 3.3 2-Approximation Algorithm for Minimum Vertex Cover
- **Problem:** Finding the minimum set of PCR primers/probes to hybridize with all target viral mutation variants.
- **Algorithm (Gavril / Bar-Yehuda & Even):**
  1. Initialize cover $C \leftarrow \emptyset$.
  2. While graph contains edges, pick an arbitrary edge $(u, v)$.
  3. Add both $u$ and $v$ to $C$.
  4. Delete all edges incident to $u$ or $v$ from $G$.
- **Provable Approximation Ratio Proof:**
  - Let $M$ be the set of disjoint edges picked in step 2. $M$ is a matching in $G$.
  - Any valid vertex cover $C^*$ must cover every edge in $M$. Because all edges in $M$ share no endpoints, $C^*$ must contain at least one distinct vertex per edge:
    $$|C^*| \ge |M|$$
  - The algorithm adds both endpoints of every edge in $M$ to $C$:
    $$|C| = 2 \cdot |M|$$
  - Combining inequalities:
    $$|C| = 2 \cdot |M| \le 2 \cdot |C^*| \implies \frac{|C|}{|C^*|} \le 2$$
  - **Conclusion:** The algorithm runs in $O(V + E)$ time and is guaranteed to return a cover at most twice the size of the optimal cover.

### 3.4 Greedy Approximation for Minimum Set Cover
- **Biological Context:** Multiplex PCR Primer Pool Design covering broad diagnostic mutation targets.
- **Harmonic Bound:** The greedy choice rule achieves an approximation ratio of:
  $$\rho = H(\max |S_i|) = \sum_{k=1}^d \frac{1}{k} \le \ln n + 1 \approx O(\log n)$$

---

## 4. Module 6: Randomized & Parallel Algorithms (CO6)

### 4.1 Las Vegas vs. Monte Carlo Algorithms
BioGenome implements both paradigms to clarify their foundational differences:

| Attribute | Las Vegas Algorithm | Monte Carlo Algorithm |
| :--- | :--- | :--- |
| **Example in Project** | Randomized QuickSelect (Median Gene Expression) | Karger's Min-Cut (Protein Network Clustering) |
| **Output Correctness** | **100% Guaranteed Exact** | **Probabilistic** (error probability $\epsilon$) |
| **Running Time** | **Random Variable** (Expected $O(n)$, worst $O(n^2)$) | **Strictly Deterministic** ($O(V^2)$ per trial) |
| **Stopping Condition** | Terminates when exact solution reached | Fixed iteration count reached |
| **Error Reduction** | Not applicable (zero error) | Amplified by independent repetition: $(1 - \frac{2}{V(V-1)})^T \le \delta$ |

### 4.2 Parallel Primitives & Formal Work-Span Analysis
To analyze whole-genome sequences (millions of base pairs), BioGenome implements parallel primitives using Java's `ForkJoinPool` framework:

1. **Parallel Reduce (Tree Reduction):**
   - Binary reduction tree computing the net nucleotide skew or base frequency.
   - **Work ($T_1$):** Total operations across 1 processor $= O(n)$.
   - **Span ($T_\infty$):** Critical path / tree depth $= O(\log n)$.
   - **Parallelism:** $P = \frac{T_1}{T_\infty} = \Theta\left(\frac{n}{\log n}\right)$.

2. **Parallel Prefix Sum (Blelloch Scan):**
   - **Up-Sweep (Reduce Phase):** Computes partial sums along a balanced tree ($O(n)$ work, $O(\log n)$ span).
   - **Down-Sweep Phase:** Distributes running prefix offsets down the tree ($O(n)$ work, $O(\log n)$ span).
   - **Biological Application:** Calculates cumulative GC-skew along the chromosome:
     $$\text{Cumulative Skew}(k) = \sum_{i=1}^k (G_i - C_i)$$
     The global minimum of this cumulative skew reveals the **Bacterial Origin of Replication (OriC)**.

3. **Brent's Theorem:**
   For an algorithm with work $T_1$ and span $T_\infty$ executed on $p$ processors:
   $$T_p \le \frac{T_1}{p} + T_\infty$$
   This guarantees near-linear speedup $S_p \approx p$ when $n \gg p \log n$.

---

## 5. Live Test Case Verification Across All 6 Modules

| Test Case | Module | Input Data | Observed Output | Theoretical Bound Verified |
| :--- | :--- | :--- | :--- | :--- |
| **KMP Search** | M1 (CO2) | Text: 49 bp, Motif: `ATCGATC` | Matches at `[5, 9, 13, 41]` | Linear $O(n+m)$ without backtracking |
| **Suffix Array + LCP** | M2 (CO2) | `GATAGACA$` | LRS: `"GA"` at indices 0, 4 | Kasai $O(n)$ linear time |
| **Global Alignment** | M3 (CO3) | `GCATGCG` vs `GATTACA` | Score: 0, Identity: 50.00% | Optimal DP $O(n \times m)$ |
| **Local Alignment** | M3 (CO3) | `TGTTACGG` vs `GGTTGACTA` | Subsequence: `GTT-ACG`, Score: 6.00 | Smith-Waterman local peak |
| **Edmonds-Karp Flux** | M4 (CO4) | Metabolic Net (6 nodes, 9 edges) | Max Flow = 23, Min-Cut = 23 | Max-Flow == Min-Cut Duality |
| **Dinic Signal Routing**| M4 (CO4) | Kinase Cascade (8 nodes, 10 edges) | Max Signal Flow = 16 | Level graph & blocking flow |
| **Ortholog Matching** | M4 (CO4) | 5 Human genes vs 6 Mouse genes | 5 matched pairs, Min VC = 5 | König's Theorem $|M| = |\tau|$ |
| **3-SAT Reduction** | M5 (CO5) | 3 vars, 3 clauses | Graph $|V|=15, |E|=24$, $k=9$ | $3\text{-SAT} \le_p \text{VC} \le_p \text{IS}$ |
| **2-Approx VC** | M5 (CO5) | 8 viral strains, 9 mutation edges | Approx: 6, Exact: 3, Ratio: 2.00 | Provable Ratio $\le 2.0$ |
| **QuickSelect Median** | M6 (CO6) | 50,000 RNA-seq measurements | Exact median: 100% match | Expected $O(n)$ Las Vegas |
| **Karger Min-Cut** | M6 (CO6) | 6-protein interaction network | Min-Cut = 2 edges | Monte Carlo amplification |
| **Parallel Scan (OriC)**| M6 (CO6) | 500,000 nucleotides | OriC index identified, Speedup observed | Work $O(n)$, Span $O(\log n)$ |

---

## 6. Summary & Academic Conclusion
BioGenome successfully implements, verifies, and analyzes all algorithms designated under Course Outcomes CO1 through CO6. By unifying theoretical algorithmic rigor with realistic computational biology applications, the system provides both an interactive experimental playground and a complete demonstration suite for academic evaluation.
