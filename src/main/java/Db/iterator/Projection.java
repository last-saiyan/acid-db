package Db.iterator;

import Db.catalog.Field;
import Db.catalog.Tuple;

import java.util.ArrayList;

public class Projection extends Operator {
    private DbIterator child;
    private ArrayList<Field> outputFieldList;

    public Projection(DbIterator child, ArrayList<Field> outputFieldList){
        this.child = child;
        this.outputFieldList = outputFieldList;
    }

    @Override
    public void open() {

    }

    @Override
    public void close() {

    }


    @Override
    protected Tuple fetchNext() {
        if(child.hasNext()){
            Tuple tuple = child.next();

//            create new tuple with values corresponding to output fieldList
//            return it
            Tuple outputTuple = new Tuple(null);

            return outputTuple;

        }

        return null;
    }
}
