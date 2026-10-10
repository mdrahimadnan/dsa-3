# BioGenome: Complete Individual Viva & Technical Q&A Guide
## Preparation for Review & Final Evaluation (CO1 – CO6)

---

### Category 1: Problem Understanding & Requirement Mapping (CO1)

#### Q1: What is the goal of the BioGenome project?
**Answer:** BioGenome applies core and advanced DSA concepts to computational genomics. Specifically, it provides efficient algorithms for:
1. DNA motif discovery (KMP, Rabin-Karp, Aho-Corasick) [CO2].
2. Suffix indexing and genomic repeat detection (Suffix Array with Kasai's LCP) [CO2].
3. Pairwise sequence alignment (Needleman-Wunsch and Smith-Waterman) [CO3].
4. Metabolic network flux modeling & bottleneck drug target cuts (Edmonds-Karp, Dinic, Bipartite Matching) [CO4].
5. NP-completeness reductions & provable approximation algorithms (3-SAT to Vertex Cover, 2-Approx VC, Greedy Set Cover) [CO5].
6. Randomized algorithms & multi-core parallel primitives (Las Vegas, Monte Carlo, Parallel Prefix Scan for OriC) [CO6].

#### Q2: Why choose these algorithms instead of naive approaches?
**Answer:** Genomes contain millions to billions of nucleotides, and cellular networks contain thousands of reactions. Naïve quadratic string matching, exponential NP searches, or un-parallelized scans are completely impractical. By utilizing linear-time pattern matching ($O(n+m)$), compact integer arrays ($4n$ bytes), polynomial network flow ($O(V E^2)$), bounded approximation ratios ($\le 2.0$), and work-efficient parallel tree scans ($O(\log n)$ span), BioGenome delivers provably optimal solutions.

---

### Category 2: String Algorithms & Motif Search (CO2)

#### Q3: How does KMP eliminate text backtracking?
**Answer:** KMP preprocesses the motif into an LPS (Longest Proper Prefix which is also a Suffix) array. When a mismatch occurs at $pattern[j]$, instead of resetting the text pointer $i$, the pattern index jumps to $j = LPS[j-1]$. This guarantees the text pointer $i$ never moves backward, achieving guaranteed $O(n + m)$ linear time.

#### Q4: How does Rabin-Karp achieve $O(1)$ window transitions?
**Answer:** Rabin-Karp uses a polynomial rolling hash. When sliding the window by one position, it subtracts the outgoing character, multiplies by the base $b$, adds the incoming character, and applies modulo $q$:
$$H_{new} = ((H_{old} - T[i] \cdot b^{m-1}) \cdot b + T[i+m]) \pmod q$$
This computes each successive hash in $O(1)$ arithmetic operations.

#### Q5: When is Aho-Corasick preferred over KMP?
**Answer:** KMP is designed for a single pattern. If we have $k$ motifs (e.g. CRISPR PAM targets), running KMP $k$ times takes $O(k \cdot n)$ time. Aho-Corasick builds a Trie with BFS failure links in $O(L)$ time, and then searches all $k$ patterns simultaneously across the genome in a single linear pass of $O(n + \text{matches})$.

---

### Category 3: Suffix Structures & Indexing (CO2)

#### Q6: What is a Suffix Array, and why is it preferred over a Suffix Tree?
**Answer:** A Suffix Array contains the starting indices of all suffixes of a string sorted in lexicographical order. While a Suffix Tree requires 20–45 bytes of pointer storage per character, a Suffix Array requires only 4 bytes per character ($4n$ bytes), reducing memory consumption by over 80% while retaining sub-linear $O(m \log n)$ substring search.

#### Q7: What does Kasai's algorithm do, and why is its time complexity $O(n)$?
**Answer:** Kasai's algorithm computes the Longest Common Prefix (LCP) between adjacent sorted suffixes in the Suffix Array. It runs in $O(n)$ because it processes suffixes in original text order rather than suffix array order, exploiting the theorem that $LCP[rank[i+1]] \ge LCP[rank[i]] - 1$. The maximum value in the LCP array identifies the Longest Repeated Substring (LRS).

---

### Category 4: Dynamic Programming & Alignment (CO3)

#### Q8: What is the difference between Needleman-Wunsch and Smith-Waterman?
**Answer:**
- **Needleman-Wunsch (Global Alignment):** Aligns the entire length of both sequences end-to-end. Its DP matrix is initialized with cumulative gap penalties ($dp[i][0] = i \cdot \text{gap}$), and traceback runs from $dp[n][m]$ to $dp[0][0]$.
- **Smith-Waterman (Local Alignment):** Finds the highest-scoring homologous sub-region. Its recurrence includes a floor of zero ($\max(0, \dots)$), preventing negative regions from accumulating. Traceback starts at the cell with the **maximum value in the whole matrix** and terminates when a cell with value $0$ is reached.

---

### Category 5: Network Flow & Min-Cut Duality (CO4)

#### Q9: What is the Max-Flow Min-Cut Theorem, and how is the cut extracted?
**Answer:** The maximum flow passing from source $S$ to sink $T$ equals the minimum total capacity of edges that, if removed, disconnect $S$ from $T$.
To extract the Min-Cut: run BFS on the residual graph starting from $S$ along edges with remaining residual capacity $c_f(u, v) > 0$. The set of reachable nodes forms $S_{cut}$. All edges going from $S_{cut}$ to $V \setminus S_{cut}$ in the original graph form the bottleneck **Min-Cut**.

#### Q10: Why does Edmonds-Karp guarantee termination while standard Ford-Fulkerson can loop indefinitely?
**Answer:** Standard Ford-Fulkerson can use arbitrary augmenting paths, which can oscillate indefinitely with irrational capacities. Edmonds-Karp specifically finds augmenting paths using **BFS (shortest path in number of edges)**. This guarantees that the shortest path distance monotonically increases, bounding augmenting path discoveries to at most $O(V \cdot E)$ iterations, resulting in $O(V \cdot E^2)$ runtime.

#### Q11: How does Dinic's Algorithm improve upon Edmonds-Karp?
**Answer:** Dinic constructs a **Level Graph** using BFS, and then pushes multiple paths simultaneously using DFS to find a **Blocking Flow** in $O(V \cdot E)$ per phase. Because each phase strictly increases the sink's level, there are at most $V - 1$ phases, achieving $O(V^2 E)$ total runtime (and $O(E \sqrt{V})$ on unit bipartite graphs).

#### Q12: How is Maximum Bipartite Matching reduced to Max-Flow?
**Answer:** Add a super-source $S$ with capacity 1 to all left partition vertices (e.g., Human genes), direct all bipartite edges from left to right with capacity 1, and connect all right partition vertices (e.g., Mouse genes) to a super-sink $T$ with capacity 1. The maximum flow value equals the maximum cardinality matching. By König's theorem, this also equals the minimum vertex cover size.

---

### Category 6: NP-Completeness & Approximation Algorithms (CO5)

#### Q13: What are the differences between P, NP, NP-Complete, and NP-Hard?
**Answer:**
- **P:** Problems solvable in polynomial time $O(n^c)$ by a deterministic Turing machine.
- **NP:** Problems whose solutions can be verified in polynomial time by a deterministic Turing machine given a certificate.
- **NP-Complete:** The hardest problems in NP; every problem in NP reduces to them in polynomial time ($X \in \text{NP}$ and $\forall Y \in \text{NP}, Y \le_p X$).
- **NP-Hard:** Problems to which any NP problem can be reduced, but they need not be in NP themselves.

#### Q14: How does the canonical reduction 3-SAT $\to$ Vertex Cover work?
**Answer:**
1. **Variable Gadget:** For each variable $x_i$, create an edge between literal nodes $(x_i, \neg x_i)$. At least 1 node must be in any cover ($n$ nodes).
2. **Clause Gadget:** For each 3-literal clause, create a triangle of 3 nodes. At least 2 nodes must be in any cover ($2m$ nodes).
3. **Connecting Edges:** Connect each clause literal node to its corresponding variable node.
4. **Budget:** Set target $k = n + 2m$. The 3-SAT formula is satisfiable if and only if the graph has a vertex cover of size $k$.

#### Q15: Prove that the Maximal Matching heuristic for Vertex Cover achieves a 2-approximation ratio.
**Answer:**
1. Let $M$ be a maximal matching (disjoint edges with no shared vertices).
2. Any valid vertex cover $C^*$ must contain at least one endpoint from each edge in $M$. Because edges in $M$ are disjoint:
   $$|C^*| \ge |M|$$
3. The algorithm selects both endpoints of every edge in $M$, so $|C_{\text{approx}}| = 2 \cdot |M|$.
4. Therefore:
   $$|C_{\text{approx}}| = 2 \cdot |M| \le 2 \cdot |C^*| \implies \frac{|C_{\text{approx}}|}{|C^*|} \le 2.0$$

#### Q16: What is the approximation guarantee for Greedy Set Cover?
**Answer:** Greedy Set Cover iteratively picks the subset that covers the maximum number of remaining uncovered elements. It guarantees an approximation ratio of:
$$\rho = H(d) = \sum_{i=1}^d \frac{1}{i} \le \ln n + 1 \approx O(\log n)$$
where $d$ is the maximum subset size.

---

### Category 7: Randomized & Parallel Algorithms (CO6)

#### Q17: What is the fundamental difference between Las Vegas and Monte Carlo algorithms?
**Answer:**
- **Las Vegas (e.g., Randomized QuickSelect):** Output is **always 100% correct**. The running time is a random variable (expected $O(n)$ time).
- **Monte Carlo (e.g., Karger's Min-Cut):** Running time is **strictly fixed and deterministic** ($O(V^2)$). The output has a bounded probability of error $\epsilon$, which is exponentially diminished by running repeated independent trials.

#### Q18: What is Work and Span in Parallel Algorithm analysis?
**Answer:**
- **Work ($T_1$):** Total number of basic operations executed across all processors (equivalent to running on 1 processor).
- **Span ($T_\infty$):** Length of the critical path / longest dependency chain in the directed acyclic graph of computations (equivalent to running on $\infty$ processors).
- **Parallelism:** $P = \frac{T_1}{T_\infty}$.

#### Q19: How does the Blelloch Parallel Prefix Sum work, and how does it locate OriC?
**Answer:**
- **Phase 1 (Up-Sweep):** Computes partial sums in a bottom-up binary tree ($O(n)$ work, $O(\log n)$ span).
- **Phase 2 (Down-Sweep):** Distributes accumulated offsets down the tree to calculate all prefix sums ($O(n)$ work, $O(\log n)$ span).
- **OriC Location:** In bacterial chromosomes, leading strand DNA replication creates asymmetric mutation pressures leading to an excess of Guanine over Cytosine. The cumulative GC-skew $\sum (G - C)$ reaches a sharp local minimum precisely at the **Bacterial Origin of Replication (OriC)**. Parallel prefix scan calculates this in $O(\log n)$ span.

#### Q20: What does Brent's Theorem state?
**Answer:** Brent's Theorem states that any parallel algorithm with work $T_1$ and span $T_\infty$ running on $p$ processors has execution time:
$$T_p \le \frac{T_1}{p} + T_\infty$$
It shows that as long as $p \ll \frac{T_1}{T_\infty}$, the algorithm achieves linear speedup $S_p \approx p$.
