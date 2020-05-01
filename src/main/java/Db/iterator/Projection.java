package Db.iterator;

import Db.Tuples.Tuple;

public class Projection extends Operator {
    private DbIterator child;

    public Projection(DbIterator child){
        this.child = child;
    }

    @Override
    public void open() {

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
        return false;
    }
}
