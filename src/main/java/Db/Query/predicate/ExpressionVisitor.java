package Db.query.predicate;

import Db.catalog.Field;
import Db.catalog.TypesEnum;
import Db.catalog.Value;
import Db.query.ExpressionBaseVisitor;
import Db.query.ExpressionParser;

import java.util.HashMap;
import java.util.Map;


public class ExpressionVisitor extends ExpressionBaseVisitor<ExpressionNode> {
    private HashMap<String, Field> fieldMap;
    private HashMap<String, Value> tupleMap;

    public ExpressionVisitor(HashMap<String, Field> fieldMap, HashMap<String, Value> tupleMap){
        this.fieldMap = fieldMap;
        this.tupleMap = tupleMap;
    }


    /** INT */
    @Override
    public ExpressionNode visitInt(ExpressionParser.IntContext ctx) {
        return new ExpressionNode(Integer.valueOf(ctx.INT().getText()));
    }


    /** ID */
    @Override
    public ExpressionNode visitId(ExpressionParser.IdContext ctx) {
        String str = ctx.ID().getText();
        if (str.charAt(0)=='\'' && str.charAt(str.length()-1)=='\''){
//            its a string not field
//            str = str.substring()
            return new ExpressionNode(ctx.ID().getText(), TypesEnum.STRING);
        }else {
//            its a field
            if (fieldMap.containsKey(str)){
                Field field = fieldMap.get(str);

                if (field.typesEnum == TypesEnum.INTEGER){
                    Value<Integer> value = tupleMap.get(str);
                    return new ExpressionNode(value.getCastValue());
                }else {
                    Value<String> value = tupleMap.get(str);
                    return new ExpressionNode(value.getCastValue(), TypesEnum.STRING);
                }
            }else {
                throw new RuntimeException(str + " is not a field");
            }
        }
    }


    /** expr op=('*'|'/') expr */
    @Override
    public ExpressionNode visitMulDiv(ExpressionParser.MulDivContext ctx) {
        ExpressionNode left = visit(ctx.expr(0));  // get value of left subexpression
        ExpressionNode right = visit(ctx.expr(1)); // get value of right subexpression

        if ( !( (left!=null && right!=null) &&  (left.type == right.type) && (left.type==TypesEnum.INTEGER) ) ){
//            handle other case
            throw new RuntimeException("cant multiply / divide strings");
        }

        if ( ctx.op.getType() == ExpressionParser.MUL ){
            return new ExpressionNode(left.intValue * right.intValue);
        }
        // must be DIV
        return new ExpressionNode(left.intValue / right.intValue);
    }



    /** expr op=('+'|'-') expr */
    @Override
    public ExpressionNode visitAddSub(ExpressionParser.AddSubContext ctx) {
        ExpressionNode left = visit(ctx.expr(0));  // get value of left subexpression
        ExpressionNode right = visit(ctx.expr(1)); // get value of right subexpression

        if ( !( (left!=null && right!=null) && (left.type == right.type) && (left.type==TypesEnum.INTEGER) ) ){
//            handle other case
            throw new RuntimeException("cant add / subtract strings");
        }
        if ( ctx.op.getType() == ExpressionParser.ADD ){
            return new ExpressionNode(left.intValue + right.intValue);
        }
        // must be sub
        return new ExpressionNode(left.intValue - right.intValue);

    }


    /** expr op=('='|'>='|'<='|'<'|'>') expr */
    @Override
    public ExpressionNode visitEqual(ExpressionParser.EqualContext ctx) {
        ExpressionNode left = visit(ctx.expr(0));  // get value of left subexpression
        ExpressionNode right = visit(ctx.expr(1)); // get value of right subexpression

        if ( !( (left!=null && right!=null) && (left.type == right.type) && (left.type==TypesEnum.INTEGER) ) ) {
            throw new RuntimeException("cant compare items");
        }

        if ( ctx.op.getType() == ExpressionParser.EQ ){
            boolean output = false;
            if (left.intValue == right.intValue){
                output = true;
            }
            return new ExpressionNode(output);
        }

        // todo implement other operators
        return null;
    }


    /** '(' expr ')' */
    @Override
    public ExpressionNode visitParens(ExpressionParser.ParensContext ctx) {
        return visit(ctx.expr()); // return child expr's value
    }
}