package Db.Tx;

import Db.Utils;
import Db.bufferManager.Manager;
import Db.catalog.Tuple;
import Db.catalog.TupleDesc;
import Db.diskManager.Page;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

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
    public void recover(String dbName, Manager bfPool, Transaction tx, TupleDesc td) throws IOException, InterruptedException {
        LogRecord.setTupleDesc(td);
        LogRecordPage.setFile(dbName+ ".log");
        LogIterator iterator = new LogIterator(true);
        if(iterator.hasNext()){
            LogRecord record = iterator.next();
            Page page = bfPool.getPage(record.pageID, tx, Permission.EXCLUSIVE);

            if(page.getHeader("lsn") < record.lsn){
                if(record.logtype == LogRecord.insert){
                    page.insertTuple(new Tuple(record.nextByte, null));
                }
                if(record.logtype == LogRecord.update){
                    page.update(record.offset, new Tuple(record.nextByte, null));
                }
                if(record.logtype == LogRecord.delete){
                    page.deleteTuple(record.offset);
                }
                if(record.logtype == LogRecord.clr){
                    undo(record.tId, record.lsn);
                }
            }
        }

    }


    /*
    * used when setup to create
    * */
    public static void setupLogFile(String dbname, boolean isNew, TupleDesc td) throws IOException {
        LogRecord.setTupleDesc(td);
        dbName = dbname;
        if(isNew){
//            create new log file
            File logfile = new File (Utils.dbFolderPath +"/"+ dbname + ".log" );
            logfile.createNewFile();
        }
        LogRecordPage.setFile(dbname+ ".log");
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
    public synchronized void abort(int tID) throws IOException {
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
    private void undo(int tid, int lsn) throws IOException {
        LogRecord temp = new LogRecord(lsn);


    }


    /*
    * flush the log till the given lsn
    * */
    private void writeLogRecord(int lsn){
        LogRecordPage page = new LogRecordPage();

        Iterator<LogRecord> iter = logRecordList.iterator();
        while (iter.hasNext()){
            iter.next();
        }
        while (lastwriteLsn == lsn){
            lastwriteLsn++;
            writeLogRecord(lastwriteLsn);

        }
    }

}
