package Db.Tx;

import Db.diskManager.Page;

import java.io.File;

public class LogIterator {

    File logFile;
    int pageIndex;
    int logID;
    boolean direction;
    LogRecordPage page;
    /*
    * if direction is false
    * it iterates from last to first
    * */
    public LogIterator(boolean direction){
        logID = 0;
        this.direction = direction;
        if(direction){
            page = LogRecordPage.getFirstPage();
            pageIndex = 0;
        }else {
            page = LogRecordPage.getLastPage();
            pageIndex = LogRecordPage.pageCount();
        }
    }


    public void reset(){


    }


    public boolean hasNext(){

        return false;
    }



    public LogRecord next(){
        int headerSize = 0;
        int recordSize = 0;
        int count = page.getHeader("count");


        return null;
    }



    private LogRecordPage getNextPage(){

        if(direction){
            pageIndex++;
            if(pageIndex<= LogRecordPage.pageCount()) {
                return page.getNextPage();
            }else {
                return null;
            }
        }else {
            pageIndex--;
            if(pageIndex >= 0) {
                return page.getPrevPage();
            }else {
                return null;
            }
        }
    }


}
