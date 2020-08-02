package Db.query.predicate;

import Db.catalog.*;
import Db.query.ExpressionLexer;
import Db.query.ExpressionParser;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CodePointCharStream;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.tree.ParseTree;

import java.util.HashMap;

public class Predicate {

    String predicateString;
    private ParseTree tree;
    private ExpressionVisitor visitor;


    public Predicate(String predicateString){
        this.predicateString = predicateString;
    }

    

    public ExpressionNode evaluate(Tuple tuple, TupleDesc td){

        createExpressionTree(td.getFieldMap(), tuple.getMapValue());

        return evaluateTree();

    }


    public void createExpressionTree(HashMap<String, Field> fieldMap, HashMap<String, Value> tupleMap){
        CodePointCharStream input = CharStreams.fromString(predicateString);
        ExpressionLexer lexer = new ExpressionLexer(input);
        CommonTokenStream tokenStream = new CommonTokenStream(lexer);
        ExpressionParser parser = new ExpressionParser(tokenStream);
        tree = parser.prog(); // check whats happening here
        visitor = new ExpressionVisitor(fieldMap, tupleMap);
    }


    ExpressionNode evaluateTree(){
        return visitor.visit(tree);
    }


}
