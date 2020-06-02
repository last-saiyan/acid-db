package Db.Tx;


import java.util.ArrayList;

public class LogRecordPage {


    public boolean addLogRecord(LogRecord logRecord){

        return false;
    }


    public void writePageToDisk(){

    }

    public static LogRecordPage getPage(int pageID){

        return null;
    }


    public LogRecordPage getNextPage(){

        return null;
    }

    public LogRecordPage getPrevPage(){

        return null;
    }

    public static int pageCount(){

        return -1;
    }

    public static LogRecordPage getFirstPage(){

        return null;
    }

    public static LogRecordPage getLastPage(){

        return null;
    }
}
