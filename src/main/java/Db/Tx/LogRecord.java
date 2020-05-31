package Db.Tx;

public class LogRecord {

    int lsn;
    int prevLsn;

    int pageID;
    int tId;
    public static int update = 1;
    public static int delete = 1;
    public static int commit = 1;
    public static int rollback = 1;

    int logtype;

    int recordId;

    public LogRecord(int lsn, int prevLsn, int logtype, byte[] prev, byte[] next){


    }


    public byte[] encodeLog(){

        return null;
    }

    public LogRecord decodeLog(){

        return null;
    }




}
