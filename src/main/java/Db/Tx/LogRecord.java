package Db.Tx;

import Db.Utils;
import Db.catalog.TupleDesc;

import java.io.IOException;

public class LogRecord {

    int lsn;
    int prevLsn;

    int pageID;
    int tId;
    int undoNextLSN;

    public static int updateType = 1;
    public static int commitType = 2;
    public static int abortType = 3;
    public static int endType = 4;
    public static int clrType = 5;



    byte[] prevByte;
    byte[] nextByte;
    int logtype;
    int offset;
    static TupleDesc td;


    public LogRecord(int prevLsn, int logtype, byte[] prev, byte[] next, int pageID, int tId, int offset){
        this.prevLsn = prevLsn;
        this.logtype = logtype;
        this.pageID = pageID;
        this.tId = tId;
        prevByte = prev;
        nextByte = next;
        this.offset = offset;
    }

    public LogRecord(int prevLsn, int logtype, int tId, int undoNextLSN){
        this.prevLsn = prevLsn;
        this.logtype = logtype;
        this.tId = tId;
        this.undoNextLSN = undoNextLSN;
        prevByte = new byte[td.tupleSize()];
        nextByte = new byte[td.tupleSize()];
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


    public LogRecord(byte[] data){

        byte[] intByte = new byte[4];

        System.arraycopy(data, 0, intByte, 0, 4);
        lsn = Utils.byteToInt(intByte);

        System.arraycopy(data, 4, intByte, 0, 4);
        prevLsn = Utils.byteToInt(intByte);

        System.arraycopy(data, 8, intByte, 0, 4);
        logtype = Utils.byteToInt(intByte);

        System.arraycopy(data, 12, intByte, 0, 4);
        pageID = Utils.byteToInt(intByte);

        System.arraycopy(data, 16, intByte, 0, 4);
        tId = Utils.byteToInt(intByte);

        System.arraycopy(data, 20, intByte, 0, 4);
        offset = Utils.byteToInt(intByte);

        byte[] prevByte = new byte[td.tupleSize()];
        System.arraycopy(data, 24, prevByte, 0, td.tupleSize());
        this.prevByte = prevByte;

        byte[] nextByte = new byte[td.tupleSize()];
        System.arraycopy(data, (24 + td.tupleSize()), nextByte, 0, td.tupleSize());
        this.nextByte = nextByte;

    }


    public void setLsn(int lsn){
        this.lsn = lsn;
    }


    /*
    * todo - find better way to do this if number of items increases
    *
    * */
    public byte[] encodeLog(){
        byte[] recordData = new byte[size()];
        byte[] intByte;

        intByte = Utils.intToByte(lsn);
        System.arraycopy(intByte, 0, recordData, 0, 4);

        intByte = Utils.intToByte(prevLsn);
        System.arraycopy(intByte, 0, recordData, 4, 4);

        intByte = Utils.intToByte(logtype);
        System.arraycopy(intByte, 0, recordData, 8, 4);

        intByte = Utils.intToByte(pageID);
        System.arraycopy(intByte, 0, recordData, 12, 4);

        intByte = Utils.intToByte(tId);
        System.arraycopy(intByte, 0, recordData, 16, 4);

        intByte = Utils.intToByte(offset);
        System.arraycopy(intByte, 0, recordData, 20, 4);

        if(prevByte == null){
            prevByte = new byte[td.tupleSize()];
        }
        System.arraycopy(prevByte, 0, recordData, 24, td.tupleSize());

        System.arraycopy(nextByte, 0, recordData, (24+td.tupleSize()), 4);

        return recordData;
    }

    public static int size(){
//        20 is the size of other int fields, need to find a better way to do this
        return td.tupleSize()*2 + 24;
    }

}
