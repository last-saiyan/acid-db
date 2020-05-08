package Db.catalog;

public abstract class Value<variableType> {
//    research about factory design pattern

    public final TypesEnum type;
    public final int size;
    private byte[] value;

    public Value(TypesEnum type, int size, byte[]value){
        this.size = size;
        this.type = type;
        this.value = value;
    }

    public byte[] getValue(){
        return value;
    }

    public abstract variableType getCastValue();


    public abstract String toString();


}
