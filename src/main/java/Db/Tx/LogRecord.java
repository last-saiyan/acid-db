package Db.Tx;

import Db.Utils;
import Db.catalog.TupleDesc;
import Db.diskManager.PageHeaderEnum;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

public class LogRecord {

    private TreeMap <PageHeaderEnum, Integer> headerMap ;

    private static int headerSize;

    public enum LogType{
        UPDATE, COMMIT, ABORT, END, CLR
    }

    private static HashMap<LogType, Integer> logTypeMap;

    static {
        logTypeMap = new HashMap();
        logTypeMap.put(LogType.UPDATE, 1);
        logTypeMap.put(LogType.COMMIT, 2);
        logTypeMap.put(LogType.ABORT, 3);
        logTypeMap.put(LogType.END, 4);
        logTypeMap.put(LogType.CLR, 5);

        PageHeaderEnum[] headers = {PageHeaderEnum.ID, PageHeaderEnum.LSN, PageHeaderEnum.PREV_LSN,
                PageHeaderEnum.LOG_TYPE, PageHeaderEnum.OFFSET, PageHeaderEnum.TID, PageHeaderEnum.UNDO_NEXT_LSN};

        headerSize = headers.length;
    }

    byte[] prevByte;
    byte[] nextByte;

    static TupleDesc td;


    public LogRecord(int prevLsn, LogType logtype, byte[] prev, byte[] next, int pageID, int tId, int offset){
        headerMap = new TreeMap<>();
        headerMap.put(PageHeaderEnum.PREV_LSN, prevLsn);
        headerMap.put(PageHeaderEnum.LOG_TYPE, logTypeMap.get(logtype));
        headerMap.put(PageHeaderEnum.ID, pageID);
        headerMap.put(PageHeaderEnum.TID, tId);
        headerMap.put(PageHeaderEnum.OFFSET, offset);
        headerMap.put(PageHeaderEnum.UNDO_NEXT_LSN, -1);
        headerMap.put(PageHeaderEnum.LSN, -1);

        if (prev == null){
            prev = new byte[td.tupleSize()];
        }
        if (next == null){
            next = new byte[td.tupleSize()];
        }
        prevByte = prev;
        nextByte = next;
    }

    public LogRecord(int prevLsn, LogType logtype, int tId, int undoNextLSN){
        headerMap = new TreeMap<>();
        headerMap.put(PageHeaderEnum.PREV_LSN, prevLsn);
        headerMap.put(PageHeaderEnum.LOG_TYPE,  logTypeMap.get(logtype));
        headerMap.put(PageHeaderEnum.TID, tId);
        headerMap.put(PageHeaderEnum.UNDO_NEXT_LSN, undoNextLSN);
        headerMap.put(PageHeaderEnum.ID, -1);
        headerMap.put(PageHeaderEnum.OFFSET, -1);
        headerMap.put(PageHeaderEnum.LSN, -1);

        prevByte = new byte[td.tupleSize()];
        nextByte = new byte[td.tupleSize()];
    }

    public LogRecord(int prevLsn, LogType logtype, int tId){
        headerMap = new TreeMap<>();
        headerMap.put(PageHeaderEnum.PREV_LSN, prevLsn);
        headerMap.put(PageHeaderEnum.LOG_TYPE,  logTypeMap.get(logtype));
        headerMap.put(PageHeaderEnum.TID, tId);
        headerMap.put(PageHeaderEnum.UNDO_NEXT_LSN, -1);
        headerMap.put(PageHeaderEnum.ID, -1);
        headerMap.put(PageHeaderEnum.OFFSET, -1);
        headerMap.put(PageHeaderEnum.LSN, -1);

        prevByte = new byte[td.tupleSize()];
        nextByte = new byte[td.tupleSize()];
    }


    int getPrevLsn(){
        return headerMap.get(PageHeaderEnum.PREV_LSN);
    }

