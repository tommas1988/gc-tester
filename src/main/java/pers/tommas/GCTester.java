package pers.tommas;

import java.lang.management.ManagementFactory;
import java.lang.management.MemoryMXBean;
import java.util.ArrayList;
import java.util.List;

public class GCTester {
    int TOTAL_HOLDER_COUNT = 1000;

    private final MemoryMXBean memoryBean;

    static class Holder {
        private char[] chars;

        Holder(int holderSize) {
            chars = new char[holderSize];
        }
    }

    private List<Holder> holders;

    public GCTester() {
        memoryBean = ManagementFactory.getMemoryMXBean();
        long heapSize = memoryBean.getHeapMemoryUsage().getMax();

        int holderSize = (int) (heapSize / TOTAL_HOLDER_COUNT);

        printMemoryUsage("create holders without hold their references");
        for (int i = 0; i < TOTAL_HOLDER_COUNT; i++) {
            new Holder(holderSize);
        }
        printMemoryUsage(null);

        holders = new ArrayList<Holder>(TOTAL_HOLDER_COUNT);

        for (int i = 0,
             length = (int) (TOTAL_HOLDER_COUNT * 0.7),
             _20 = (int) (TOTAL_HOLDER_COUNT * 0.2),
             _50 = (int) (TOTAL_HOLDER_COUNT * 0.5);
             i < length; i++) {
            holders.add(new Holder(holderSize));
            if (i == _20) {
                printMemoryUsage("hold 20% holders");
            } else if (i == _50) {
                printMemoryUsage("hold 50% holders");
            }
        }

        printMemoryUsage("hold 80% holders");
    }

    private void printMemoryUsage(String msg) {
        if (msg != null) {
            System.out.println(msg + "\n");
        }

        System.out.println(memoryBean.getHeapMemoryUsage());
        System.out.println(memoryBean.getNonHeapMemoryUsage());
        System.out.println("------------------------------------------------------------------\n\n");
    }

    public static void main(String[] args) {
        new GCTester();
    }
}
