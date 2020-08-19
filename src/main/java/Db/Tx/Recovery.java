package Db.Tx;

import Db.Utils;
import Db.bufferManager.Manager;
import Db.catalog.Tuple;
import Db.catalog.TupleDesc;
import Db.diskManager.Page;
import Db.diskManager.PageHeaderEnum;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

public class Recovery {
    public int lsn = -1;
    HashMap<Integer, Integer> tIDMapLastLsn;
    static String dbName;
    ArrayList<LogRecord> logRecordList;
    HashMap<Integer, Integer> recLsnPIDMap;
    private int pageId;
    private static TupleDesc td;


    public Recovery(){
        logRecordList = new ArrayList<>();
        tIDMapLastLsn = new HashMap<>();
        recLsnPIDMap = new HashMap<>();
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
        record.setLsn(lsn);
        logRecordList.add(record);
        return lsn;
    }


    /*
    * used when setup to create
    * */
    public static void setupLogFile(String dbname, TupleDesc td1) throws IOException {
        LogRecord.setTupleDesc(td1);
        dbName = dbname;
        td = td1;
        File logfile = new File(Utils.dbFolderPath + "/" + dbname + ".log");
        if (!logfile.exists()) {
            logfile.createNewFile();
        }
        LogRecordPage.setFile(logfile);
    }


    /*
    * adds a commit record to logfile
    * */
    public synchronized void commit(int tID) throws IOException {
        int prevLsn = tIDMapLastLsn.get(tID);
        LogRecord commitRecord = new LogRecord(prevLsn, LogRecord.LogType.COMMIT, new byte[td.tupleSize()], new byte[td.tupleSize()], -1, tID, -1);
        addLogRecord(commitRecord, tID);

        writeLogRecord();
        logRecordList.clear();
    }



    /*
    * adds abort record to logfile
    * undo the changes to the last
    * */
    public synchronized void abort(int tID) throws IOException {
        int prevLsn = tIDMapLastLsn.get(tID);
        LogRecord abortRecord = new LogRecord(prevLsn, LogRecord.LogType.ABORT, new byte[td.tupleSize()], new byte[td.tupleSize()], -1, tID, -1);
        addLogRecord(abortRecord, tID);
        writeLogRecord();

        LogRecord prevLogRecord = abortRecord;
        while (prevLogRecord.getPrevLsn() != -1) {
            LogRecord clrRecord = new LogRecord(lsn, LogRecord.LogType.CLR, tID, prevLogRecord.getPrevLsn());
            addLogRecord(clrRecord, tID);
            prevLogRecord = LogRecord.getLogRecord(prevLogRecord.getPrevLsn());
        }

        writeLogRecord();
    }



    /*
     * flush the log till the given lsn
     * */
    private void writeLogRecord() throws IOException {
        LogRecordPage logRecordPage = new LogRecordPage(pageId);

        Iterator<LogRecord> iter = logRecordList.iterator();

        while (iter.hasNext()){
            LogRecord tempLogRecord = iter.next();
            if(!logRecordPage.addLogRecord(tempLogRecord)){
                logRecordPage.writePageToDisk();
                pageId++;
                logRecordPage = new LogRecordPage(pageId);
                logRecordPage.addLogRecord(tempLogRecord);
            }
        }

        logRecordPage.writePageToDisk();
    }

}
