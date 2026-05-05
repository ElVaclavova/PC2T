package zamestnancidb.structure;

import zamestnancidb.model.*;
import java.util.*;

public class EmployeeDatabase {

    private final Map<String, EmployeeGroup> groups;

    public EmployeeDatabase() {
        groups = new LinkedHashMap<>();
        groups.put("Datovy analytik", new EmployeeGroup("Datovy analytik"));
        groups.put("Bezpecnostny specialista", new EmployeeGroup("Bezpecnostny specialista"));
    }

    public Collection<EmployeeGroup> getGroups() {
        return groups.values();
    }

    public List<String> getGroupNames() {
        return new ArrayList<>(groups.keySet());
    }

    public void addEmployee(Employee e) {
        String groupName = e.getGroupName();
        EmployeeGroup group = groups.get(groupName);
        if (group == null) {
            throw new IllegalArgumentException("Skupina neexistuje: " + groupName);
        }
        group.addEmployee(e);
    }

    public boolean removeEmployee(int id) {
        Employee e = findById(id);
        if (e == null) return false;

        for (Employee other : getAllEmployees()) {
            other.removeCollaboration(id);
        }

        EmployeeGroup group = groups.get(e.getGroupName());
        return group != null && group.removeEmployee(e);
    }

    public Employee findById(int id) {
        for (EmployeeGroup group : groups.values()) {
            Employee e = group.findById(id);
            if (e != null) return e;
        }
        return null;
    }

    public List<Employee> getAllEmployees() {
        List<Employee> all = new ArrayList<>();
        for (EmployeeGroup group : groups.values()) {
            all.addAll(group.getAllEmployees());
        }
        return all;
    }

    public boolean addCollaboration(int id1, int id2, CollaborationQuality quality) {
        Employee e1 = findById(id1);
        Employee e2 = findById(id2);
        if (e1 == null || e2 == null) return false;
        if (id1 == id2) return false;

        for (Collaboration c : e1.getCollaborations()) {
            if (c.getPartnerID() == id2) return false;
        }

        e1.addCollaboration(new Collaboration(id2, quality));
        e2.addCollaboration(new Collaboration(id1, quality));
        return true;
    }

    public Employee getMostConnected() {
        List<Employee> all = getAllEmployees();
        if (all.isEmpty()) return null;
        Employee best = all.get(0);
        for (Employee e : all) {
            if (e.getCollaborationCount() > best.getCollaborationCount()) {
                best = e;
            }
        }
        return best;
    }

    public CollaborationQuality getDominantQuality() {
        int[] counts = new int[4];
        for (Employee e : getAllEmployees()) {
            for (Collaboration c : e.getCollaborations()) {
                counts[c.getQuality().getValue()]++;
            }
        }
        int maxVal = 0, maxIdx = 1;
        for (int i = 1; i <= 3; i++) {
            if (counts[i] > maxVal) {
                maxVal = counts[i];
                maxIdx = i;
            }
        }
        return CollaborationQuality.fromValue(maxIdx);
    }

    public Map<String, Integer> getGroupCounts() {
        Map<String, Integer> result = new LinkedHashMap<>();
        for (Map.Entry<String, EmployeeGroup> entry : groups.entrySet()) {
            result.put(entry.getKey(), entry.getValue().getSize());
        }
        return result;
    }

    public Map<String, List<Employee>> getAlphabeticalByGroup() {
        Map<String, List<Employee>> result = new LinkedHashMap<>();
        for (Map.Entry<String, EmployeeGroup> entry : groups.entrySet()) {
            result.put(entry.getKey(), entry.getValue().getSortedByLastName());
        }
        return result;
    }

    public int getTotalCount() {
        return getAllEmployees().size();
    }
}