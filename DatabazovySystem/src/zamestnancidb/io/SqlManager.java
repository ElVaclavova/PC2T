package zamestnancidb.io;

import java.sql.*;
import zamestnancidb.model.*;
import zamestnancidb.structure.*;

public class SqlManager {
    private static final String DB_URL = "jdbc:sqlite:employees_backup.db";

    public static void inicializuj() {
    	try {
            Class.forName("org.sqlite.JDBC");
        } catch (ClassNotFoundException e) {
            System.err.println("Kritická chyba: Knihovna sqlite-jdbc nebyla nalezena!");
            return;
        }

        try (Connection conn = DriverManager.getConnection(DB_URL);
             Statement stmt = conn.createStatement()) {
            
            stmt.execute("CREATE TABLE IF NOT EXISTS employees (" +
                         "id INTEGER PRIMARY KEY, " +
                         "first_name TEXT, " +
                         "last_name TEXT, " +
                         "birth_year INTEGER, " +
                         "group_name TEXT)");

            stmt.execute("CREATE TABLE IF NOT EXISTS collaborations (" +
                         "emp_id INTEGER, " +
                         "partner_id INTEGER, " +
                         "quality INTEGER, " +
                         "FOREIGN KEY(emp_id) REFERENCES employees(id))");
        } catch (SQLException e) {
            System.err.println("Chyba SQL inicializace: " + e.getMessage());
        }
    }

    public static void ulozVse(EmployeeDatabase db) {
        try (Connection conn = DriverManager.getConnection(DB_URL)) {
            conn.setAutoCommit(false);
            Statement st = conn.createStatement();
            st.execute("DELETE FROM collaborations");
            st.execute("DELETE FROM employees");

            String sqlE = "INSERT INTO employees VALUES (?,?,?,?,?)";
            try (PreparedStatement ps = conn.prepareStatement(sqlE)) {
                for (Employee e : db.getAllEmployees()) {
                    ps.setInt(1, e.getID());
                    ps.setString(2, e.getFirstName());
                    ps.setString(3, e.getLastName());
                    ps.setInt(4, e.getYearBirth());
                    ps.setString(5, e.getGroupName());
                    ps.addBatch();
                }
                ps.executeBatch();
            }

            String sqlC = "INSERT INTO collaborations VALUES (?,?,?)";
            try (PreparedStatement ps = conn.prepareStatement(sqlC)) {
                for (Employee e : db.getAllEmployees()) {
                    for (Collaboration c : e.getCollaborations()) {
                        ps.setInt(1, e.getID());
                        ps.setInt(2, c.getPartnerID());
                        ps.setInt(3, c.getQuality().getValue());
                        ps.addBatch();
                    }
                }
                ps.executeBatch();
            }
            conn.commit();
        } catch (SQLException e) {
            System.err.println("Chyba při ukládání do SQL: " + e.getMessage());
        }
    }

    public static void nactiVse(EmployeeDatabase db) {
        try (Connection conn = DriverManager.getConnection(DB_URL);
             Statement st = conn.createStatement()) {

            ResultSet rsE = st.executeQuery("SELECT * FROM employees");
            while (rsE.next()) {
                int id = rsE.getInt("id");
                String fn = rsE.getString("first_name");
                String ln = rsE.getString("last_name");
                int year = rsE.getInt("birth_year");
                String group = rsE.getString("group_name");

                Employee e;
                if ("Datovy analytik".equals(group)) {
                    e = new DataAnalyst(id, fn, ln, year);
                } else {
                    e = new SecuritySpecialist(id, fn, ln, year);
                }
                db.addEmployee(e);
            }

            ResultSet rsC = st.executeQuery("SELECT * FROM collaborations");
            while (rsC.next()) {
                db.addCollaboration(rsC.getInt("emp_id"), 
                                   rsC.getInt("partner_id"), 
                                   CollaborationQuality.fromValue(rsC.getInt("quality")));
            }
        } catch (SQLException e) {
            System.out.println("SQL záloha nenalezena, start s prázdnou DB.");
        }
    }
}