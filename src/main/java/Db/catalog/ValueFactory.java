package Db.catalog;

import java.rmi.activation.UnknownObjectException;

public class ValueFactory {
    public static Value getValue(TypesEnum type, int size, String value) throws UnknownObjectException {


        if(type == TypesEnum.STRING){
            return new StringValue(value, size);
        }else if(type == TypesEnum.INTEGER){
            return new IntValue(value);
        }else {
            throw new UnknownObjectException("the type - "+type.toString() + "is not handled" );
        }
    }
}
