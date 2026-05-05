package zamestnancidb.io;

import zamestnancidb.model.*;
import zamestnancidb.structure.EmployeeDatabase;

import java.io.*;
import java.util.*;

public class FileManager {

    private static final String EMPLOYEE_TAG = "EMPLOYEE";
    private static final String COLLAB_TAG = "COLLAB";

    public static void saveToFile(EmployeeDatabase db, String filename) throws IOException {
        try (PrintWriter pw = new PrintWriter(new FileWriter(filename))) {
            for (Employee e : db.getAllEmployees()) {
                String type = (e instanceof DataAnalyst) ? "DA" : "SS";
                pw.printf("%s;%d;%s;%s;%s;%d%n",
                        EMPLOYEE_TAG, e.getID(), type,
                        e.getFirstName(), e.getLastName(), e.getYearBirth());
            }
            Set<String> saved = new HashSet<>();
            for (Employee e : db.getAllEmployees()) {
                for (Collaboration c : e.getCollaborations()) {
                    int a = Math.min(e.getID(), c.getPartnerID());
                    int b = Math.max(e.getID(), c.getPartnerID());
                    String key = a + "-" + b;
                    if (!saved.contains(key)) {
                        pw.printf("%s;%d;%d;%d%n", COLLAB_TAG, a, b, c.getQuality().getValue());
                        saved.add(key);
                    }
                }
            }
        }
    }

    public static void loadFromFile(EmployeeDatabase db, String filename) throws IOException {
        List<String[]> collabLines = new ArrayList<>();

        try (BufferedReader br = new BufferedReader(new FileReader(filename))) {
            String line;
            while ((line = br.readLine()) != null) {
                line = line.trim();
                if (line.isEmpty()) continue;
                String[] parts = line.split(";");

                if (parts[0].equals(EMPLOYEE_TAG)) {
                    int id = Integer.parseInt(parts[1]);
                    String type = parts[2];
                    String firstName = parts[3];
                    String lastName = parts[4];
                    int birthYear = Integer.parseInt(parts[5]);

                    Employee e;
                    if ("DA".equals(type)) {
                        e = new DataAnalyst(id, firstName, lastName, birthYear);
                    } else {
                        e = new SecuritySpecialist(id, firstName, lastName, birthYear);
                    }
                    db.addEmployee(e);

                } else if (parts[0].equals(COLLAB_TAG)) {
                    collabLines.add(parts);
                }
            }
        }

        for (String[] parts : collabLines) {
            int id1 = Integer.parseInt(parts[1]);
            int id2 = Integer.parseInt(parts[2]);
            int qualVal = Integer.parseInt(parts[3]);
            CollaborationQuality quality = CollaborationQuality.fromValue(qualVal);
            db.addCollaboration(id1, id2, quality);
        }
    }
}
