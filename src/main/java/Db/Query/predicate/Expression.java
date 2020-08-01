package Db.query.predicate;

import Db.catalog.Field;
import Db.catalog.Value;
import Db.query.ExpressionLexer;
import Db.query.ExpressionParser;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CodePointCharStream;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.tree.ParseTree;

import java.util.HashMap;

public class Expression {
    private ParseTree tree;
    private ExpressionVisitor visitor;

    public Expression(String expressionString, HashMap<String, Field> fieldMap, HashMap<String, Value> tupleMap) {
        CodePointCharStream input = CharStreams.fromString(expressionString);
        ExpressionLexer lexer = new ExpressionLexer(input);
        CommonTokenStream tokenStream = new CommonTokenStream(lexer);
        ExpressionParser parser = new ExpressionParser(tokenStream);
        tree = parser.prog(); // check whats happening here
        visitor = new ExpressionVisitor(fieldMap, tupleMap);

    }

    public ExpressionNode evaluate(){
        return visitor.visit(tree);
    }
}
