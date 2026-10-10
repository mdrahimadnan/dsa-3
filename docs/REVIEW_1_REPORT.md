# BioGenome: Genome Research & Sequence Analysis Platform
## Review-1 Comprehensive Project Report (CO1 – CO3)
**Evaluation Marks: 60 Marks**

---

## 1. Problem Understanding & Requirement Mapping (CO1)

### 1.1 Problem Statement
Genomic sequences (DNA and RNA) consist of long strings over $\Sigma = \{A, T, G, C\}$. In computational genomics, biomedical research, viral tracking, and therapeutic engineering:
1. Exact regulatory DNA motifs (such as CRISPR PAM sites and promoter boxes) must be located rapidly.
2. Long sequence repeats (transposable elements) must be indexed and identified sub-linearly.
3. Evolutionary sequence homology between gene pairs must be aligned to detect conserved functional domains, insertions, deletions, and point mutations.

### 1.2 Selection of the 6 Core Algorithms
Rather than implementing redundant toy algorithms, BioGenome focuses on the **6 essential, gold-standard algorithms** used in real bioinformatics pipelines:

| Module | Core Algorithm | Biological Problem Solved | Course Outcome |
| :--- | :--- | :--- | :--- |
| **M1** | **KMP** | Linear single-motif discovery without text backtracking | **CO2** (String Matching) |
| **M1** | **Rabin-Karp** | Hash-based substring search & rolling fingerprinting | **CO2** (String Matching) |
| **M1** | **Aho-Corasick** | Simultaneous multi-motif search (CRISPR PAM targets) | **CO2** (String Matching) |
| **M2** | **Suffix Array + Kasai's LCP** | Full-text genome indexing & Longest Repeated Substring | **CO2** (Suffix Structures) |
| **M3** | **Needleman-Wunsch** | End-to-end Global Sequence Alignment with traceback | **CO3** (Advanced DP) |
| **M3** | **Smith-Waterman** | Local Conserved Homology Alignment with traceback | **CO3** (Advanced DP) |

---

## 2. Algorithm Selection & Technical Justification (CO1–CO3)

### 2.1 Single-Motif Search: KMP vs Rabin-Karp
- **KMP (Knuth-Morris-Pratt):** Precomputes the Longest Proper Prefix which is also a Suffix (**LPS**) table in $O(m)$ time. When a character mismatch occurs, it skips ahead using $LPS[j-1]$ without retreating the text index. It provides a guaranteed worst-case linear time of $O(n + m)$.
- **Rabin-Karp:** Uses a polynomial rolling hash function ($H_{new} = ((H_{old} - c_{out} \cdot b^{m-1}) \cdot b + c_{in}) \pmod q$) to transition windows in $O(1)$ time. This is optimal for sliding-window repeat identification and average-case $O(n + m)$ scanning.

### 2.2 Multi-Motif Search: Aho-Corasick
- When scanning viral sequences for multiple guide RNA or CRISPR PAM motifs ($k$ motifs of total length $L$), iterating single-pattern algorithms takes $O(k \cdot n)$ time.
- **Aho-Corasick** constructs a Trie with failure links using Breadth-First Search (BFS) in $O(L)$ preprocessing time, allowing the entire genome to be scanned in a single linear pass of $O(n + \text{matches})$, regardless of how many motifs are provided.

### 2.3 Genome Indexing: Suffix Array + Kasai's LCP
- Suffix Trees require large pointer overhead (20–45 bytes per character). A **Suffix Array** stores the sorted suffix starting indices in a compact array of 4-byte integers ($4n$ bytes).
- **Kasai's Algorithm** computes the Longest Common Prefix (LCP) array between consecutive sorted suffixes in strictly $O(n)$ linear time. The maximum entry in the LCP array immediately reveals the **Longest Repeated Substring (LRS)** in the genome (critical for finding retrotransposons and tandem repeats).

### 2.4 Sequence Alignment: Needleman-Wunsch vs Smith-Waterman
- **Needleman-Wunsch (Global Alignment):** Aligns two sequences from start to end. Fills the DP boundary with cumulative gap penalties ($dp[i][0] = i \cdot \text{gap}$), ensuring optimal full-length alignment.
- **Smith-Waterman (Local Alignment):** Uses a floor of zero ($\max(0, \dots)$), preventing score decay across divergent flanking regions. Traceback starts at the **peak scoring cell in the entire matrix**, isolating conserved biological motifs.

---

## 3. Complexity & Algorithmic Analysis (CO2–CO3)

| Algorithm | Preprocessing Time | Matching Time | Space Complexity |
| :--- | :--- | :--- | :--- |
| **KMP** | $O(m)$ (LPS Array) | $O(n)$ | $O(m)$ |
| **Rabin-Karp** | $O(m)$ (Hash Computation) | $O(n)$ avg, $O(n \times m)$ worst | $O(1)$ |
| **Aho-Corasick** | $O(L)$ (Trie + Failure Links) | $O(n + \text{matches})$ | $O(L \cdot \|\Sigma\|)$ |
| **Suffix Array + LCP** | $O(n \log n)$ SA, $O(n)$ LCP | $O(m \log n)$ | $O(n)$ |
| **Needleman-Wunsch** | None | $O(n \times m)$ | $O(n \times m)$ |
| **Smith-Waterman** | None | $O(n \times m)$ | $O(n \times m)$ |

---

## 4. Live Test Cases Demonstrated During Review-1

### Test Case 1: KMP DNA Motif Search
- **DNA:** `ATGCGATCGATCGATCGATCGATAGCTAGCTAGCTAATGCGATCGATC`
- **Motif:** `ATCGATC`
- **Output:** Matches at indices `[5, 9, 13, 41]`.
- **Time:** $O(n + m)$, LPS prevents index backtracking.

### Test Case 2: Suffix Array & Kasai LCP
- **Genome:** `GATAGACA$`
- **LCP Array:** `[0, 1, 0, 1, 0, 2, 0, 1, 0]`
- **Detected LRS:** `"GA"` (Length: 2, occurs at indices 0 and 4).

### Test Case 3: Needleman-Wunsch Global Alignment
- **Seq 1:** `GCATGCG`
- **Seq 2:** `GATTACA`
- **Traceback Alignment:**
  ```text
  Seq 1:  GCA-TGCG
  Match:  | | |.|.
  Seq 2:  G-ATTACA
  ```
- **Optimal Score:** `0`, Identity: `50.00%`.

### Test Case 4: Smith-Waterman Local Alignment
- **Seq 1:** `TGTTACGG`
- **Seq 2:** `GGTTGACTA`
- **Traceback Alignment:**
  ```text
  Subsequence 1: GTT-ACG
  Local Match:   ||| |.|
  Subsequence 2: GTTGACT
  ```
- **Peak Local Score:** `6.00`.
