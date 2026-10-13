package appointmentservice;

import java.util.HashMap;
import java.util.Date;

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
}
