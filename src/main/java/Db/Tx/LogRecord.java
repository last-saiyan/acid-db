package Db.Tx;

import Db.Tuples.Tuple;
import Db.Utils;
import Db.catalog.TupleDesc;

public class LogRecord {

    int lsn;
    int prevLsn;

    int pageID;
    int tId;
    public static int update = 1;
    public static int delete = 1;
    public static int commit = 1;
    public static int rollback = 1;
    byte[] prevByte;
    byte[] nextByte;
    int logtype;

    public LogRecord(int prevLsn, int logtype, byte[] prev, byte[] next, int pageID, int tId){
        this.prevLsn = prevLsn;
        this.logtype = logtype;
        this.pageID = pageID;
        this.tId = tId;
        prevByte = prev;
        nextByte = next;
    }

    public LogRecord(TupleDesc td, byte[] data){
        byte[] intByte = new byte[4];

        System.arraycopy(data, 0, intByte, 0, 4);
        lsn = Utils.byteToInt(intByte);

        System.arraycopy(data, 4, intByte, 0, 4);
        prevLsn = Utils.byteToInt(intByte);

        System.arraycopy(data, 8, intByte, 0, 4);
        logtype = Utils.byteToInt(intByte);

        System.arraycopy(data, 16, intByte, 0, 4);
        pageID = Utils.byteToInt(intByte);

        System.arraycopy(data, 20, intByte, 0, 4);
        tId = Utils.byteToInt(intByte);

        byte[] prevByte = new byte[td.tupleSize()];
        System.arraycopy(data, 20, prevByte, 0, td.tupleSize());
        this.prevByte = prevByte;

        byte[] nextByte = new byte[td.tupleSize()];
        System.arraycopy(data, (20+td.tupleSize()), nextByte, 0, td.tupleSize());
        this.nextByte = nextByte;

    }


    public void setLsn(int lsn){
        this.lsn = lsn;
    }


    /*
    * todo - find better way to do this if number of items increases
    *
    * */
    public byte[] encodeLog(TupleDesc td){
        byte[] recordData = new byte[size(td)];
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

        System.arraycopy(prevByte, 0, recordData, 20, td.tupleSize());

        System.arraycopy(nextByte, 0, recordData, (20+td.tupleSize()), 4);

        return recordData;
    }

    public static int size(TupleDesc td){
//        20 is the size of other int fields, need to find a better way to do this
        return td.tupleSize()*2 + 20;
    }


}
