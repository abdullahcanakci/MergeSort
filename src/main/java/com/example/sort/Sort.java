package com.example.sort;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Abdullah Çanakçı 
 * 2018 - FALL
 */
public class Sort {
    public static void main(String[] args) {
        System.out.println("Sort <main> method running.");
        Sort sort = new Sort();

        Random rand = new Random(System.currentTimeMillis());

        ArrayList<Integer> list = new ArrayList<>(2500);
        for (int i = 0; i < 2500; i++) {
            list.add(rand.nextInt(10000));
        }
        long startTime = System.currentTimeMillis();
        sort.sortList(list);
        long endTime = System.currentTimeMillis();

        long deltaTime = endTime - startTime;
        boolean success = true;

        // Check the sorting of the list
        for (int i = 0; i < list.size() - 1; i++) {
            if (i != list.size())
                if (list.get(i) > list.get(i + 1)) {
                    success = false;
                }
        }
        log("Time took to sort 2500 random number in ms:" + deltaTime, false);
        if (success) {
            log("Sorting is succesfull", true);
        } else {
            log("Sorting is wrong", true);
        }
    }

    /**
     * Sort class that sorts provided Lists
     */
    public Sort() {
    }

    /**
     * This method is the access point for anybody requesting merge sort
     */
    public void sortList(List<Integer> list) {
        Thread thread = sortThread(list);
        thread.start();
        try {
            thread.join();
        } catch (InterruptedException ie) {
            log("Main sorting thread is interrupted", true);
            ie.printStackTrace();
        }
    }

    /**
    *   Handles seperating into thread and merging them later.
    *   @return Thread that will work until final merging completed. 
    *   Receiver should join this thread if it want to wait until full sorting.
    */
    private Thread sortThread(final List<Integer> list) {
        Thread thread = new Thread(new Runnable() {
            public void run() {
                int center = split(list);
                Thread thr1 = new SortRunner(list.subList(0, center));
                Thread thr2 = new SortRunner(list.subList(center, list.size()));
                try {
                    thr1.start();
                    thr2.start();
                    thr1.join();
                    thr2.join();
                    // thr1.start();
                    // thr2.start();
                    merge(list, center);
                } catch (InterruptedException ie) {
                    log("Thread is interrupted", true);
                    ie.printStackTrace();
                }
            }
        });
        return thread;
    }

    /**
     * Returns index number where a list should be divided in equal
     */
    private int split(List<Integer> list) {
        return Math.floorDiv(list.size(), 2);
    }

    /**
     * Sorts provided list
     */
    private void sort(List<Integer> list) {
        if (list.size() == 1) {
            return;
        }
        int centerIndex = split(list);
        sort(list.subList(0, centerIndex));
        sort(list.subList(centerIndex, list.size()));
        merge(list, centerIndex);
    }

    /**
     * Merges 2 sorted lists in place no lists are returned
     * 
     * @param list     list to be operated on
     * @param endIndex inclusive
     */
    private void merge(List<Integer> list, int centerIndex) {
        int leftIndexOfset = 0;
        int rightIndexOfset = centerIndex;
        // This list will be used for in place merging source for left list
        List<Integer> tempList = new ArrayList<>(list.subList(0, centerIndex));

        for (int i = 0; i < list.size(); i++) {
            /**
             * TRUE IF
             * There are items still items left on the left list.
             * The item from left list is smaller or equal to the item from the right list.
             * Until there are no more items on the right list
             */

            if (
                    leftIndexOfset < tempList.size() && 
                    (rightIndexOfset >= list.size() || 
                    tempList.get(leftIndexOfset) <= list.get(rightIndexOfset))) {
                list.set(i, tempList.get(leftIndexOfset));
                leftIndexOfset += 1;
            } else {
                list.set(i, list.get(rightIndexOfset));
                rightIndexOfset += 1;
            }
        }
    }

    /**
     * Utility method that print provided log messages to the console.
     * @param message Message to be printed to the console.
     * @param error Whether the message is from an error. Print an error output.
     */
    private static void log(String message, boolean error) {
        if (error) {
            System.err.println(message);
        } else {
            System.out.println(message);
        }
    }

    /**
     * Inner class that will handle threading sorting.
     */
    private class SortRunner extends Thread {
        private List<Integer> list;

        public SortRunner(List<Integer> list) {
            this.list = list;
        }

        public void run() {
            sort(list);
        }
    }
}
