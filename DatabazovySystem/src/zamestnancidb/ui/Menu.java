package zamestnancidb.ui;

import zamestnancidb.io.FileManager;
import zamestnancidb.model.*;
import zamestnancidb.structure.*;

import java.util.*;

public class Menu {

    private final Scanner sc = new Scanner(System.in);
    private final EmployeeDatabase db = new EmployeeDatabase();

    private static final String DEFAULT_FILE = "employees.txt";

    public void run() {
        System.out.println("=== Databázový systém zamestnancov ===");

        boolean running = true;
        while (running) {
            printMenu();
            String choice = sc.nextLine().trim();
            System.out.println();

            switch (choice) {
                case "1": addEmployee(); break;
                case "2": addCollaboration(); break;
                case "3": removeEmployee(); break;
                case "4": findEmployee(); break;
                case "5": showGroupSkills(); break;
                case "6": printAlphabetical(); break;
                case "7": printStats(); break;
                case "8": printGroupCounts(); break;
                case "9": saveToFile(); break;
                case "10": loadFromFile(); break;
                case "0": running = false; break;
                default: System.out.println("Neplatná volba.\n");
            }
        }
        System.out.println("Program ukončen.");
    }

    private void printMenu() {
        System.out.println("--- MENU ---");
        System.out.println(" 1) Pridať zamestnanca");
        System.out.println(" 2) Pridať spoluprácu");
        System.out.println(" 3) Odobrať zamestnanca");
        System.out.println(" 4) Nájsť zamestnanca podľa ID");
        System.out.println(" 5) Spustiť dovednosti skupiny zamestnanca");
        System.out.println(" 6) Abecedný výpis zamestnancov");
        System.out.println(" 7) Štatistiky");
        System.out.println(" 8) Počet zamestnancov v skupinách");
        System.out.println(" 9) Uložiť do súboru");
        System.out.println("10) Načítať zo súboru");
        System.out.println(" 0) Ukončiť");
        System.out.print("Voľba: ");
    }

    private void addEmployee() {
        System.out.println("Skupiny:");
        List<String> groupNames = db.getGroupNames();
        for (int i = 0; i < groupNames.size(); i++) {
            System.out.printf("  %d) %s%n", i + 1, groupNames.get(i));
        }
        System.out.print("Vyberte skupinu (číslo): ");
        int groupIdx = readInt() - 1;
        if (groupIdx < 0 || groupIdx >= groupNames.size()) {
            System.out.println("Neplatná skupina.\n");
            return;
        }
        String groupName = groupNames.get(groupIdx);

        System.out.print("Meno: ");
        String firstName = sc.nextLine().trim();
        System.out.print("Priezvisko: ");
        String lastName = sc.nextLine().trim();
        System.out.print("Rok narodenia: ");
        int birthYear = readInt();

        Employee e;
        if ("Datový analytik".equals(groupName)) {
            e = new DataAnalyst(firstName, lastName, birthYear);
        } else {
            e = new SecuritySpecialist(firstName, lastName, birthYear);
        }

        db.addEmployee(e);
        System.out.printf("Zamestnanec pridaný s ID=%d.%n%n", e.getID());
    }

    private void addCollaboration() {
        System.out.print("ID zamestnanca: ");
        int id1 = readInt();
        System.out.print("ID kolegu: ");
        int id2 = readInt();

        System.out.println("Úroveň spolupráce:");
        System.out.println("  1) Špatná");
        System.out.println("  2) Průměrná");
        System.out.println("  3) Dobrá");
        System.out.print("Voľba: ");
        int qVal = readInt();
        if (qVal < 1 || qVal > 3) {
            System.out.println("Neplatná hodnota.\n");
            return;
        }

        CollaborationQuality quality = CollaborationQuality.fromValue(qVal);
        boolean ok = db.addCollaboration(id1, id2, quality);
        if (ok) {
            System.out.println("Spolupráca pridaná.\n");
        } else {
            System.out.println("Nepodarilo sa pridať spoluprácu.\n");
        }
    }

    private void removeEmployee() {
        System.out.print("ID zamestnanca na odobranie: ");
        int id = readInt();
        boolean ok = db.removeEmployee(id);
        System.out.println(ok ? "Zamestnanec odobratý.\n" : "Zamestnanec nenájdený.\n");
    }

