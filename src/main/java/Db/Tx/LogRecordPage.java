package Db.Tx;


import Db.Utils;

import java.util.HashMap;

public class LogRecordPage {

    static int pageSize = Utils.pageSize;
    byte[] pageData = new byte[pageSize];
    HashMap<String, Integer> headers;

    public LogRecordPage(){
//        todo initilize pageId
        int pageID = -1;
        headers = new HashMap<>();
        headers.put("count", 0);
        headers.put("ID", pageID);
    }

    public boolean addLogRecord(LogRecord logRecord){
        byte[] recordData = logRecord.encodeLog(null);

        int recordCount = getHeader("count");
//        find better approach as the header can be updated
        int headerSize = headers.size()*4;
        int offset = headerSize + recordCount*LogRecord.size(null);
        if((recordCount*LogRecord.size(null) +
                recordData.length + headerSize) < pageSize){

            System.arraycopy(recordData, 0, pageData, offset, recordData.length);
            headers.put("count", recordCount+1);
            return true;
        }
        return false;
    }


    public byte[] getData(){
        int headerSize = headers.size();
        int recordCount = getHeader("count");
        byte[] data = new byte[recordCount*LogRecord.size(null)];
        System.arraycopy(pageData,headerSize, data, 0, data.length);
        return pageData;
    }


    public int getHeader(String headerName){
        if(headers.containsKey(headerName)){
            return headers.get(headerName);
        }
        return -1;
    }

    public void writePageToDisk(){
        int pageID = getHeader("ID");
        int offset = pageID*pageSize;

    }

    public static LogRecordPage getPage(int pageID){
        int offset = pageID*pageSize;
//        read here
        return null;
    }


    public LogRecordPage getNextPage(){
        int pageID = getHeader("ID");
        pageID++;
        if (pageID > pageCount())
            return null;
        return getPage(pageID);
    }

    public LogRecordPage getPrevPage(){
        int pageID = getHeader("ID");
        pageID--;
        if(pageID<0)
            return null;
        return getPage(pageID);
    }



    public static int pageCount(){

        return -1;
    }


    public static LogRecordPage getFirstPage(){
        return getPage(0);
    }

    public static LogRecordPage getLastPage(){
        return getPage(pageCount());
    }
}
