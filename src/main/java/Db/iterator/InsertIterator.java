package Db.iterator;

import Db.query.ColValue;
import Db.catalog.*;

import java.rmi.activation.UnknownObjectException;
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
        Value tempValue = null;
        for (int i=0; i<values.size(); i++){
            ColValue colValue = values.get(i);
            fieldMap = td.getFieldMap();

            TypesEnum type = fieldMap.get(colValue.colName).typesEnum;
            int size = fieldMap.get(colValue.colName).size;
            try {
                tempValue = ValueFactory.getValue(type,size, colValue.value);
            } catch (UnknownObjectException e) {
//                need to abort the transaction
                e.printStackTrace();
            }
            tupleMap.put(colValue.colName, tempValue);
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
