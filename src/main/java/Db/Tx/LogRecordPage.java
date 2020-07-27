package Db.Tx;


import Db.Utils;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.HashMap;
import java.util.Map;

public class LogRecordPage {

    static int pageSize = Utils.pageSize;
    byte[] pageData;
    HashMap<String, Integer> headers;
    static RandomAccessFile logRecordFile;

    public LogRecordPage() {
//        todo initilize pageId
        int pageID = -1;
        headers = new HashMap<>();
        headers.put("count", 0);
        headers.put("ID", pageID);
        pageData = new byte[pageSize];
    }

    public LogRecordPage(byte[] data){
        pageData = data;
    }


    public static void setFile(String logFileName) throws FileNotFoundException {
        logRecordFile = new RandomAccessFile( logFileName , "rw");
    }



    public boolean addLogRecord(LogRecord logRecord){
        byte[] recordData = logRecord.encodeLog();

        int recordCount = getHeader("count");
//        find better approach as the header can be updated
        int headerSize = headers.size()*4;
        int offset = headerSize + recordCount*LogRecord.size();
        if((recordCount*LogRecord.size() +
                recordData.length + headerSize) < pageSize){

            System.arraycopy(recordData, 0, pageData, offset, recordData.length);
            headers.put("count", recordCount+1);
            return true;
        }
        return false;
    }


    private byte[] encodePageData(){
        int headerSize = headers.size();
        int recordCount = getHeader("count");
        byte[] data = new byte[recordCount*LogRecord.size()];
        System.arraycopy(pageData,headerSize, data, 0, data.length);
        return pageData;
    }


    public byte[] encodeHeader(){
        int headerSize = headers.size();
        byte[] headerByte = new byte[headerSize*4];
        int index = 0;
        for(Map.Entry<String, Integer> entry : headers.entrySet()){
            byte[] temp = Utils.intToByte(entry.getValue());
            System.arraycopy(temp,0,headerByte,index*4,temp.length);
            index++;
        }
        return headerByte;
    }


    public HashMap<String, Integer> decodeHeader(byte[] data){

        HashMap<String,Integer> header = new HashMap();
        byte[] size = new byte[4], id = new byte[4];

        System.arraycopy(data,0, size,0,size.length);
        System.arraycopy(data,size.length, id,0,id.length);

        header.put("id", Utils.byteToInt(id));
        header.put("count", Utils.byteToInt(size));
        return header;
    }


    public int getHeader(String headerName){
        if(headers.containsKey(headerName)){
            return headers.get(headerName);
        }
        return -1;
    }


    public void writePageToDisk() throws IOException {
        int pageID = getHeader("ID");
        int offset = pageID*pageSize;
        logRecordFile.seek(offset);
        byte[] data = new byte[Utils.pageSize];
        System.arraycopy(encodeHeader(),0, data, 0, headers.size()*4);

        byte[] pageData = encodePageData();
        System.arraycopy(pageData, 0, data, headers.size()*4, pageData.length);
        logRecordFile.write(data);
    }

    public static LogRecordPage getPage(int pageID) throws IOException {
//        todo check if its present
        int offset = pageID*pageSize;
        byte[] pageData = new byte[Utils.pageSize];
        logRecordFile.seek(offset);
        logRecordFile.read(pageData);
        return new LogRecordPage(pageData);
    }


    public LogRecordPage getNextPage() throws IOException {
        int pageID = getHeader("ID");
        pageID++;
        if (pageID > pageCount())
            return null;
        return getPage(pageID);
    }

    public LogRecordPage getPrevPage() throws IOException {
        int pageID = getHeader("ID");
        pageID--;
        if(pageID<0)
            return null;
        return getPage(pageID);
    }



    public static int pageCount() throws IOException {
        return (int) logRecordFile.length();
    }


    public static LogRecordPage getFirstPage() throws IOException {
        return getPage(0);
    }

    public static LogRecordPage getLastPage() throws IOException {
        return getPage(pageCount());
    }
}
