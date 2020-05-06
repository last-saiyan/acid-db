package Db.catalog;

import Db.Utils;


public class IntValue extends Value <Integer> {



    public IntValue(int value){
        super(TypesEnum.INTEGER, 4, Utils.intToByte(value));
    }

    public IntValue(String value){
        super(TypesEnum.INTEGER, 4, Utils.intToByte(Integer.parseInt(value)));
    }

    public IntValue(byte[] value){
        super(TypesEnum.INTEGER, 4, value);
    }


    @Override
    public Integer getCastValue() {
        byte[] byteVal = getValue();
        return new Integer(Utils.byteToInt(byteVal));
    }


    @Override
    public String toString() {
        Integer integer = getCastValue();
        return integer.toString();
    }


}
