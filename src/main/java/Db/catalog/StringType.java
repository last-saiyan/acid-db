package Db.catalog;

import Db.Utils;

import java.nio.charset.StandardCharsets;

public class StringType extends Type {


    private String value;
    private int noBytes;
    private byte[] strByte;
    public StringType(String strValue, int noBytes){
        super("STR");
        this.value = value;
        this.noBytes = noBytes;
//        throw exception if len is larger
        this.strByte = Utils.stringToByte(strValue,noBytes);
    }

    @Override
    public byte[] toByteArray(){
        return strByte;
    }
    public String returnValue(){
        return this.value;
    }

}
