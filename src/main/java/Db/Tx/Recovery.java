package Db.Tx;

import java.util.ArrayList;
import java.util.HashMap;

public class Recovery {
    public int lsn = 0;
    public int lastwriteLsn;
    int tid;
    HashMap<Integer, Integer> tIDMapLastLsn;
    static String dbName;
    ArrayList<LogRecord> logRecordList;


    public Recovery(){
        logRecordList = new ArrayList<>();
        tIDMapLastLsn = new HashMap<>();

    }


    /*
    * makes new entry in tIDMapLastLsn
    * is called when new transaction is started
    * */
    public void newTransaction(int tid){
        tIDMapLastLsn.put(tid, null);
    }


    /*
    * adds log item to log list
    * returns its lsn
    * rewrite this using immutable data-structures (concurrency)
    * */
    public synchronized int addLogRecord(LogRecord record, int tID){
        lsn++;
        tIDMapLastLsn.put(tID, lsn);
        record.lsn = lsn;
        logRecordList.add(record);
        return lsn;
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
    public synchronized void commit(int tID){
        lsn++;
        LogRecord commitRecord = null;
        logRecordList.add(commitRecord);
        writeLogRecord(lsn);
    }


    /*
    * adds abort record to logfile
    * undo the changes to the last
    * */
    public synchronized void abort(int tID){
        lsn++;
        LogRecord crlRecord = null;
        logRecordList.add(crlRecord);
        undo(tID, lsn);
        writeLogRecord(lsn);
    }


    /*
    * todo
    *  undo transaction by going to previous log records of previous LSN
    * undo the changes to the pages in buffer pool
    * question what should i update the page LSN?
    * */
    private void undo(int tid, int lsn){

    }


    /*
    * flush the log till the given lsn
    * */
    private void writeLogRecord(int lsn){
        lastwriteLsn = lsn;

    }


    private LogRecordPage getPage(int pageId){

        return null;
    }



}
