package Db.query;

import Db.Acid;
import Db.bufferManager.Manager;
import Db.iterator.*;

public class Planner {
    private QueryMapper query;
    private Predicate predicate;
    private Manager bfManager;
    public Planner(QueryMapper query, Predicate predicate){
        this.query = query;
        this.predicate = predicate;
        bfManager = Acid.getDatabase().bufferPoolManager;;
    }

    public DbIterator getplan(){

        switch (query.type){
            case "select":
                return selectIterator(query, null);
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

        HeapFileIterator pageIter = new HeapFileIterator(bfManager);

        DbIterator iter = new TupleIterator(pageIter, predicate);

        return new Projection(iter, query.columns);
    }


    private DbIterator updateIterator(QueryMapper query, Predicate predicate){
//      get these from a static class
        HeapFileIterator pageIter = new HeapFileIterator(bfManager);
        DbIterator iter = new TupleIterator(pageIter, predicate);
//        correct this later implement new operator
        return null;

    }
    private DbIterator insertIterator(QueryMapper query){

        DbIterator iter = new InsertIterator(query.values, Acid.getDatabase().tupleDesc);
        return new Insertion(iter, bfManager);

    }
    private DbIterator deleteIterator(QueryMapper query, Predicate predicate){

//      get these from a static class

        HeapFileIterator pageIter = new HeapFileIterator(bfManager);
        DbIterator iter = new TupleIterator(pageIter, predicate);
        return new Deletion(iter);

    }


}


