package Db.iterator;

import Db.Tx.Transaction;
import Db.bufferManager.Manager;
import Db.catalog.Tuple;

public class Insertion extends Operator {

    private Tuple data;
    private DbIterator child;
    private Manager bfPool;
    private Transaction tx;

    public Insertion(DbIterator child, Manager bfPool, Transaction tx){
        this.child = child;
        this.bfPool = bfPool;
        this.tx = tx;
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
            try {
                bfPool.insertTuple(tuple, tx);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            return tuple;
        }
        return null;
    }
}
