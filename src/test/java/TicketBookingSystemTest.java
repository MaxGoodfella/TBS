import com.max.TicketBookingSystem;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class TicketBookingSystemTest {

    @Test
    public void testSuccessfulBooking() {
        TicketBookingSystem tbs = new TicketBookingSystem(5);

        boolean result = tbs.bookTicket("user1");

        assertTrue("Бронирование должно быть успешным", result);
        assertEquals( "После бронирования должно остаться 4 билета", 4, tbs.getAvailableTickets());
    }

    @Test
    public void testFailedBookingWhenNoTicketsAvailable_thenAddToWaitingQueue() {
        TicketBookingSystem tbs = new TicketBookingSystem(1);

        assertTrue("Бронирование должно быть успешным", tbs.bookTicket("user1"));

        boolean result = tbs.bookTicket("user2");

        assertFalse("Пользователь должен быть добавлен в очередь ожидания", result);
        assertEquals("Должно остаться 0 билетов", 0, tbs.getAvailableTickets());
    }

    @Test
    public void testSuccessfulReturn() {
        TicketBookingSystem tbs = new TicketBookingSystem(1);

        tbs.bookTicket("user1");

        assertEquals("Должно остаться 0 билетов", 0, tbs.getAvailableTickets());

        boolean result = tbs.returnTicket("user1");

        assertTrue("Билет должен быть успешно возвращен", result);
        assertEquals("Должен быть 1 доступный билет после возврата", 1, tbs.getAvailableTickets());
    }

    @Test
    public void testFailedReturnWhenNoUserFound() {
        TicketBookingSystem tbs = new TicketBookingSystem(1);

        tbs.bookTicket("user1");

        assertEquals("Должно остаться 0 билетов", 0, tbs.getAvailableTickets());

        boolean result = tbs.returnTicket("user2");

        assertFalse("Билет не должен быть успешно возвращен", result);
        assertEquals("Должно быть 0 доступный билетов после неудачного возврата",
                0, tbs.getAvailableTickets());
    }

    @Test
    public void testAutomaticNextSuccessfulBookingFromQueueAfterTicketReturned() {
        TicketBookingSystem tbs = new TicketBookingSystem(2);

        tbs.bookTicket("user1");
        tbs.bookTicket("user2");

        tbs.bookTicket("user3");

        assertEquals("Должно остаться 0 билетов", 0, tbs.getAvailableTickets());

        boolean result = tbs.returnTicket("user1");

        assertTrue("Билет должен быть успешно возвращен", result);
        assertEquals("Должно остаться 0 билетов после автоматического бронирования для следующего пользователя",
                0, tbs.getAvailableTickets());
    }

    @Test
    public void testMultipleUsersInQueue() {
        TicketBookingSystem tbs = new TicketBookingSystem(1);

        tbs.bookTicket("user1");
        tbs.bookTicket("user2");
        tbs.bookTicket("user3");

        assertEquals(0, tbs.getAvailableTickets());

        tbs.returnTicket("user1");

        assertEquals("Должно остаться 0 билетов после автоматического бронирования user2",
                0, tbs.getAvailableTickets());

        tbs.returnTicket("user2");

        assertEquals( "Должно остаться 0 билетов после автоматического бронирования user3",
                0, tbs.getAvailableTickets());
    }

    @Test
    public void testConcurrentBooking() throws InterruptedException {
        TicketBookingSystem bookingSystem = new TicketBookingSystem(3);
        int numberOfThreads = 10;
        List<Thread> threads = new ArrayList<>();

        for (int i = 1; i <= numberOfThreads; i++) {
            final String user = "user" + i;
            Thread thread = new Thread(() -> bookingSystem.bookTicket(user));
            threads.add(thread);
            thread.start();
        }

        for (Thread thread : threads) {
            thread.join();
        }

        assertEquals("Успешных бронирований не должно быть больше доступных билетов",
                3, bookingSystem.getSuccessfulBookings());
        assertEquals("Неудачных бронирований должно быть 7",
                7, bookingSystem.getFailedBookings());
    }

}