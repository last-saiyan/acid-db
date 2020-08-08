package Db.query;

import Db.catalog.*;
import Db.query.predicate.ExpressionNode;
import Db.query.predicate.Predicate;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class PredicateTest {


    @Mock
    Tuple tuple = mock(Tuple.class);




    TupleDesc getTD(){

        ArrayList<Field> fieldArrayList = new ArrayList<>();

        Field f1 = new Field("columnA", TypesEnum.INTEGER, 4);
        fieldArrayList.add(f1);
        f1 = new Field("columnB", TypesEnum.INTEGER, 4);
        fieldArrayList.add(f1);

        TupleDesc td = new TupleDesc(fieldArrayList);

        return td;
    }


    @Test
    void testPredicate(){

        HashMap<String, Value> tupleMap = new HashMap<>();
        Value<Integer> val1 = new IntValue(10);
        tupleMap.put("columnA", val1);
        Value<Integer> val2 = new IntValue(20);
        tupleMap.put("columnB", val2);

        TupleDesc td = getTD();

        when(tuple.getMapValue()).thenReturn(tupleMap);


        Predicate exp = new Predicate("columnA+columnB=30", td);
        ExpressionNode node = exp.evaluate(tuple);

        Predicate exp2 = new Predicate("columnA+columnB=20", td);
        ExpressionNode node2 = exp2.evaluate(tuple);


        Assertions.assertEquals(node.finalValue , true);

        Assertions.assertEquals(node2.finalValue , false);



    }



}
