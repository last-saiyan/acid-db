package Db.query.predicate;

import Db.catalog.Field;
import Db.catalog.TypesEnum;
import Db.catalog.Value;
import Db.query.ExpressionBaseVisitor;
import Db.query.ExpressionParser;
import org.antlr.v4.runtime.tree.ParseTree;

import java.util.HashMap;


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
            return new ExpressionNode(ctx.ID().getText(), "str");
        }else {
//            its a field
            if (fieldMap.containsKey(str)){
                Field field = fieldMap.get(str);

                if (field.typesEnum == TypesEnum.INTEGER){
                    Value<Integer> value = tupleMap.get(str);
                    return new ExpressionNode(value.getCastValue());
                }else {
                    Value<String> value = tupleMap.get(str);
                    return new ExpressionNode(value.getCastValue(), "str");
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

        if ( !( (left.type == right.type) && (left.type=="int") ) ){
//            handle other case
            throw new RuntimeException("cant multiply / divide strings");
        }

        if ( ctx.op.getType() == ExpressionParser.MUL ){
            ExpressionNode operatorNode = new ExpressionNode("*", "operator");
            operatorNode.left = left;
            operatorNode.right = right;
            return operatorNode;
        }
        // must be DIV
        ExpressionNode operatorNode = new ExpressionNode("/", "operator");
        operatorNode.left = left;
        operatorNode.right = right;
        return operatorNode;
    }

    /** expr op=('+'|'-') expr */
    @Override
    public ExpressionNode visitAddSub(ExpressionParser.AddSubContext ctx) {
        ExpressionNode left = visit(ctx.expr(0));  // get value of left subexpression
        ExpressionNode right = visit(ctx.expr(1)); // get value of right subexpression

        if ( !( (left.type == right.type) && (left.type=="int") ) ){
//            handle other case
            throw new RuntimeException("cant add / subtract strings");
        }

        System.out.println("+" + left.intValue + " - " + right.intValue  );
        if ( ctx.op.getType() == ExpressionParser.ADD ){
            ExpressionNode operatorNode = new ExpressionNode("+", "operator");
            operatorNode.left = left;
            operatorNode.right = right;
            return operatorNode;
        }
        // must be sub
        ExpressionNode operatorNode = new ExpressionNode("-", "operator");
        operatorNode.left = left;
        operatorNode.right = right;
        return operatorNode;
    }


    /** expr op=('='|'>='|'<='|'<'|'>') expr */
    @Override
    public ExpressionNode visitEqual(ExpressionParser.EqualContext ctx) {
        ExpressionNode left = visit(ctx.expr(0));  // get value of left subexpression
        ExpressionNode right = visit(ctx.expr(1)); // get value of right subexpression

        if ( ctx.op.getType() == ExpressionParser.EQ ){
            ExpressionNode operatorNode = new ExpressionNode("=", "operator");
            operatorNode.left = left;
            operatorNode.right = right;
            return operatorNode;
        }
        // must be DIV
        ExpressionNode operatorNode = new ExpressionNode("/", "operator");
        operatorNode.left = left;
        operatorNode.right = right;
        return operatorNode;
    }

    /** '(' expr ')' */
    @Override
    public ExpressionNode visitParens(ExpressionParser.ParensContext ctx) {
        return visit(ctx.expr()); // return child expr's value
    }
}