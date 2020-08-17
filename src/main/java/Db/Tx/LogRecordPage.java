package Db.Tx;


import Db.Utils;

import java.io.File;
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

    public LogRecordPage(int pageId) {
        headers = new HashMap<>();
        headers.put("count", 0);
        headers.put("id", pageId);
        pageData = new byte[pageSize - headers.size()*4];
    }

    public LogRecordPage(byte[] data){

        headers = decodeHeader(data);

        pageData = new byte[pageSize - headers.size()*4];
        System.arraycopy(data, headers.size()*4, pageData, 0, pageData.length);

    }


    public static void setFile(File logFileName) throws FileNotFoundException {
        logRecordFile = new RandomAccessFile(logFileName, "rw");
    }



    public boolean addLogRecord(LogRecord logRecord){
        byte[] recordData = logRecord.encodeLog();
        int recordCount = getHeader("count");
//        find better approach as the header can be updated
        int headerSize = headers.size()*4;
        int offset = recordCount*LogRecord.size();
        if(((recordCount+1)*LogRecord.size() + headerSize) < pageSize){
            System.arraycopy(recordData, 0, pageData, offset, recordData.length);
            headers.put("count", recordCount+1);
            return true;
        }
        return false;
    }


    public byte[] encodeHeader(){
        int headerSize = headers.size();
        byte[] headerByte = new byte[headerSize*4];
        int index = 0;

        byte[] temp = Utils.intToByte(headers.get("count"));
        System.arraycopy(temp,0,headerByte,index*4,temp.length);


        index = 1;
        temp = Utils.intToByte(headers.get("id"));
        System.arraycopy(temp,0,headerByte,index*4,temp.length);

        return headerByte;
    }


    public HashMap<String, Integer> decodeHeader(byte[] data){

        HashMap<String,Integer> header = new HashMap();
        byte[] size = new byte[4], id = new byte[4];

        System.arraycopy(data,0, size,0,size.length);
        header.put("count", Utils.byteToInt(size));

        System.arraycopy(data,size.length, id,0,id.length);
        header.put("id", Utils.byteToInt(id));

        return header;
    }


    public int getHeader(String headerName){
        if(headers.containsKey(headerName)){
            return headers.get(headerName);
        }
        return -1;
    }


    public void writePageToDisk() throws IOException {
        int pageID = getHeader("id");

        int offset = pageID*pageSize;
        logRecordFile.seek(offset);

        byte[] data = new byte[Utils.pageSize];
        byte[] headerData = encodeHeader();
        System.arraycopy(headerData,0, data, 0, headers.size()*4);
        System.arraycopy(pageData, 0, data, headers.size()*4, (pageData.length));

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
        int pageID = getHeader("id");
        pageID++;
        if (pageID > pageCount())
            return null;
        return getPage(pageID);
    }

    public LogRecordPage getPrevPage() throws IOException {
        int pageID = getHeader("id");
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
