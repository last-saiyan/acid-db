package Db.iterator;


import Db.Tx.Transaction;
import Db.bufferManager.Manager;
import Db.catalog.Tuple;

import java.io.IOException;

public class Insertion implements DbIterator {

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
    public Tuple next() throws IOException, InterruptedException {
        Tuple tuple = child.next();
        if (tuple == null){
            return null;
        }
        bfPool.insertTuple(tuple, tx);
        return tuple;
    }
}
