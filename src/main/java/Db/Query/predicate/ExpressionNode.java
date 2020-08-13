package Db.query.predicate;


import Db.catalog.TypesEnum;

/*
* node can be number string or a table field
*
* */
public class ExpressionNode extends Node {
    /*
    * todo add support for string
    * */

    public int intValue;
    public String fieldName;

    public TypesEnum type; // int, operator, finalValue, str
    public boolean finalValue; // final value of the predicate true or false

    public ExpressionNode(int intValue){
        this.type = TypesEnum.INTEGER;
        this.intValue = intValue;
    }

    public ExpressionNode(String stringValue , TypesEnum type){
        this.type = type;
        this.fieldName = stringValue;
    }

    public ExpressionNode(boolean finalValue){
        this.type = TypesEnum.BOOLEAN;
        this.finalValue = finalValue;
    }

}
