package Db;

import java.util.HashMap;

public class Page implements Utils {

    HashMap<String, Integer> pageHeader;


    public Page(){

        byte[] page = new byte[Utils.pageSize];


        StringBuilder page = new StringBuilder(Utils.pageSize);



    }
    public Page(byte[] pageInByte){


    }

    public byte[] convertPageToBytes(){

        return null;
    }


    private StringBuilder s ;



    public Page getPage(int id){


        return null;
    }





}
