package com.max;

import java.util.Random;

public class User implements Runnable {

    private final TicketBookingSystem tbs;
    private final String userName;
    private final Random random = new Random();

    public User(TicketBookingSystem tbs, String userName) {
        this.tbs = tbs;
        this.userName = userName;
    }

    @Override
    public void run() {
        if (tbs.bookTicket(userName)) {
            try {
                Thread.sleep(random.nextInt(5000));
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
            if (random.nextBoolean()) {
                tbs.returnTicket(userName);
            }
        }
    }

}