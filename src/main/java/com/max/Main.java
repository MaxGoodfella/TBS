package com.max;

import java.util.ArrayList;
import java.util.List;

public class Main {
    public static void main(String[] args) {

        int totalTickets = 100;
        TicketBookingSystem tbs = new TicketBookingSystem(totalTickets);
        List<Thread> userThreads = new ArrayList<>();

        for (int i = 1; i <= 130; i++) {
            String userName = "User" + i;
            Thread userThread = new Thread(new User(tbs, userName));
            userThread.start();
            userThreads.add(userThread);
        }

        for (Thread userThread : userThreads) {
            try {
                userThread.join();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }

        tbs.printStatistics();

    }
}