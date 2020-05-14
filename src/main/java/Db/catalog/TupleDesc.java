package Db.catalog;


/*
*
* describe the fields of a tuple
*
* */

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

public class TupleDesc implements Serializable {

    private HashMap<String, Field> fieldHashMap;
    private ArrayList<Field> fieldList;



    public TupleDesc(ArrayList<Field> fieldList ){
        this.fieldList = fieldList;
        fieldHashMap = new HashMap();



        for(int i=0;i<fieldList.size() ; i++){
            fieldHashMap.put(fieldList.get(i).fieldName, fieldList.get(i));
        }
    }

    public void serializeToDisk(String catalogFilePath) throws IOException {

        String filepath = catalogFilePath;
        FileOutputStream fileOut = new FileOutputStream(filepath);
        ObjectOutputStream objectOut = new ObjectOutputStream(fileOut);
        objectOut.writeObject(this);
        objectOut.close();
    }

    public static TupleDesc deSerializeFromDisk (String catalogFilePath) throws IOException, ClassNotFoundException {
        String filepath = catalogFilePath;
        FileInputStream fileIn = new FileInputStream(filepath);
        ObjectInputStream objectinputstream = new ObjectInputStream(fileIn);
        return (TupleDesc) objectinputstream.readObject();
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
