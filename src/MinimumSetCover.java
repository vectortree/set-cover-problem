import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public class MinimumSetCover {

    static Deque<BitSet> c;
    static Deque<BitSet> sPrime;
    static List<BitSet> s;
    static boolean[] added;
    static boolean flag;
    static int universalSetSize, numberOfSubsets, bound;
    static BitSet bitMask;

    static int uncoveredCount(BitSet bitMask, BitSet x) {
        BitSet bs = new BitSet();
        bs.or(x);
        bs.or(bitMask);
        bs.xor(bitMask);
        return bs.cardinality();
    }

    static int subset(BitSet x1, BitSet x2) {
        BitSet x = new BitSet();
        x.or(x1);
        x.or(x2);
        if(x.equals(x1)) return -1;
        if(x.equals(x2)) return 1;
        return 0;
    }

    static boolean subset(BitSet x) {
        return s.stream().anyMatch(bs -> subset(x, bs) == 1);
    }

    static void removeRedundantSubsets() {
        s.removeIf(MinimumSetCover::subset);
    }

    static BitSet getBitMask() {
        BitSet bs = new BitSet();
        c.forEach(bs::or);
        return bs;
    }

    static int countOnes(int i) {
        return (int) s.stream().filter(bitSet -> bitSet.get(i)).count();
    }

    static BitSet uniqueElements() {
        BitSet bs = new BitSet();
        IntStream.range(1, universalSetSize+1)
                .filter(i -> countOnes(i)==1).forEachOrdered(bs::set);
        return bs;
    }

    static void addUniqueElementSets() {
        BitSet bs = uniqueElements();
        s.forEach(bitSet -> {
            BitSet x = new BitSet();
            x.or(bitSet);
            x.and(bs);
            if(x.cardinality()!=0)
                c.push(bitSet);
        });
        c.forEach(bitSet -> s.remove(bitSet));
    }

    static void greedy(int universalSetSize, BitSet bitSet, ArrayList<BitSet> s) {
        BitSet bitMask = new BitSet();
        bitMask.or(bitSet);
        PriorityQueue<BitSet> pq = new PriorityQueue<>((x1, x2) ->
                uncoveredCount(bitMask, x2) - uncoveredCount(bitMask, x1));
        pq.addAll(s);
        while(bitMask.cardinality() < universalSetSize && !pq.isEmpty()) {
            BitSet bs = pq.poll();
            c.add(bs);
            bitMask.or(bs);
        }
    }

    static void findMinimumSetCover(int k, BitSet ors) {
        int c_size = c.size();
        int sPrime_size = sPrime.size();
        int s_size = s.size();
        if(c_size <= sPrime_size) return;
        if(sPrime_size >= bound && ors.cardinality() == universalSetSize) {
            c = new ArrayDeque<>(sPrime);
            flag = true;
            return;
        }
        if(universalSetSize > sPrime_size && c_size-1 > sPrime_size) {
            for(int i=k;i<s_size;++i) {
                if(universalSetSize <= sPrime_size) return;
                if(!added[i]) {
                    BitSet orsCopy = new BitSet();
                    orsCopy.or(ors);
                    orsCopy.or(s.get(i));
                    if(orsCopy.equals(ors)) continue;
                    sPrime.push(s.get(i));
                    added[i] = true;
                    findMinimumSetCover(i+1, orsCopy);
                    sPrime.pop();
                    added[i] = false;
                }
                if(flag) {
                    flag = false;
                    return;
                }
            }
        }
    }

    public static void main(String... args) {
        System.out.println("Enter pathname of file: ");
        Scanner scanner = new Scanner(System.in);
        String pathName = scanner.nextLine();
        scanner.close();
        try {
            List<String> lines =
                    Files.readAllLines(Paths.get(pathName));
            universalSetSize = Integer.parseInt(lines.get(0));
            numberOfSubsets = Integer.parseInt(lines.get(1));
            s = new ArrayList<>();
            IntStream.range(2,lines.size()).forEach(i -> {
                if(!lines.get(i).isEmpty()) {
                    BitSet bs = new BitSet();
                    Stream.of(lines.get(i).split(" "))
                            .mapToInt(Integer::parseInt).forEachOrdered(bs::set);
                    s.add(bs);
                }
                else --numberOfSubsets;
            });
            long startTime = System.nanoTime();
            removeRedundantSubsets();
            s.sort(Comparator.comparingInt(BitSet::cardinality).reversed());
            c = new ArrayDeque<>();
            addUniqueElementSets();
            bitMask = getBitMask();
            sPrime = new ArrayDeque<>(c);
            greedy(universalSetSize, bitMask, new ArrayList<>(s));
            //System.out.println(c.size() + "\n" + c);
            bound = (int) (c.size()/Math.log(universalSetSize));
            added = new boolean[s.size()];
            findMinimumSetCover(0, bitMask);
            System.out.println(c.size() + "\n" + c);
            long endTime = System.nanoTime();
            System.out.println((endTime-startTime)/1000000 + " ms");
        }
        catch(IOException e) {
            e.printStackTrace();
        }
    }
}