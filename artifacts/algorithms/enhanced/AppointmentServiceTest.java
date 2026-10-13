package appointmentservice;

import static org.junit.jupiter.api.Assertions.*;

import java.util.Calendar;
import java.util.Date;
import org.junit.jupiter.api.Test;
//Added for Milestone Three
import java.util.List;

class AppointmentServiceTest {

    //helpers to make dates
    private Date makeFutureDate() {
        Calendar c = Calendar.getInstance();
        c.add(Calendar.DAY_OF_MONTH, 1);
        return c.getTime();
    }

    private Date makePastDate() {
        Calendar c = Calendar.getInstance();
        c.add(Calendar.DAY_OF_MONTH, -1);
        return c.getTime();
    }

    @Test
    void addAppointment_succeeds_withAutoId() {
        AppointmentService svc = new AppointmentService();
        Date future = makeFutureDate();

        //add first appointment, ID should be "0"
        assertDoesNotThrow(() -> svc.addUniqueAppointment(future, "Valid description"));

        //add second appointment, ID should be "1"
        assertDoesNotThrow(() -> svc.addUniqueAppointment(future, "Another valid description"));

        //delete both to prove they exist with the expected IDs
        assertDoesNotThrow(() -> svc.deleteAppointment("0"));
        assertDoesNotThrow(() -> svc.deleteAppointment("1"));
    }

    @Test
    void addAppointment_rejectsInvalidInputs_viaModelRules() {
        AppointmentService svc = new AppointmentService();

        //past date should fail 
        assertThrows(IllegalArgumentException.class,
            () -> svc.addUniqueAppointment(makePastDate(), "Valid description"));

        //null description should fail 
        assertThrows(IllegalArgumentException.class,
            () -> svc.addUniqueAppointment(makeFutureDate(), null));

        //overlong description should fail
        String longDesc = "This description is longer than fifty characters and should fail the test.";
        assertThrows(IllegalArgumentException.class,
            () -> svc.addUniqueAppointment(makeFutureDate(), longDesc));
    }

    @Test
    void deleteAppointment_throwsWhenIdNotFound() {
        AppointmentService svc = new AppointmentService();

        //nothing added yet, so any delete should throw
        assertThrows(IllegalArgumentException.class, () -> svc.deleteAppointment("0"));

        //add one, then delete it, then deleting again should throw
        svc.addUniqueAppointment(makeFutureDate(), "Valid description");
        assertDoesNotThrow(() -> svc.deleteAppointment("0"));
        assertThrows(IllegalArgumentException.class, () -> svc.deleteAppointment("0"));
    }

    //added for milestone three
    @Test
    void getAppointmentsChronologically_sortsByDate() {
        AppointmentService svc = new AppointmentService();

        Calendar c1 = Calendar.getInstance();
        c1.add(Calendar.DAY_OF_MONTH, 3);

        Calendar c2 = Calendar.getInstance();
        c2.add(Calendar.DAY_OF_MONTH, 1);

        Calendar c3 = Calendar.getInstance();
        c3.add(Calendar.DAY_OF_MONTH, 2);

        svc.addUniqueAppointment(c1.getTime(), "Third appointment");
        svc.addUniqueAppointment(c2.getTime(), "First appointment");
        svc.addUniqueAppointment(c3.getTime(), "Second appointment");

        List<Appointment> sorted = svc.getAppointmentsChronologically();

        assertEquals("First appointment", sorted.get(0).getDescription());
        assertEquals("Second appointment", sorted.get(1).getDescription());
        assertEquals("Third appointment", sorted.get(2).getDescription());
    }
    //added for milestone three
    @Test
    void getAppointmentsByDateRange_returnsMatchingAppointments() {
        AppointmentService svc = new AppointmentService();

        Calendar c1 = Calendar.getInstance();
        c1.add(Calendar.DAY_OF_MONTH, 1);

        Calendar c2 = Calendar.getInstance();
        c2.add(Calendar.DAY_OF_MONTH, 3);

        Calendar c3 = Calendar.getInstance();
        c3.add(Calendar.DAY_OF_MONTH, 5);

        svc.addUniqueAppointment(c1.getTime(), "First appointment");
        svc.addUniqueAppointment(c2.getTime(), "Middle appointment");
        svc.addUniqueAppointment(c3.getTime(), "Last appointment");

        Calendar start = Calendar.getInstance();
        start.add(Calendar.DAY_OF_MONTH, 2);

        Calendar end = Calendar.getInstance();
        end.add(Calendar.DAY_OF_MONTH, 4);

        List<Appointment> results =
                svc.getAppointmentsByDateRange(start.getTime(), end.getTime());

        assertEquals(1, results.size());
        assertEquals("Middle appointment", results.get(0).getDescription());
    }
}
