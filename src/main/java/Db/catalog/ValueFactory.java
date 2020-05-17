package Db.catalog;


public class ValueFactory {
    public static Value getValue(TypesEnum type, int size, String value) {
        if(type == TypesEnum.STRING){
            return new StringValue(value, size);
        }else if(type == TypesEnum.INTEGER){
            return new IntValue(value);
        }else {
            throw new RuntimeException("the type - "+type.toString() + "is not handled" );
        }
    }


    public static Value getValue(TypesEnum type, byte[] value)  {
        if(type == TypesEnum.STRING){
            return new StringValue(value);
        }else if(type == TypesEnum.INTEGER){
            return new IntValue(value);
        }else {
            throw new RuntimeException("the type - "+type.toString() + "is not handled" );
        }
    }
}
