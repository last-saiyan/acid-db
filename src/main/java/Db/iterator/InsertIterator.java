package Db.iterator;

import Db.Query.ColValue;
import Db.catalog.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

public class InsertIterator implements DbIterator {

    ArrayList<Tuple> tuples;
    Iterator<Tuple> iterator;

    public InsertIterator(ArrayList<ColValue> values, TupleDesc td){
        tuples = new ArrayList();
        HashMap<String, Value> tupleMap = new HashMap();
        HashMap<String, Field> fieldMap;
        Value tempValue;
        for (int i=0; i<values.size(); i++){
            ColValue colValue = values.get(i);
            fieldMap = td.getFieldMap();
//            rewrite this using some builder pattern
            if(fieldMap.get(colValue.colName).typesEnum == TypesEnum.STRING){
                tempValue = new StringValue(colValue.value, fieldMap.get(colValue.colName).size);
                tupleMap.put(colValue.colName, tempValue);
            }else if(fieldMap.get(colValue.colName).typesEnum == TypesEnum.INTEGER){
                tempValue = new IntValue(colValue.value);
                tupleMap.put(colValue.colName, tempValue);
            }
        }
        Tuple temp = new Tuple(tupleMap);
        tuples.add(temp);
    }
    @Override
    public void open() {
        iterator = tuples.iterator();
    }

    @Override
    public void close() {

    }

    @Override
    public Tuple next() {
        return iterator.next();
    }

    @Override
    public boolean hasNext() {
        return iterator.hasNext();
    }
}