    public static void setTupleDesc(TupleDesc tupleDesc){
        td = tupleDesc;
    }


    public static LogRecord getLogRecord(int lsn)  {
        int pageCapacity = Utils.pageSize/size();
        int pageID = lsn/pageCapacity;
        int recordId = lsn%pageCapacity;
        int pageOffset = recordId*size();

        LogRecordPage page = null;
        try {
            page = LogRecordPage.getPage(pageID);
        } catch (IOException e) {
            e.printStackTrace();
        }

        byte[] recordData = new byte[size()];
        System.arraycopy(page.pageData, pageOffset, recordData, 0, recordData.length);

        return new LogRecord(recordData);
    }


    public static LogRecord getLogRecord(int recordId, LogRecordPage page)  {
        int pageOffset = recordId*size();
        byte[] recordData = new byte[size()];
        System.arraycopy(page.pageData, pageOffset, recordData, 0, recordData.length);
        return new LogRecord(recordData);
    }


    public LogRecord(byte[] data){
        headerMap = new TreeMap<>();
        headerMap.put(PageHeaderEnum.PREV_LSN, -1);
        headerMap.put(PageHeaderEnum.LOG_TYPE, -1);
        headerMap.put(PageHeaderEnum.TID, -1);
        headerMap.put(PageHeaderEnum.UNDO_NEXT_LSN, -1);
        headerMap.put(PageHeaderEnum.ID, -1);
        headerMap.put(PageHeaderEnum.OFFSET, -1);
        headerMap.put(PageHeaderEnum.LSN, -1);


        byte[] intByte = new byte[4];
        int index = 0;

        for (Map.Entry<PageHeaderEnum, Integer>entry: headerMap.entrySet()){
            System.arraycopy(data, index*4, intByte, 0, intByte.length);
            int temp = Utils.byteToInt(intByte);
            headerMap.put(entry.getKey(), temp);
            index++;
        }

        prevByte = new byte[td.tupleSize()];
        System.arraycopy(data, index*4, prevByte, 0, td.tupleSize());

        index = index*4 + prevByte.length;

        nextByte = new byte[td.tupleSize()];
        System.arraycopy(data, index, nextByte, 0, td.tupleSize());

    }


    void setLsn(int lsn){
        headerMap.put(PageHeaderEnum.LSN, lsn);
    }

    int getLsn(){
        return headerMap.get(PageHeaderEnum.LSN);
    }
    int getTid(){
        return headerMap.get(PageHeaderEnum.TID);
    }
    int getPid(){
        return headerMap.get(PageHeaderEnum.ID);
    }
    int getOffset(){
        return headerMap.get(PageHeaderEnum.OFFSET);
    }
    int getUndoNextLsn(){ return headerMap.get(PageHeaderEnum.UNDO_NEXT_LSN); }
    byte[] getNextByte(){
        return nextByte;
    }
    byte[] getPrevByte(){
        return prevByte;
    }


    LogType getLogType(){
        int logID = headerMap.get(PageHeaderEnum.LOG_TYPE);
        for(Map.Entry<LogType, Integer> logType: logTypeMap.entrySet()){
            if (logID == logType.getValue()){
                return logType.getKey();
            }
        }
        return null;
    }


    /*
    *
    * */
    public byte[] encodeLog(){
        byte[] recordData = new byte[size()];
        byte[] intByte;
        int index = 0;

        for (Map.Entry<PageHeaderEnum, Integer>entry: headerMap.entrySet()){
            intByte = Utils.intToByte(entry.getValue());
            System.arraycopy(intByte, 0, recordData, index*4, intByte.length);
            index++;
        }

        System.arraycopy(prevByte, 0, recordData, index*4, td.tupleSize());
        index = index*4 + prevByte.length;
        System.arraycopy(nextByte, 0, recordData, index, nextByte.length);
        return recordData;
    }


    public static int size(){
        return td.tupleSize()*2 + headerSize*4;
    }

}
