package appointmentservice;

import java.util.HashMap;
import java.util.Date;
//Added for milestone three
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class AppointmentService {
    
    private int currentIDNum = 0;  //pseudo unique ID counter
    private final HashMap<String, Appointment> appointments = new HashMap<>();
    
    //add appointment with auto-gen ID
    public void addUniqueAppointment(Date date, String description) {
        String stringID = Integer.toString(currentIDNum);
        Appointment tempAppointment = new Appointment(stringID, date, description);
        
        if (appointments.containsKey(stringID)) {
            throw new IllegalArgumentException("Duplicate appointment ID");
        }
        
        appointments.put(stringID, tempAppointment);
        currentIDNum++;
    }
    
    //delete appointment by ID
    public void deleteAppointment(String id) {
        if (!appointments.containsKey(id)) {
            throw new IllegalArgumentException("Appointment ID not found");
        }
        appointments.remove(id);
    }
    //added for milestone three
    //return appointments in chronological order
    public List<Appointment> getAppointmentsChronologically() {
        List<Appointment> sortedAppointments =
                new ArrayList<>(appointments.values());

        sortedAppointments.sort(
                Comparator.comparing(Appointment::getAppointmentDate)
        );

        return sortedAppointments;
    }

    //added for milestone three
    //return appointments within a date range
    public List<Appointment> getAppointmentsByDateRange(Date startDate, Date endDate) {
        List<Appointment> results = new ArrayList<>();

        for (Appointment appointment : appointments.values()) {
            Date appointmentDate = appointment.getAppointmentDate();

            if (!appointmentDate.before(startDate) && !appointmentDate.after(endDate)) {
                results.add(appointment);
            }
        }

        results.sort(
                Comparator.comparing(Appointment::getAppointmentDate)
        );

        return results;
    }
}
