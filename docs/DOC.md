# BioGenome: Quick Reference Tables

---

## 1. Project Purpose (In Short)

| Purpose | Real-World Application |
| :--- | :--- |
| **Fast DNA Search** | Finds virus signatures & disease-causing genes in seconds instead of hours. |
| **Genome Indexing** | Indexes 3 billion base pairs in memory to find repeated DNA patterns. |
| **Sequence Alignment** | Compares human vs. animal genes to detect mutations and evolutionary links. |

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

## 5. One-Line Viva Cheat Sheet

| Question | Short Answer (Direct for Examiner) |
| :--- | :--- |
| **Why KMP over Naive?** | KMP uses LPS array to avoid text backtracking, guaranteeing strict $O(n+m)$ time. |
| **Why Aho-Corasick over KMP?** | KMP scans text $k$ times for $k$ patterns; Aho-Corasick scans text only once ($O(n)$). |
| **Why Suffix Array over Suffix Tree?** | Suffix Array takes $4n$ bytes (integers), saving over 80% RAM compared to Suffix Trees. |
| **Needleman-Wunsch vs Smith-Waterman?** | Needleman-Wunsch = Global alignment (end-to-end); Smith-Waterman = Local alignment (conserved domains). |
