package Db.Tx;


import Db.diskManager.PageHeaderEnum;

import java.io.IOException;

public class LogIterator {

    int pageIndex;
    int logID;
    boolean direction;
    LogRecordPage page;
    int recordIndexInPage;
    int recordSize;
    LogRecord current;


    /*
    * if direction is false
    * it iterates from last to first
    * */
    public LogIterator(boolean direction) throws IOException {
        recordSize = LogRecord.size();
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
        pageIndex = 0;
    }


    public boolean hasNext() throws IOException {
        if(current == null){
            current = next();
        }
        if(current== null){
            return false;
        }else {
            return true;
        }
    }


    public LogRecord next() throws IOException {
        if(current == null) {
            current = getNext();
        }
        LogRecord tempRecord = current;
        current = null;
        return tempRecord;
    }


    private LogRecord getNext() throws IOException {
        int headerSize = 0;
        int recordSize = 0;
        int count = page.getHeader(PageHeaderEnum.SIZE);

        if(recordIndexInPage < count) {
            byte[] data = page.pageData;
            byte[] logRecordData = new byte[recordSize];
            int offset = recordIndexInPage*recordSize;
            System.arraycopy(data,offset, logRecordData, 0, logRecordData.length);
            recordIndexInPage++;
            new LogRecord(logRecordData);
        } else if(hasNextPage()){
            page = getNextPage();
            recordIndexInPage = 0;
            byte[] data = page.pageData;
            int offset = recordIndexInPage*recordSize;
            byte[] logRecordData = new byte[recordSize];
            System.arraycopy(data,offset, logRecordData, 0, logRecordData.length);
            recordIndexInPage++;
        }
        return null;
    }


    private boolean hasNextPage() throws IOException {
        if(pageIndex < LogRecordPage.pageCount()){
            return true;
        }

        return false;
    }

    private LogRecordPage getNextPage() throws IOException {

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
