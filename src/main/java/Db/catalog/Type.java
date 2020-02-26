package Db.catalog;

public abstract class Type {

    public final String type;
    public final int size;
    private byte[] value;


    public Type(String type, int size, byte[]value){
        this.size = size;
        this.type = type;
        this.value = value;
    }

//    abstract public Object returnValue();

    public byte[] getValue(){
        return value;
    }

}
