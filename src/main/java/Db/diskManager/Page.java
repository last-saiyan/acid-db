package Db.diskManager;

import Db.Utils;
import Db.catalog.Tuple;
import Db.catalog.TupleDesc;

import java.util.HashMap;
import java.util.Map;

public class Page implements Utils {

    public HashMap<String, Integer> pageHeader = new HashMap();
    public byte[] pageData;
    public TupleDesc td;
    public int pageDataCapacity;

    /*
    * header should be encoded and decoded in the same order
    *
    * */

    public Page(int id, TupleDesc td){

        pageHeader.put("id",id);
        pageHeader.put("size",0);
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
//        test correctly here
//        improve logic here
        id = id * td.tupleSize();
        int size = pageHeader.get("size");
        if(id + td.tupleSize() > size){
            int src =  (id +1) * td.tupleSize();
            int dest = id * td.tupleSize() ;
            int len =  pageData.length - src;
            System.arraycopy(pageData,src,pageData,dest, len);
            pageHeader.put("size", size -  td.tupleSize());
        }else {
//
        }

    }

    public TupleDesc getTupleDesc(){
        return this.td;
    }


    public void insertTuple(Tuple tuple){
        int size = pageHeader.get("size");
        if(tuple.size() + size < pageDataCapacity){
            System.arraycopy(tuple.getBytes(),0, pageData, size, tuple.size());
            pageHeader.put("size", tuple.size() + size);
        }else {
            throw new ArrayIndexOutOfBoundsException("cant insert any more tuples in the page");
        }
    }


    public void update(int id, Tuple tuple){
        deleteTuple(id);
        insertTuple(tuple);
    }



    private byte[] headerToByte(){
        int headerSize = pageHeader.size();
        byte[] headerByte = new byte[headerSize*4];
        int index = 0;
        for(Map.Entry<String, Integer> entry : pageHeader.entrySet()){
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
    private HashMap<String, Integer>decodeHeader(byte[] page){

        HashMap<String,Integer> header = new HashMap();
        byte[] size = new byte[4], id = new byte[4];

        System.arraycopy(page,0, size,0,size.length);
        System.arraycopy(page,size.length, id,0,id.length);

        header.put("id", Utils.byteToInt(id));
        header.put("size", Utils.byteToInt(size));
        return header;
    }

    public int getHeader(String headerName){
//        check if the key exists and throw exception
         return this.pageHeader.get(headerName);
    }

    public int pageSize(){
        return getHeader("size");
    }



    public byte[] getPageData(){
        byte[] pageInBytes = new byte[Utils.pageSize];
        byte[] headerInBytes = headerToByte();
        System.arraycopy(headerInBytes, 0,pageInBytes,0,pageHeader.size()*4) ;
        System.arraycopy(pageData,0,pageInBytes,pageHeader.size()*4,pageData.length);
        return pageInBytes;
    }

}