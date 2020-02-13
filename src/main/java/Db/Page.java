package Db;

import java.util.HashMap;
import java.util.Map;

public class Page implements Utils {

    public HashMap<String, Integer> pageHeader = new HashMap();
    public byte[] pageData;


    public Page(int id){
        pageHeader.put("id",id);
//        pageHeader.put("size",30);
//        pageHeader.put("size",31);
        pageHeader.put("size",29);
        pageData = new byte[Utils.pageSize - pageHeader.size()*4];
    }
    private Page(){

    }

    public boolean writePageToDisk(){
        DiskManager dm = null;
        try {
            dm = new DiskManager();
        } catch (Exception e) {
            e.printStackTrace();
        }
        byte[] pageInBytes = new byte[Utils.pageSize];
        byte[] headerInBytes = headerToByte();
        System.arraycopy(headerInBytes, 0,pageInBytes,0,pageHeader.size()*4) ;
        System.arraycopy(pageData,0,pageInBytes,pageHeader.size()*4,pageData.length);
        dm.writePage(pageHeader.get("id"), pageInBytes);
        return false;
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
//       pageHeader.forEach((k,v)->{
//            byte[] temp = Utils.intToByte(v);
//            System.arraycopy(temp,0,headerByte,index*4,temp.length);
//            index = index+1;
//        });
    }

    public Page(byte[] pageInByte){
        // parse header
        pageHeader = decodeHeader(pageInByte);

        pageData = new byte[pageInByte.length - pageHeader.size()*4];
        System.arraycopy(pageInByte,8, pageData,0, pageData.length);

//        pageData = pageData;
    }

    private HashMap<String, Integer>decodeHeader(byte[] page){

        HashMap<String,Integer> header = new HashMap();
        byte[] size = new byte[4], id = new byte[4];

        System.arraycopy(page,0, size,0,size.length);
        System.arraycopy(page,size.length, id,0,id.length);

        header.put("id", Utils.byteToInt(id));
        header.put("size", Utils.byteToInt(size));
        return header;
    }

    public byte[] convertPageToBytes(){

        return null;
    }


    public Page getPage(int id){
// diskmanager or buffermanager

        return null;
    }

}