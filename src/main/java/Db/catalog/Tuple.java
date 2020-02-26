package Db.catalog;

import Db.Utils;

import java.util.HashMap;
import java.util.Iterator;

public class Tuple {

    HashMap<String, Type> values;

    public Tuple(byte[] byteTuple, TupleDesc tDesc){

        Iterator<Field> iter = tDesc.open();
        Field tempField;
        byte[] tempByte ;
        int index = 0;
        while (iter.hasNext()){
            tempField = iter.next();

            tempByte = new byte[tempField.getSize()];

//
            if(tempField.type.equals("STR")){
                System.arraycopy(byteTuple,index,tempByte,0,tempByte.length);
                values.put(tempField.fieldName, new StringType(tempByte));

            }else if(tempField.type.equals("INT")){

                System.arraycopy(byteTuple,index,tempByte,0,tempByte.length);
                values.put(tempField.fieldName, new IntType(tempByte));
            }

            index = index + tempField.getSize();
        }

    }






}
