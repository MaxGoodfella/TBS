package com.max;

import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.atomic.AtomicInteger;

@Slf4j
public class TicketBookingSystem {

    private final AtomicInteger availableTickets;
    private final BlockingQueue<String> waitingQueue = new ArrayBlockingQueue<>(130);
    private final Object lock = new Object();
    private List<String> users = new ArrayList<>();

    private int successfulBookings = 0;
    private int failedBookings = 0;
    private int returnedTickets = 0;
    private long totalWaitTime = 0;
    private int waitingUsersCount = 0;

    public TicketBookingSystem(int ticketCount) {
        this.availableTickets = new AtomicInteger(ticketCount);
    }


    public boolean bookTicket(String user) {
        synchronized (lock) {
            int ticketsLeft = availableTickets.get();

            if (ticketsLeft > 0) {
                availableTickets.decrementAndGet();
                users.add(user);
                successfulBookings++;
                log.info("Пользователь {} успешно забронировал билет. Осталось билетов: {}", user, availableTickets.get());
                return true;
            } else {
                try {
                    waitingQueue.put(user);
                    waitingUsersCount++;
                    log.info("Билетов нет: пользователь {} добавлен в очередь ожидания.", user);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    log.error("Ошибка при добавлении пользователя {} в очередь ожидания: {}", user, e.getMessage());
                }
                return false;
            }
        }
    }

    public boolean returnTicket(String user) {
        synchronized (lock) {
            if (users.remove(user)) {
                availableTickets.incrementAndGet();
                returnedTickets++;
                log.info("Пользователь {} успешно вернул билет. Осталось билетов: {}", user, availableTickets.get());

                if (!waitingQueue.isEmpty()) {
                    String nextUser = waitingQueue.poll();
                    if (nextUser != null) {
                        bookTicket(nextUser);
                    }
                }

                return true;
            } else {
                log.info("Пользователь {} не смог вернуть билет.", user);
                return false;
            }
        }
    }

    public void printStatistics() {
        log.info("Статистика:");
        log.info("Успешные бронирования: {}", successfulBookings);
        log.info("Неудачные бронирования: {}", failedBookings);
        log.info("Возвращённые билеты: {}", returnedTickets);

        if (waitingUsersCount > 0) {
            log.info("Пользователей в очереди: {}", waitingUsersCount);
            log.info("Среднее время ожидания: {} мс", totalWaitTime / waitingUsersCount);
        } else {
            log.info("Нет пользователей в очереди.");
        }
    }

}
