package appointmentservice;

import static org.junit.jupiter.api.Assertions.*;

import java.util.Calendar;
import java.util.Date;
import org.junit.jupiter.api.Test;

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
}
