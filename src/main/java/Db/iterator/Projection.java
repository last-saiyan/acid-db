package Db.iterator;


import Db.catalog.Tuple;
import Db.catalog.Value;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

public class Projection implements DbIterator {
    private DbIterator child;
    private ArrayList<String> outputFieldList;

    public Projection(DbIterator child, ArrayList<String> outputFieldList){
        this.child = child;
        this.outputFieldList = outputFieldList;
    }


    @Override
    public void open() {
        child.open();
    }


    /*
     *
     * create new tuple with values corresponding to output fieldList
     * returns the tuple
     * */

    @Override
    public Tuple next() throws IOException, InterruptedException {
        Tuple tuple = child.next();
        if (tuple == null){
            return null;
        }

        HashMap<String, Value> tupleMap = tuple.getMapValue();
        HashMap<String, Value> outTupleMap = new HashMap();

        for(int i=0; i<outputFieldList.size(); i++){
            String fieldName = outputFieldList.get(i);
            outTupleMap.put(fieldName, tupleMap.get(fieldName));
        }
        Tuple outputTuple = new Tuple(outTupleMap);
        return outputTuple;
    }


}