    private void findEmployee() {
        System.out.print("ID zamestnanca: ");
        int id = readInt();
        Employee e = db.findById(id);
        if (e == null) {
            System.out.println("Zamestnanec nenájdený.\n");
            return;
        }
        System.out.println(e);
        System.out.println(e.getStatistics());
        System.out.println("Spolupráce:");
        if (e.getCollaborations().isEmpty()) {
            System.out.println("  (žiadne)");
        } else {
            for (Collaboration c : e.getCollaborations()) {
                Employee partner = db.findById(c.getPartnerID());
                String partnerName = (partner != null) ? partner.getLastName() + " " + partner.getFirstName() : "?";
                System.out.printf("  -> [%d] %s | %s%n", c.getPartnerID(), partnerName, c.getQuality());
            }
        }
        System.out.println();
    }

    private void showGroupSkills() {
        System.out.print("ID zamestnanca: ");
        int id = readInt();
        Employee e = db.findById(id);
        if (e == null) {
            System.out.println("Zamestnanec nenájdený.\n");
            return;
        }

        List<Employee> all = db.getAllEmployees();

        if (e instanceof DataAnalyst) {
            DataAnalyst da = (DataAnalyst) e;
            System.out.println("=== Datový analytik: Spoločné spolupráce ===");
            for (Collaboration c : da.getCollaborations()) {
                Employee partner = db.findById(c.getPartnerID());
                if (partner == null) continue;
                int common = da.getCommonCollaboratorsCount(partner, all);
                System.out.printf("  [%d] %s %s – spoločné spolupráce: %d%n",
                        partner.getID(), partner.getLastName(), partner.getFirstName(), common);
            }

        } else if (e instanceof SecuritySpecialist) {
            SecuritySpecialist ss = (SecuritySpecialist) e;
            System.out.println("=== Bezpečnostní specialista: Rizikové skóre ===");
            System.out.printf("  Počet väzieb: %d%n", ss.getCollaborationCount());
            System.out.printf("  Priemerná kvalita: %.2f%n", ss.getAverageCollaborationQuality());
            System.out.printf("  Rizikové skóre: %.2f%n", ss.riskScore());
        }
        System.out.println();
    }

    private void printAlphabetical() {
        System.out.println("=== Abecedný výpis zamestnancov ===");
        Map<String, List<Employee>> byGroup = db.getAlphabeticalByGroup();
        for (Map.Entry<String, List<Employee>> entry : byGroup.entrySet()) {
            System.out.println("Skupina: " + entry.getKey());
            if (entry.getValue().isEmpty()) {
                System.out.println("  (prázdna)");
            } else {
                for (Employee emp : entry.getValue()) {
                    System.out.println("  " + emp);
                }
            }
        }
        System.out.println();
    }

    private void printStats() {
        System.out.println("=== Štatistiky ===");
        Employee most = db.getMostConnected();
        if (most != null) {
            System.out.println("Zamestnanec s najviac väzbami: " + most);
            System.out.println("Prevažujúca kvalita spolupráce: " + db.getDominantQuality());
        } else {
            System.out.println("Databáza je prázdna.");
        }
        System.out.println();
    }

    private void printGroupCounts() {
        System.out.println("=== Počty zamestnancov v skupinách ===");
        for (Map.Entry<String, Integer> entry : db.getGroupCounts().entrySet()) {
            System.out.printf("  %-30s: %d%n", entry.getKey(), entry.getValue());
        }
        System.out.println();
    }

    private void saveToFile() {
        System.out.print("Názov súboru [" + DEFAULT_FILE + "]: ");
        String name = sc.nextLine().trim();
        if (name.isEmpty()) name = DEFAULT_FILE;
        try {
            FileManager.saveToFile(db, name);
            System.out.println("Uložené do: " + name + "\n");
        } catch (Exception ex) {
            System.out.println("Chyba pri ukladaní: " + ex.getMessage() + "\n");
        }
    }

    private void loadFromFile() {
        System.out.print("Názov súboru [" + DEFAULT_FILE + "]: ");
        String name = sc.nextLine().trim();
        if (name.isEmpty()) name = DEFAULT_FILE;
        try {
            FileManager.loadFromFile(db, name);
            System.out.println("Načítané z: " + name + "\n");
        } catch (Exception ex) {
            System.out.println("Chyba pri načítaní: " + ex.getMessage() + "\n");
        }
    }

    private int readInt() {
        while (true) {
            String line = sc.nextLine().trim();
            try {
                return Integer.parseInt(line);
            } catch (NumberFormatException e) {
                System.out.print("Zadajte číslo: ");
            }
        }
    }
}