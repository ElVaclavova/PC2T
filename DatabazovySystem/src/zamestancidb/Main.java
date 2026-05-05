package zamestancidb;

import zamestnancidb.io.SqlManager;
import zamestnancidb.structure.EmployeeDatabase;
import zamestnancidb.ui.Menu;

public class Main {
    public static void main(String[] args) {
        EmployeeDatabase db = new EmployeeDatabase();
        
        SqlManager.inicializuj();
        SqlManager.nactiVse(db);

        Menu menu = new Menu(db); 
        menu.run();

        System.out.println("Zálohuji data do SQL...");
        SqlManager.ulozVse(db);
    }
}