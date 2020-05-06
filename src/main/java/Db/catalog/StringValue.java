package Db.catalog;

import Db.Utils;


public class StringValue extends Value<String> {


    private String value;
    private byte[] strByte;
    public StringValue(String strValue, int size){
        super(TypesEnum.STRING, size, Utils.stringToByte(strValue,size));

//        throw exception if len is larger

    }


    public StringValue(byte[] value){
        super(TypesEnum.STRING, value.length, value);
    }

    @Override
    public String getCastValue(){
        byte[] byteVal = getValue();
        return Utils.byteToString(byteVal);
    }

    @Override
    public String toString() {
        return getCastValue();
    }

}
