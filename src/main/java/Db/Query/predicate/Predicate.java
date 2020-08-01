package Db.query.predicate;

import Db.catalog.*;

import java.util.HashMap;

public class Predicate {

    Expression expression;
    

    public boolean evaluate(Tuple tuple, TupleDesc td,String predicateString){

        expression = new Expression(predicateString, td.getFieldMap(), tuple.getMapValue());

        HashMap<String , Field> fieldMap = td.getFieldMap();


        return false;
    }


}
