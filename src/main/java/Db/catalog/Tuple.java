package Db.catalog;

import Db.Acid;
import Db.Utils;

import java.util.*;

public class Tuple {

    private HashMap<String, Value> fieldValuesMap;
    private byte[] byteTuple;
    private TupleDesc tupleDesc;
    public byte[] getBytes(){
        return byteTuple;
    }


    public Tuple(byte[] byteTuple, TupleDesc tDesc){
        this.byteTuple = byteTuple;
        this.fieldValuesMap = decodeTupleToMap(tDesc, byteTuple);
        this.tupleDesc = tDesc;
    }


    public Tuple(HashMap<String, Value> values){
        this.fieldValuesMap = values;
        this.byteTuple = encodeTupleToByte(values);
        this.tupleDesc = Acid.getDatabase().tupleDesc;
    }


    public int size(){
        return byteTuple.length;
    }

    public HashMap<String, Value> getMapValue(){
        return fieldValuesMap;
    }

    private byte[] encodeTupleToByte(HashMap<String, Value> values){
        int size = 0;
        for(Map.Entry<String, Value> entry: values.entrySet()){
            size = size + entry.getValue().size;
        }
        byte [] byteTuple = new byte[size];
        int pos = 0;

        for(Map.Entry<String, Value> entry: values.entrySet()){
            byte[] temp =  entry.getValue().getValue();
            System.arraycopy(temp,0,byteTuple, pos, temp.length);
           pos = pos + temp.length;
        }
        return byteTuple;
    }



    private HashMap<String, Value> decodeTupleToMap(TupleDesc tDesc, byte[] byteTuple) {
        Iterator<Field> iter = tDesc.open();
        fieldValuesMap = new HashMap();
        Field tempField;
        byte[] tempByte ;
        int index = 0;
        while (iter.hasNext()){
            tempField = iter.next();
            tempByte = new byte[tempField.getSize()];

            System.arraycopy(byteTuple,index,tempByte,0,tempByte.length);
            Value tempValue   = ValueFactory.getValue(tempField.typesEnum, tempByte);

            fieldValuesMap.put(tempField.fieldName, tempValue);

            index = index + tempField.getSize();
        }
        return fieldValuesMap;
    }



    public byte[] getValue(String fieldName){
        if(fieldValuesMap.containsKey(fieldName)){
            return fieldValuesMap.get(fieldName).getValue();
        }else {
            return null;
        }
    }


    public String toString(){
        String tupleString = "";

        ArrayList<Field> fieldListList = tupleDesc.getFieldList();
        fieldListList.sort(new Comparator<Field>() {
            @Override
            public int compare(Field o1, Field o2) {
                return o1.id - o2.id;
            }
        });
        for(int i=0; i<fieldListList.size(); i++){
            Field field = fieldListList.get(i);
            tupleString += field.fieldName + " - ";
            
            tupleString += fieldValuesMap.get(field.fieldName).toString();

            tupleString +=  " ; ";
        }
        return tupleString + "\n";
    }


}
