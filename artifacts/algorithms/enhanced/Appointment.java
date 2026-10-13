package appointmentservice;

import java.util.Date;

public class Appointment {
	private final String appointmentId; //cannot be updated, cannot be null, < 10 chars
	private final Date appointmentDate; // cannot be past date, cannot be null
	private final String description; //cannot be null, < 50 chars
	
	// constructor 
	public Appointment(String appointmentId, Date appointmentDate, String description) {
		if (appointmentId == null || appointmentId.length() > 10) {
			throw new IllegalArgumentException("Invalid appointment ID");
		}
		
		//validate date
		if (appointmentDate == null || appointmentDate.before(new Date())) {
			throw new IllegalArgumentException("Invalid appointment date is past");
		}
		
		//validate description
		if (description == null || description.length() > 50) {
			throw new IllegalArgumentException("Invalid Description");
		}
		
		this.appointmentId = appointmentId;
		this.appointmentDate = new Date(appointmentDate.getTime());
		this.description = description;
	}
	
	//getters
	public String getAppointmentId() {
		return appointmentId;
	}
	
	public Date getAppointmentDate() {
		return new Date(appointmentDate.getTime());
	}
	
	public String getDescription() {
		return description;
	}

}
