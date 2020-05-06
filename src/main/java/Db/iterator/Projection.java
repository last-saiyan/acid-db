package Db.iterator;

import Db.catalog.Field;
import Db.catalog.Tuple;
import Db.catalog.TupleDesc;
import Db.catalog.Value;

import java.util.ArrayList;
import java.util.HashMap;

public class Projection extends Operator {
    private DbIterator child;
    private ArrayList<String> outputFieldList;

    public Projection(DbIterator child, ArrayList<String> outputFieldList){

        this.child = child;

        this.outputFieldList = outputFieldList;
    }

    @Override
    public void open() {

    }

    @Override
    public void close() {

    }


/*
*
* create new tuple with values corresponding to output fieldList
* returns the tuple
* */

    @Override
    protected Tuple fetchNext() {
        if(child.hasNext()){
            Tuple tuple = child.next();

            HashMap<String, Value> tupleMap = tuple.getMapValue();

            HashMap<String, Value> outupleMap = new HashMap();

            for(int i=0; i<outputFieldList.size(); i++){
                String fieldName = outputFieldList.get(i);
                outupleMap.put(fieldName, tupleMap.get(fieldName));
            }
            Tuple outputTuple = new Tuple(outupleMap);
            return outputTuple;

        }

        return null;
    }
}
