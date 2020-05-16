package Db.Query;

import Db.catalog.Field;
import Db.catalog.TupleDesc;
import Db.catalog.TypesEnum;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.StringTokenizer;

public class PredicateTest {
    @Test
    void stringTokenizeTest() {
        String queryString = "{\"type\": \"create\",\"database\": \"dbname\",\"values\": [{\"colName\" :\"column1\",\"value\": \"STRING 30\"},{\"colName\":\"column2\",\"value\": \"INTEGER 4\"}]}\n";
        Query query = new Query(queryString, null);

        ArrayList<ColValue> colNameType = query.getQuery().values;
        ArrayList<Field> fieldList = new ArrayList();
        ColValue temp;
        for (int i = 0; i < colNameType.size(); i++) {
            temp = colNameType.get(i);
            String[] valSize = temp.value.split(" ");
            Field field = new Field(temp.colName, TypesEnum.valueOf(valSize[0]), Integer.parseInt(valSize[1]));
            fieldList.add(field);

        }



//        String a = "b+c=4";
//        String b = "b  +c=4";
//
//        System.out.println(a.substring(4,5));


        TupleDesc td = new TupleDesc(fieldList);
//
        Predicate pred = new Predicate("afff+beerw='5' ",td);

//pred.evaluatePredicate();


    }

}
