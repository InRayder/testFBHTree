package com.Ray.JMTree;

import java.util.LinkedList;

/**
 * Range
 */
public class Range {

    private LinkedList<Integer> range = new LinkedList<Integer>();
    // private int[] range;
    private int startI;
    private int endI;
    private int size;

    public Range(int start, int end) {
        this.startI = start;
        this.endI = end;
        this.size = end - start;
        for (int i = start; i < end; i++) {
            this.range.add(i);
        }
    }

    /**
     * 窺視頭位元
     * 
     * @return
     */
    public int start() {
        if (range.isEmpty()) {
            return endI;
        }
        return range.getFirst();
    }

    /**
     * 窺視尾位元
     */
    public int end() {
        return range.getLast();
    }

    /**
     * 丟出頭位元
     * 
     * @return
     */
    public int next() {
        if (range.isEmpty()) {
            return endI;
        }
        int tmp = start();
        range.removeFirst();
        return tmp;
    }

    /**
     * 丟出尾位元
     * 
     * @return
     */
    public int next_back() {
        int tmp = end();
        range.removeLast();
        return tmp;

    }

    public static void main(String[] args) {
        Range r = new Range(0, 10);
        System.out.println(r.start()); // 0
        System.out.println(r.end()); // 9
        System.out.println(r.next()); // 0
        System.out.println(r.next()); // 1
        System.out.println(r.next_back()); //9
        System.out.println(r.end()); //8

    }
}