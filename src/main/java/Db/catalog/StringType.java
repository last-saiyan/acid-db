package Db.catalog;

import Db.Utils;

import java.nio.charset.StandardCharsets;

public class StringType extends Type {


    private String value;
    private byte[] strByte;
    public StringType(String strValue, int size){
        super("STR",size);
        this.value = value;
//        throw exception if len is larger
        this.strByte = Utils.stringToByte(strValue,size);
    }

    @Override
    public byte[] toByteArray(){
        return strByte;
    }
    public String returnValue(){
        return this.value;
    }

}
