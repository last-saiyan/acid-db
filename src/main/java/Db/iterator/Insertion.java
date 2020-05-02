package Db.iterator;

import Db.bufferManager.Manager;
import Db.catalog.Tuple;

public class Insertion extends Operator {

    private Tuple data;
    private DbIterator child;
    private Manager bfPool;

    public Insertion(DbIterator child, Manager bfPool){
        this.child = child;
        this.bfPool = bfPool;
    }


    @Override
    public void open() {
        child.open();
    }

    @Override
    public void close() {

    }

    @Override
    protected Tuple fetchNext() {
        if(child.hasNext()){
            Tuple tuple = child.next();
            bfPool.insertTuple(tuple);
            return tuple;
        }
        return null;
    }
}
