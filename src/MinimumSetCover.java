import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Stream;

public class MinimumSetCover {

    static HashSet<BitSet> c;
    static ArrayList<BitSet> s;
    static Boolean flag;
    static int universalSetSize, numberOfSubsets;

    static int uncoveredCount(BitSet bitMask, BitSet x) {
        x.or(bitMask);
        x.xor(bitMask);
        return x.cardinality();
    }

    static int subset(BitSet x1, BitSet x2) {
        BitSet x = (BitSet) x1.clone();
        x.or(x2);
        if(x.equals(x1)) return -1;
        if(x.equals(x2)) return 1;
        return 0;
    }

    static boolean subset(BitSet x) {
        for (BitSet bs : s) if (subset(x, bs) == 1) return true;
        return false;
    }

    static void removeRedundantSubsets() {
        s.removeIf(MinimumSetCover::subset);
    }

    static void greedy(int universalSetSize, BitSet bitMask, ArrayList<BitSet> s) {
        while(bitMask.cardinality() < universalSetSize) {
            s.sort(Comparator.comparingInt(x -> uncoveredCount(bitMask, (BitSet) x.clone())));
            BitSet bs = s.remove(s.size()-1);
            c.add(bs);
            bitMask.or(bs);
        }
    }

    static void findMinimumSetCover(int k,
                                    BitSet ors,
                                    ArrayList<BitSet> s,
                                    HashSet<BitSet> sPrime) {
        if(c.size() <= sPrime.size()) return;
        if(ors.cardinality() == universalSetSize) {
            c = new HashSet<>(sPrime);
            flag = Boolean.TRUE;
            return;
        }
        if(universalSetSize > sPrime.size() && c.size()-1 > sPrime.size()) {
            for(int i=k;i<s.size();++i) {
                if(universalSetSize <= sPrime.size() || c.size() <= sPrime.size()) return;
                if(!sPrime.contains(s.get(i))) {
                    BitSet orsCopy = (BitSet) ors.clone();
                    orsCopy.or(s.get(i));
                    if(orsCopy.equals(ors)) continue;
                    sPrime.add(s.get(i));
                    findMinimumSetCover(i+1, orsCopy, s, sPrime);
                    sPrime.remove(s.get(i));
                }
                if(flag) {
                    flag = Boolean.FALSE;
                    return;
                }
            }
        }
    }

    public static void main(String... args) {
        try {
            List<String> lines =
                    Files.readAllLines(Paths.get("/Users/starrxu/CSE373 HW4 Test Files/s-rg-118-30"));
            universalSetSize = Integer.parseInt(lines.get(0));
            numberOfSubsets = Integer.parseInt(lines.get(1));
            s = new ArrayList<>();
            for(int i=2;i<lines.size();++i) {
                if(!lines.get(i).isEmpty()) {
                    BitSet bs = new BitSet();
                    Stream.of(lines.get(i).split(" ")).mapToInt(Integer::parseInt).forEach(bs::set);
                    s.add(bs);
                }
                else --numberOfSubsets;
            }
            long startTime = System.nanoTime();
            removeRedundantSubsets();
            s.sort(Comparator.comparingInt(BitSet::cardinality).reversed());
            c = new HashSet<>();
            greedy(universalSetSize, new BitSet(), new ArrayList<>(s));
            //System.out.println(c);
            flag = Boolean.FALSE;
            findMinimumSetCover(0, new BitSet(), s, new HashSet<>());
            System.out.println(c);
            System.out.println(c.size());
            long endTime = System.nanoTime();
            System.out.println((endTime-startTime)/1000000 + " ms");
        }
        catch(IOException e) {
            e.printStackTrace();
        }
    }
}
