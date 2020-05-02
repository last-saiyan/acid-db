package Db.Query;

import Db.bufferManager.Manager;
import Db.catalog.TupleDesc;
import Db.iterator.*;

public class Planner {
    private QueryMapper query;
    private Predicate predicate;

    public Planner(QueryMapper query, Predicate predicate){
        this.query = query;
        this.predicate = predicate;
    }

    public DbIterator getplan(){

        switch (query.type){
            case "select":
                return selectIterator(query, predicate);
            case "update":
                return updateIterator(query, predicate);
            case "create":
                return insertIterator(query);
            case "delete":
                return deleteIterator(query, predicate);
        }

        return null;
    }

    private DbIterator selectIterator(QueryMapper query, Predicate predicate){
        Manager bfManager = null;
//      get these from a static class

        HeapFileIterator pageIter = new HeapFileIterator(bfManager);

        DbIterator iter = new TupleIterator(pageIter, predicate);

        return new Projection(iter, query.columns);
    }


    private DbIterator updateIterator(QueryMapper query, Predicate predicate){
        Manager bfManager = null;
//      get these from a static class
        HeapFileIterator pageIter = new HeapFileIterator(bfManager);
        DbIterator iter = new TupleIterator(pageIter, predicate);
//        correct this later implement new operator
        return null;

    }
    private DbIterator insertIterator(QueryMapper query){
        Manager bfManager = null;

        DbIterator iter = new InsertIterator(query.values);
        return new Insertion(iter, bfManager);

    }
    private DbIterator deleteIterator(QueryMapper query, Predicate predicate){

        Manager bfManager = null;
//      get these from a static class

        HeapFileIterator pageIter = new HeapFileIterator(bfManager);
        DbIterator iter = new TupleIterator(pageIter, predicate);
        return new Deletion(iter);

    }


}


