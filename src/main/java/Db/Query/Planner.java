package Db.query;

import Db.Acid;
import Db.Tx.Permission;
import Db.Tx.Transaction;
import Db.bufferManager.Manager;
import Db.diskManager.DiskManager;
import Db.iterator.*;
import Db.query.predicate.Predicate;

public class Planner {
    private QueryMapper query;
    private Predicate predicate;
    private Manager bfManager;
    private DiskManager dskMgr;
    private Transaction tx;

    public Planner(QueryMapper query, Predicate predicate, Transaction tx){
        this.query = query;
        this.predicate = predicate;
        bfManager = Acid.getDatabase().bufferPoolManager;
        dskMgr = Acid.getDatabase().diskManager;
        this.tx = tx;
    }

    public DbIterator getplan(){

        switch (query.type){
            case "select":
                return selectIterator(query, predicate);
            case "update":
                return updateIterator(query, predicate);
            case "insert":
                return insertIterator(query);
            case "delete":
                return deleteIterator(query, predicate);
        }

        return null;
    }

    private DbIterator selectIterator(QueryMapper query, Predicate predicate){
//      get these from a static class

        HeapFileIterator pageIter = new HeapFileIterator(bfManager, dskMgr, tx, Permission.SHARED);

        DbIterator iter = new TupleIterator(pageIter, predicate);

        return new Projection(iter, query.columns);
    }


    private DbIterator updateIterator(QueryMapper query, Predicate predicate){
//      get these from a static class
        HeapFileIterator pageIter = new HeapFileIterator(bfManager, dskMgr, tx, Permission.EXCLUSIVE);
        DbIterator iter = new TupleIterator(pageIter, predicate);

        return new Update(iter, query.values, Acid.getDatabase().tupleDesc);
    }


    private DbIterator insertIterator(QueryMapper query){

        DbIterator iter = new InsertIterator(query.values, Acid.getDatabase().tupleDesc);

        return new Insertion(iter, bfManager, tx);

    }


    private DbIterator deleteIterator(QueryMapper query, Predicate predicate){

//      get these from a static class

        HeapFileIterator pageIter = new HeapFileIterator(bfManager, dskMgr, tx, Permission.EXCLUSIVE);
        DbIterator iter = new TupleIterator(pageIter, predicate);
        return new Deletion(iter);

    }


}


