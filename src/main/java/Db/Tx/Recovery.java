package Db.Tx;

import Db.Acid;
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
import java.util.Map;

public class Recovery {
    public int lsn = -1;
    HashMap<Integer, Integer> tIDMapLastLsn;
    static String dbName;
    ArrayList<LogRecord> logRecordList;
    HashMap<Integer, Integer> pIDRecLsnMap;
    private static int pageId;
    private static TupleDesc td;


    public Recovery(){
        logRecordList = new ArrayList<>();
        tIDMapLastLsn = new HashMap<>();
        pIDRecLsnMap = new HashMap<>();
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
        pageId = (int) logfile.length()/Utils.pageSize;
    }


    /*
    * adds a commit record to logfile
    * */
    public synchronized void commit(int tID) throws IOException {
        if (!tIDMapLastLsn.containsKey(tID)){
            return;
        }
        int prevLsn = tIDMapLastLsn.get(tID);
        LogRecord commitRecord = new LogRecord(prevLsn, LogRecord.LogType.COMMIT,
                new byte[td.tupleSize()], new byte[td.tupleSize()], -1,
                tID, -1);
        prevLsn = addLogRecord(commitRecord, tID);
        addLogRecord(new LogRecord(prevLsn, LogRecord.LogType.END, tID), tID);
        writeLogRecord();
        logRecordList.clear();
        tIDMapLastLsn.remove(tID);
    }



    /*
    * adds abort record to logfile
    * undo the changes to the last
    * */
    public synchronized void abort(int tID) throws IOException {
        if (!tIDMapLastLsn.containsKey(tID)){
            return;
        }
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

        addLogRecord(new LogRecord(lsn, LogRecord.LogType.END, tID), tID);
        writeLogRecord();
        tIDMapLastLsn.remove(tID);
    }


    /*
    *
    *
    * */
    public void recovery(String dbName, TupleDesc td, Transaction tx) throws IOException, InterruptedException {
        try {
            setupLogFile(dbName, td);
        } catch (IOException e) {
            e.printStackTrace();
        }

        int pageCount = LogRecordPage.pageCount();
        int pageIndex = 0;
        while (pageIndex <= pageCount){
            LogRecordPage logRecordPage = LogRecordPage.getPage(pageIndex);
            int recordCount =  logRecordPage.getSize();

            int recordIndex = 0;
            while (recordIndex < recordCount){
                LogRecord logRecord = LogRecord.getLogRecord(recordIndex, logRecordPage);

                LogRecord.LogType logtype = logRecord.getLogType();

                if (logtype == LogRecord.LogType.END){
                    tIDMapLastLsn.remove(logRecord.getTid());
                }else {
                    tIDMapLastLsn.put(logRecord.getTid(), logRecord.getLsn());
                }

                if(!pIDRecLsnMap.containsKey( logRecord.getPid()) && logRecord.getPid() != -1 ){
                    pIDRecLsnMap.put(logRecord.getPid(), logRecord.getLsn());
                }
                recordIndex++;
            }
            pageIndex++;
        }

        redo(tx);
        undo();
        tx.releaseAllLocks();
    }



    private void redo(Transaction tx) throws IOException, InterruptedException {
        Manager manager = Acid.getDatabase().bufferPoolManager;
        int pageCount = LogRecordPage.pageCount();
        int pageIndex = 0;

        while (pageIndex <= pageCount){
            LogRecordPage logRecordPage = LogRecordPage.getPage(pageIndex);
            int recordCount =  logRecordPage.getSize();

            int recordIndex = 0;
            while (recordIndex < recordCount){
                LogRecord logRecord = LogRecord.getLogRecord(recordIndex, logRecordPage);
                LogRecord.LogType logtype = logRecord.getLogType();

                if (!(logtype.equals(LogRecord.LogType.UPDATE) || logtype.equals(LogRecord.LogType.CLR) )){
                    recordIndex++;
                    continue;
                }
                if (!
                        (!pIDRecLsnMap.containsKey(logRecord.getPid()) ||
                                (pIDRecLsnMap.containsKey(logRecord.getPid()) &&
                                        (pIDRecLsnMap.get(logRecord.getPid()) > logRecord.getLsn())
                                )
                        )
                ){
                    Page page = manager.getPage(logRecord.getPid(), tx, Permission.EXCLUSIVE);
                    if (! (page.getHeader(PageHeaderEnum.LSN) >= logRecord.getLsn()) ){
                        Tuple temp = new Tuple(logRecord.getNextByte(), td);
                        page.replaceTuple(logRecord.getOffset(), temp, td);
                    }
                }
                recordIndex++;
            }
            pageIndex++;
        }
    }


    private void undo(){



    }

    /*
     * flush the log till the given lsn
     * */
    private void writeLogRecord() throws IOException {
        LogRecordPage logRecordPage =  LogRecordPage.getPage(pageId);
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
