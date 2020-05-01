package Db.iterator;

import Db.Tuples.Tuple;

public class Insertion extends Operator {

    private Tuple data;
    private DbIterator child;

    public Insertion(DbIterator child, Tuple data){
        this.child = child;
        this.data = data;
    }


    @Override
    public void open() {
        child.open();
    }

    @Override
    public void close() {

    }

    @Override
    public Tuple next() {


        return null;
    }

    @Override
    public boolean hasNext() {
        return (child.hasNext());
    }
}
