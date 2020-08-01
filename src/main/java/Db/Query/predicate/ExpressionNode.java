package Db.query.predicate;


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

    public String type; // int, operator, finalValue, str
    boolean finalValue; // final value of the predicate true or false

    public ExpressionNode(int intValue){
        this.type = "int";
        this.intValue = intValue;
    }

    public ExpressionNode(String stringValue , String type){
        this.type = type;
        this.fieldName = stringValue;
    }

    public ExpressionNode(boolean finalValue){
        this.type = "finalValue";
        this.finalValue = finalValue;
    }

}
