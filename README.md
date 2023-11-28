# Set Cover Problem

Click [here](https://en.wikipedia.org/wiki/Set_cover_problem) to read about the set cover problem.

This is an implementation for solving the set cover problem that I wrote in Java. It uses several optimization techniques to pass benchmark tests. This was an assignment for my analysis of algorithms course.

When you start the program, you will be prompted to enter the pathname of a test file. Please enter the pathname of a correctly formatted test file.

Optimizations:
- Uses the greedy approximation algorithm to get a tighter upper bound on the minimum set cover size
- Removes redundant subsets (i.e., subsets of a subset) from S
- All subsets that have a unique element are included in the initial partial solution and excluded from S to reduce the number of searches
- Sorts S by size of subsets
- Uses Java parallel streams
- Takes advantage of this inequality: Greedy set cover size <= Minimum set cover size * ln(|U|), where |U| is the universal set size
- Searches through all combinations of size k in parallel from k in range [lower bound, greedy set cover size), constructs a list of the first set cover found corresponding to each k, and finds the minimum set cover from the list (if none are found, the greedy solution must be optimal)

Benchmark Results:

s-rg-118-30 48605 ms  
s-rg-63-25 1048 ms  
s-rg-40-20 52 ms  
s-rg-31-15 22 ms  

s-k-50-100 22482 ms  
s-k-40-80 2638 ms  
s-k-35-65 312 ms  
s-k-40-60 23 ms  
