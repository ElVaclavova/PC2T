package zamestnancidb.io;

import zamestnancidb.model.*;
import zamestnancidb.structure.EmployeeDatabase;
import java.io.*;
import java.util.*;

public class FileManager {

    public static void saveSingleEmployee(Employee e, String filename) throws IOException {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(filename))) {
            oos.writeObject(e);
        }
    }

    public static Employee loadSingleEmployee(String filename) throws IOException, ClassNotFoundException {
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(filename))) {
            return (Employee) ois.readObject();
        }
    }
}