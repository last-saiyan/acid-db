package Db.catalog;

import java.nio.ByteBuffer;

public class IntType extends Type {


    private int value;

    public IntType(int value){
        super("INT");
        this.value = value;
    }
    public IntType(String value){
        super("INT");
        this.value = Integer.parseInt(value);
    }

    public byte[] getByteArray(){
        return ByteBuffer.allocate(4).putInt(value).array();
    }

    @Override
    public byte[] toByteArray(){
        return ByteBuffer.allocate(4).putInt(value).array();
    }

    public int returnValue(){
        return this.value;
    }

}
