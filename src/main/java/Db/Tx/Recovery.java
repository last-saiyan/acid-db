package Db.Tx;

import java.util.HashMap;

public class Recovery {
    public int lsn = 0;

    public int LastLsn;

    int tid;

    HashMap<Integer, Integer> pIDMapLsn;

    HashMap<Integer, Integer> tIDMapLastLsn;

    static String dbName;

    public Recovery(int tID){
        this.tid = tID;
    }


    /*
    * iterates over log records and recreates the database
    * */
    public void recover(String dbName){

    }


    /*
    * used when setup to create
    * */
    public static void setupLogFile(String dbname, boolean isNew){
        dbName = dbname;
    }


    /*
    * adds a commit record to logfile
    * */
    public void commit(){


    }


    /*
    * adds commit record to logfile
    * */
    public void abort(){

    }

    public void addLogRecord(int tId, LogRecord record){


    }


    private void writeLogRecord(){


    }


    public void rollback(){

    }





}
