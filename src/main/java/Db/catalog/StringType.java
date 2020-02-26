package Db.catalog;

import Db.Utils;

import java.nio.charset.StandardCharsets;

public class StringType extends Type {


    private String value;
    private byte[] strByte;
    public StringType(String strValue, int size){
        super("STR",size, Utils.stringToByte(strValue,size));

//        throw exception if len is larger

    }


    public StringType(byte[] value){
        super("STR", value.length, value);

    }

    public String returnValue(){
        return this.value;
    }

}
