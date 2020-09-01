package Db.iterator;

import Db.catalog.*;
import Db.query.ColValue;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;


public class Update implements  DbIterator{
    private DbIterator child;
    private ArrayList<ColValue> values;
    private TupleDesc td;

    public Update(DbIterator child, ArrayList<ColValue> values, TupleDesc td){
        this.child = child;
        this.values = values;
        this.td = td;
    }

    @Override
    public void open() {
        child.open();
    }

    @Override
    public Tuple next() throws IOException, InterruptedException {
        Tuple tuple = child.next();
        if (tuple == null){
            return null;
        }

        HashMap<String, Value> tupleMapValue = tuple.getMapValue();
        HashMap<String, Field> fieldMap = td.getFieldMap();
        Value tempValue;
        for(int i=0 ; i<values.size() ; i++ ){
            ColValue value = values.get(i);
            TypesEnum type = fieldMap.get(value.colName).typesEnum;
            int size = fieldMap.get(value.colName).size;
            tempValue = ValueFactory.getValue(type, size, value.value);
            tupleMapValue.put(value.colName, tempValue);
        }
        tuple = new Tuple(tupleMapValue);

        ((TupleIterator) child).update(tuple);
        return tuple;
    }
}
