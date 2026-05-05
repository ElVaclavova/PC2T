package zamestnancidb.model;

import java.io.Serializable;
import java.util.List;

public class DataAnalyst extends Employee implements Serializable {

	public DataAnalyst(String firstName, String lastName, int yearBirth) {
	    super(firstName, lastName, yearBirth);
	}

	public DataAnalyst(int ID, String firstName, String lastName, int yearBirth) {
	    super(ID, firstName, lastName, yearBirth);
	}

    @Override
    public String getGroupName() {
        return "Datovy analytik";
    }

    public int getCommonCollaboratorsCount(Employee partner, List<Employee> allEmployees) {
        int common = 0;
        for (Collaboration c : getCollaborations()) {
            for (Collaboration pc : partner.getCollaborations()) {
                if (c.getPartnerID() == pc.getPartnerID()) {
                    common++;
                }
            }
        }
        return common;
    }

    @Override
    public String getStatistics() {
        return String.format("Datovy analytik | %s %s | Vazby: %d | Priem. kvalita: %.2f",
                getLastName(), getFirstName(),
                getCollaborationCount(),
                getAverageCollaborationQuality());
    }
}
