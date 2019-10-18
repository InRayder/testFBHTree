package com.Ray.JMTree;

import java.util.LinkedList;

/**
 * Ranage
 */
public class Ranage {

    private LinkedList<Integer> ranage = new LinkedList<Integer>();
    // private int[] ranage;
    private int startI;
    private int endI;
    private int size;

    public Ranage(int start, int end) {
        this.startI = start;
        this.endI = end;
        this.size = end - start;
        for (int i = start; i < end; i++) {
            this.ranage.add(i);
        }
    }

    /**
     * 窺視頭位元
     * 
     * @return
     */
    public int start() {
        if (ranage.isEmpty()) {
            return endI;
        }
        return ranage.getFirst();
    }

    /**
     * 窺視尾位元
     */
    public int end() {
        return ranage.getLast();
    }

    /**
     * 丟出頭位元
     * 
     * @return
     */
    public int next() {
        if (ranage.isEmpty()) {
            return endI;
        }
        int tmp = start();
        ranage.removeFirst();
        return tmp;
    }

    /**
     * 丟出尾位元
     * 
     * @return
     */
    public int next_back() {
        int tmp = end();
        ranage.removeLast();
        return tmp;

    }

    public static void main(String[] args) {
        Ranage r = new Ranage(0, 10);
        System.out.println(r.start());
        System.out.println(r.end());
        System.out.println(r.next());
        System.out.println(r.next());
        System.out.println(r.next_back());
        System.out.println(r.end());

    }
}