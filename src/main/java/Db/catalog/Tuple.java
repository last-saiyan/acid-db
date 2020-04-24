package Db.catalog;

import Db.Utils;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class Tuple {

    private HashMap<String, Type> values;
    private byte[] byteTuple;

    public byte[] getBytes(){
        return byteTuple;
    }

    public Tuple(byte[] byteTuple, TupleDesc tDesc){
        this.byteTuple = byteTuple;
        this.values = decodeTupleToMap(tDesc, byteTuple);
    }

    public Tuple(HashMap<String, Type> values){
        this.values = values;
        this.byteTuple = encodeTupleToByte(values);
    }


    public int size(){
        return byteTuple.length;
    }



    public byte[] encodeTupleToByte(HashMap<String, Type> values){
        int size = 0;
        for(Map.Entry<String, Type> entry: values.entrySet()){
            size = size + entry.getValue().size;
        }
        byte [] byteTuple = new byte[size];
        int pos = 0;
        for(Map.Entry<String, Type> entry: values.entrySet()){
            byte[] temp =  entry.getValue().getValue();
            System.arraycopy(temp,0,byteTuple, pos, temp.length);
           pos = pos + temp.length;
        }
        return byteTuple;
    }



    public HashMap<String, Type> decodeTupleToMap(TupleDesc tDesc, byte[] byteTuple) {
        Iterator<Field> iter = tDesc.open();
        Field tempField;
        byte[] tempByte ;
        int index = 0;
        while (iter.hasNext()){
            tempField = iter.next();
            tempByte = new byte[tempField.getSize()];
            if(tempField.type.equals("STR")){
                System.arraycopy(byteTuple,index,tempByte,0,tempByte.length);
                values.put(tempField.fieldName, new StringType(tempByte));
            }else if(tempField.type.equals("INT")){
                System.arraycopy(byteTuple,index,tempByte,0,tempByte.length);
                values.put(tempField.fieldName, new IntType(tempByte));
            }
            index = index + tempField.getSize();
        }
        return null;
    }



    public boolean find(){

        return false;
    }
}
