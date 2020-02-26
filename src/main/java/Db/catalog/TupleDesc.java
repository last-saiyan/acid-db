package Db.catalog;


/*
*
* describe the fields of a tuple
*
* */

import java.util.ArrayList;
import java.util.Iterator;

public class TupleDesc {
    private ArrayList<Field> fieldList;


    public TupleDesc(ArrayList<Field> fieldList ){
        this.fieldList = fieldList;
    }

    public int tupleSize(){
        int size = 0;
        for(int i=0;i<fieldList.size();i++){
            size = size + fieldList.get(i).getSize();
        }
        return size;
    }

    public Iterator<Field> open(){
        return fieldList.iterator();
    }



}
