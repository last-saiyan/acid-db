package Db.catalog;

public abstract class Type {

    public final String type;



    public Type(String type){
        this.type = type;
    }

//    abstract public Object returnValue();

    abstract public byte[] toByteArray();

}
