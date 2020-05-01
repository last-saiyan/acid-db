package Db.iterator;

import Db.Tuples.Tuple;

public class Deletion extends Operator {
    private DbIterator child;

    public Deletion(DbIterator child){
        this.child = child;

    }

    @Override
    public void open() {
        child.open();
    }

    @Override
    public void close() {
        child.close();
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
