package appointmentservice;

import static org.junit.jupiter.api.Assertions.*;

import java.util.Date;
import java.util.Calendar;
import org.junit.jupiter.api.Test;

class AppointmentTest {

    // helper to make a future date
    private Date makeFutureDate() {
        Calendar c = Calendar.getInstance();
        c.add(Calendar.DAY_OF_MONTH, 1); //one day in the future
        return c.getTime();
    }

    // helper to make a past date
    private Date makePastDate() {
        Calendar c = Calendar.getInstance();
        c.add(Calendar.DAY_OF_MONTH, -1); //one day in the past
        return c.getTime();
    }

    @Test
    void testValidAppointment() {
        Date futureDate = makeFutureDate();
        Appointment appt = new Appointment("123", futureDate, "Valid description");
        assertEquals("123", appt.getAppointmentId());
        assertEquals("Valid description", appt.getDescription());
        assertEquals(futureDate, appt.getAppointmentDate());
    }

    @Test
    void testInvalidIdTooLong() {
        Date futureDate = makeFutureDate();
        assertThrows(IllegalArgumentException.class, () -> {
            new Appointment("12345678901", futureDate, "Valid description"); // 11 chars
        });
    }

    @Test
    void testInvalidIdNull() {
        Date futureDate = makeFutureDate();
        assertThrows(IllegalArgumentException.class, () -> {
            new Appointment(null, futureDate, "Valid description");
        });
    }

    @Test
    void testInvalidPastDate() {
        Date pastDate = makePastDate();
        assertThrows(IllegalArgumentException.class, () -> {
            new Appointment("123", pastDate, "Valid description");
        });
    }

    @Test
    void testInvalidNullDate() {
        assertThrows(IllegalArgumentException.class, () -> {
            new Appointment("123", null, "Valid description");
        });
    }

    @Test
    void testInvalidNullDescription() {
        Date futureDate = makeFutureDate();
        assertThrows(IllegalArgumentException.class, () -> {
            new Appointment("123", futureDate, null);
        });
    }

    @Test
    void testInvalidDescriptionTooLong() {
        Date futureDate = makeFutureDate();
        String longDesc = "This description is longer than fifty characters and should fail the test.";
        assertThrows(IllegalArgumentException.class, () -> {
            new Appointment("123", futureDate, longDesc);
        });
    }
}
