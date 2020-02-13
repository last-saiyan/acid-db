package Db.Pages;

import Db.Utils;

public class FixedPage {


    public StringBuilder page;
    public int pageSize;
    public int headerSize;

    public FixedPage(){

        page = new StringBuilder(Utils.pageSize);

        header();

    }


    public void header(){
        int headerLength = 20;
        int indexPos = Utils.pageSize;
        int tuplePos = headerLength;
    }



    public boolean addTuple(String s){

        return false;
    }

    public boolean removeTuple(int id){

        return false;
    }

    public boolean updateTuple(int id){

        return false;
    }

    public String getTuple(int id){

        return "";
    }

}
