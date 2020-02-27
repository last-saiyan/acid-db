package Db.catalog;

import Db.Utils;

import java.nio.ByteBuffer;

public class IntType extends Type {


    private int value;

    public IntType(int value){
        super("INT", 4, Utils.intToByte(value));
    }
    public IntType(String value){
        super("INT", 4, Utils.intToByte(Integer.parseInt(value)));
    }
    public IntType(byte[] value){
        super("INT", 4, value);
    }


    public byte[] getByteArray(){
        return ByteBuffer.allocate(4).putInt(value).array();
    }


    public int returnValue(){
        return this.value;
    }

}
