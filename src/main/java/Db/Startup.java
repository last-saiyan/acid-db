package Db;

public class Startup {

    static Acid database;

    public static void main(String[] args) throws Exception {
//        directory path for database
        String dir = "";
//        existing database name
//        if empty need to create a new database
        String dbName = "";

        database = Acid.getDatabase();
        database.run();

    }
}
