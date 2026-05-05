package zamestnancidb.model;

public class SecuritySpecialist extends Employee{
	
	public SecuritySpecialist(String lastName, String firstName, int yearBirth) {
		super(lastName, firstName, yearBirth);
	}
	
	public SecuritySpecialist(int ID, String firstName, String lastName, int yearBirth) {
		super(ID, firstName, lastName, yearBirth);
	}

	@Override
	public String getGroupName() {
		return "Bezpecnostny specialista";	
		}
	
	public double riskScore() {
		int count = getCollaborationCount();
		if (count == 0) {
			return 0.0;
		} 
		else {
		double avrgQuality = getAverageCollaborationQuality();
		return count * (4.0 - avrgQuality);
		}
	}
	
	@Override
	public String getStatistics() {
		return String.format("Bezpecnostny specialista | %s %s | Vazby: %d | Priem. kvalita: %.2f | Riziko: %.2f",
				getLastName(), getFirstName(),
				getCollaborationCount(),
				getAverageCollaborationQuality(),
				riskScore());
	}

}
