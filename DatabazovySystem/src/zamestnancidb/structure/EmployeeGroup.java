package zamestnancidb.structure;

import zamestnancidb.model.Employee;
import java.util.ArrayList;
import java.util.List;

public class EmployeeGroup {
	
	private final String name;
	private final LinkedList<Employee> employees;
	
	public EmployeeGroup(String name) {
		this.name = name;
		this.employees = new LinkedList<>();
	}
	
	public String getName() {
		return name;
	}
	
	public void addEmployee(Employee e) {
		employees.add(e);
	}
	
	public boolean removeEmployee(Employee e) {
		return employees.remove(e);
	}
	
	public Employee findById(int id) {
        for (Employee e : employees) {
            if (e.getID() == id) return e;
        }
        return null;
    }

    public List<Employee> getAllEmployees() {
        List<Employee> list = new ArrayList<>();
        for (Employee e : employees) {
            list.add(e);
        }
        return list;
    }

    public List<Employee> getSortedByLastName() {
        List<Employee> list = getAllEmployees();
        list.sort((a, b) -> a.getLastName().compareToIgnoreCase(b.getLastName()));
        return list;
    }

    public int getSize() { return employees.size(); }
    public boolean isEmpty() { return employees.isEmpty(); }

    @Override
    public String toString() {
        return String.format("Skupina '%s' (%d zamestnancov)", name, getSize());
    }
	

}
