package Db.catalog;


/*
*
* describe the fields of a tuple
*
* */

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

public class TupleDesc {
    private ArrayList<Field> fieldList;
    private HashMap<String, Field> fieldHashMap;


    public TupleDesc(ArrayList<Field> fieldList ){
        this.fieldList = fieldList;
        fieldHashMap = new HashMap();

        for(int i=0;i<fieldList.size() ; i++){
            fieldHashMap.put(fieldList.get(i).fieldName, fieldList.get(i));
        }
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

    public ArrayList<Field> getFieldList(){
        return fieldList;
    }

    public HashMap<String , Field> getFieldMap(){
        return fieldHashMap;
    }



}
