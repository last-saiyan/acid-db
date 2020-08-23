package Db.iterator;

import Db.catalog.Tuple;

import java.io.IOException;

public class Deletion implements DbIterator {
    private DbIterator child;

    public Deletion(DbIterator child){
        this.child = child;

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
        ((TupleIterator) child).delete();
        return tuple;
    }

}
