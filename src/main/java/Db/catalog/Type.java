package Db.catalog;

public abstract class Type {

    public final String type;
    public final int size;


    public Type(String type, int size){
        this.size = size;
        this.type = type;
    }

//    abstract public Object returnValue();

    abstract public byte[] toByteArray();

}
