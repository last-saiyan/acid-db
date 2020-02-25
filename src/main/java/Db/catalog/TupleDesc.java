package Db.catalog;


/*
*
*
* */

import java.util.ArrayList;

public class TupleDesc {
    private ArrayList<Field> fieldList;



    public int tupleSize(){
        int size = 0;
        for(int i=0;i<fieldList.size();i++){
            size = size + fieldList.get(i).getSize();
        }
        return size;
    }



}
