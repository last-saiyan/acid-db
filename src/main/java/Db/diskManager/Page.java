package Db.diskManager;

import Db.Utils;
import Db.catalog.Tuple;
import Db.catalog.TupleDesc;

import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

public class Page implements Utils {

    private TreeMap<PageHeaderEnum, Integer> pageHeader = new TreeMap();
    public byte[] pageData;
    public static TupleDesc td;
    public int pageDataCapacity;

    /*
    * header should be encoded and decoded in the same order
    *
    * */

    public Page(int id, TupleDesc td){

        pageHeader.put(PageHeaderEnum.ID, id);
        pageHeader.put(PageHeaderEnum.SIZE, 0);
        pageHeader.put(PageHeaderEnum.LSN, -1);
        pageData = new byte[Utils.pageSize - pageHeader.size()*4];
        pageDataCapacity = Utils.pageSize - pageHeader.size()*4;
        this.td = td;
    }

    public Page(byte[] pageInByte, TupleDesc td){
        this.td = td;
        pageHeader = decodeHeader(pageInByte);
        pageData = new byte[pageInByte.length - pageHeader.size()*4];
        System.arraycopy(pageInByte,pageHeader.size()*4, pageData,0, pageData.length);
    }


    public void deleteTuple(int id){
        int pageSize = pageSize();
        int startByte = id * td.tupleSize();
        int endByte = startByte + td.tupleSize();
//        endByte has to be <= PageHeaderEnum.SIZE
        if(endByte < pageSize){
//            need to rearrange the tuple that comes after
            byte[] temp = new byte[pageSize - endByte];
            System.arraycopy(pageData, endByte, temp, 0, temp.length);
            System.arraycopy(temp, 0, pageData, startByte, temp.length);
            pageHeader.put(PageHeaderEnum.SIZE, pageSize() - td.tupleSize());
        }else if(endByte == pageSize){
//            its the last tuple so no need to rearrange
            pageHeader.put(PageHeaderEnum.SIZE, pageSize() - td.tupleSize());
        }else {
            throw new RuntimeException("accessing record "+ endByte +" greater than pagesize "+ pageSize);
        }
    }

    public TupleDesc getTupleDesc(){
        return this.td;
    }


    public void insertTuple(Tuple tuple){
        int size = pageHeader.get(PageHeaderEnum.SIZE);
        if(tuple.size() + size < pageDataCapacity){
            System.arraycopy(tuple.getBytes(),0, pageData, size, tuple.size());
            pageHeader.put(PageHeaderEnum.SIZE, tuple.size() + size);
        }else {
            throw new ArrayIndexOutOfBoundsException("cant insert any more tuples in the page");
        }
    }


    public void update(int id, Tuple tuple){
        int offset = id * td.tupleSize();
        System.arraycopy(tuple.getBytes(), 0, pageData, offset, td.tupleSize());
    }


    /*
    * used for recovery
    * replaces bytes for the offset
    * */
    public void replaceTuple(int id, Tuple tuple, TupleDesc td) {
        int offset = id * td.tupleSize();
        byte[] prevByte = new byte[td.tupleSize()];
        System.arraycopy(pageData, offset, prevByte, 0, td.tupleSize());

//        handling insert/delete/update
        if (byteArrEmpty(prevByte) && byteArrEmpty(tuple.getBytes())) {
//            todo update vs insert
            System.arraycopy(tuple.getBytes(), 0, pageData, offset, td.tupleSize());
        } else if (byteArrEmpty(prevByte)) {
//            insert
            int size = pageSize();
            pageHeader.put(PageHeaderEnum.SIZE, size + td.tupleSize());
            System.arraycopy(tuple.getBytes(), 0, pageData, offset, td.tupleSize());
        } else if (byteArrEmpty(tuple.getBytes())) {
//            delete
            int size = pageSize();
            pageHeader.put(PageHeaderEnum.SIZE, size - td.tupleSize());
            System.arraycopy(tuple.getBytes(), 0, pageData, offset, td.tupleSize());
        } else {
            System.arraycopy(tuple.getBytes(), 0, pageData, offset, td.tupleSize());
        }
    }


    private boolean byteArrEmpty(byte[] bytes){
        for (int i=0;i< bytes.length; i++){
            if (bytes[i]!=0){
                return false;
            }
        }
        return true;
    }

    private byte[] headerToByte(){
        int headerSize = pageHeader.size();
        byte[] headerByte = new byte[headerSize*4];
        int index = 0;
        for(Map.Entry<PageHeaderEnum, Integer> entry : pageHeader.entrySet()){
            byte[] temp = Utils.intToByte(entry.getValue());
            System.arraycopy(temp,0,headerByte,index*4,temp.length);
            index++;
        }
        return headerByte;
    }

    /*
    * items in the header should be encoded and decoded
    * in the same order
    *
    * */
    private TreeMap<PageHeaderEnum, Integer>decodeHeader(byte[] page){
        TreeMap<PageHeaderEnum,Integer> headerMap = new TreeMap();
        headerMap.put(PageHeaderEnum.LSN, 0);
        headerMap.put(PageHeaderEnum.ID, 0);
        headerMap.put(PageHeaderEnum.SIZE, 0);

        byte[] temp = new byte[4];
        int index = 0;
        for(Map.Entry<PageHeaderEnum, Integer>header: headerMap.entrySet()){
            System.arraycopy(page,index, temp,0,temp.length);
            headerMap.put(header.getKey(), Utils.byteToInt(temp));
        }
        return headerMap;
    }

    public void setLsn(int lsn){
        pageHeader.put(PageHeaderEnum.LSN, lsn);
    }


    public int getHeader(PageHeaderEnum headerName){
//        check if the key exists and throw exception
         return this.pageHeader.get(headerName);
    }


    public int pageSize(){
        return getHeader(PageHeaderEnum.SIZE);
    }

    public int pageID(){
        return getHeader(PageHeaderEnum.ID);
    }



    public byte[] getPageData(){
        byte[] pageInBytes = new byte[Utils.pageSize];
        byte[] headerInBytes = headerToByte();
        System.arraycopy(headerInBytes, 0,pageInBytes,0,pageHeader.size()*4) ;
        System.arraycopy(pageData,0,pageInBytes,pageHeader.size()*4,pageData.length);
        return pageInBytes;
    }

}