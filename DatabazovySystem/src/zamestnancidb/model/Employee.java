package zamestnancidb.model;

import java.util.ArrayList;
import java.util.List;

public abstract class Employee {
	
	private static int ID_COUNTER= 1;
	private final int ID;
	private String lastName;
	private String firstName;
	private int yearBirth;
	private final List<Collaboration> collaborations;
	
	public Employee(String firstName, String lastName, int yearBirth) {
		this.ID = ID_COUNTER++;
		this.lastName = lastName;
		this.yearBirth = yearBirth;
		this.collaborations = new ArrayList<>();
	}
	
	public Employee(int ID, String firstName, String lastName, int yearBirth) {
		if (ID>= ID_COUNTER) {
			ID_COUNTER = ID + 1;
		}
		this.ID = ID;
		this.firstName = firstName;
		this.lastName = lastName;
		this.yearBirth = yearBirth;
		this.collaborations = new ArrayList<>();
	}
	
	public abstract String getGroupName();
	public abstract String getStatistics();
	
	public int getID() { 
		return ID; 
	}
	public String getLastName() { 
		return lastName; 
	}
	public String getFirstName() { 
		return firstName; 
	}
	public int getYearBirth() { 
		return yearBirth; 
	}
	public List<Collaboration> getCollaborations() { 
		return collaborations; 
	}
	
	public void addCollaboration(Collaboration c) {
		collaborations.add(c);
	}
	
	public void removeCollaboration(int partnerID) {
		for (int i = 0; i < collaborations.size(); i++) {
		    if (collaborations.get(i).getPartnerID() == partnerID) {
		        collaborations.remove(i);
		        break;
		    }
		}
	}
	
	public double getAverageCollaborationQuality() {
		if (collaborations.isEmpty()) {
			return 0.0;
		}
		double sum = 0;
		for (Collaboration c : collaborations) {
			sum += c.getQuality().getValue();
		}
		return sum / collaborations.size();
	}
	public int getCollaborationCount() {
		return collaborations.size();
	}
	
	@Override
	public String toString() {
        return String.format("[%d] %s %s (nar. %d) | Skupina: %s | Vazby: %d",
                ID, lastName, firstName, yearBirth, getGroupName(), collaborations.size());
    }

    public static void resetIdCounter(int value) {
        ID_COUNTER = value;
    }
	
}
