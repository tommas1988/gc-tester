package pers.tommas;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.lang.management.GarbageCollectorMXBean;
import java.lang.management.ManagementFactory;
import java.lang.management.MemoryMXBean;
import java.lang.management.MemoryUsage;
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

        String msg = "hold 80% holders";
        holders = new ArrayList<Holder>(TOTAL_HOLDER_COUNT);
        for (int i = 0,
             length = (int) (TOTAL_HOLDER_COUNT * 0.7),
             _20 = (int) (TOTAL_HOLDER_COUNT * 0.2),
             _50 = (int) (TOTAL_HOLDER_COUNT * 0.5);
             i < length; i++) {

            try {
                holders.add(new Holder(holderSize));
            } catch (OutOfMemoryError e) {
                msg = "OOM error occurs";
                break;
            }

            if (i == _20) {
                printMemoryUsage("hold 20% holders");
            } else if (i == _50) {
                printMemoryUsage("hold 50% holders");
            }
        }

        printMemoryUsage(msg);
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
        List<GarbageCollectorMXBean> beans = ManagementFactory.getGarbageCollectorMXBeans();
        for (GarbageCollectorMXBean bean : beans) {
            System.out.println(bean.getName());
        }

        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        String s;
        try {
            while ((s = br.readLine()) != null) {
                if ("s".equals(s) || "start".equals(s)) {
                    new GCTester();
                } else if ("q".equals(s) || "quit".equals(s))
                    break;
            }
        } catch (Exception e) {
            //
        }
    }
}