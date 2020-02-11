package Db;

import java.util.HashMap;

public class Page implements Utils {

    HashMap<String, Integer> pageHeader;


    public Page(int id){
        pageHeader.put("id",id);
        byte[] page = new byte[Utils.pageSize];
    }

    private byte[] headerToByte(){


        return null;
    }

    public Page(byte[] pageInByte){

    }

    public byte[] convertPageToBytes(){

        return null;
    }


    public Page getPage(int id){
// diskmanager

        return null;
    }

}
