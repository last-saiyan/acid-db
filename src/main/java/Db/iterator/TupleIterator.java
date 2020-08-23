package Db.iterator;


import Db.diskManager.Page;
import Db.catalog.Tuple;
import Db.catalog.TupleDesc;
import Db.query.predicate.Predicate;

import java.io.IOException;


public class TupleIterator implements DbIterator {

    private Page page;
    private int tupleIndex;
    private TupleDesc tDesc;
    private HeapFileIterator pageIterator;

    private Predicate predicate;

    public TupleIterator(HeapFileIterator pageIterator, Predicate predicate){
        this.pageIterator = pageIterator;
        this.predicate = predicate;
    }


    @Override
    public void open(){
        pageIterator.open();
        tupleIndex = 0;
    }



    public void delete(){
        page.deleteTuple(tupleIndex);
    }


    public void insert(Tuple tuple){
        page.insertTuple(tuple);
    }


    /*
    *
    * the tuple is calculated by merging the current value
    * with the updated value
    * */
    public void update( Tuple tuple){
        delete();
        insert(tuple);
    }



    private Tuple filterTuple() throws IOException, InterruptedException {
        Tuple tempTuple = nextTuple();

        while (tempTuple != null){
            if (predicate == null){
                return tempTuple;
            }
            if(predicate.evaluate(tempTuple).finalValue){
                return tempTuple;
            }
            tempTuple = nextTuple();
        }
        return tempTuple;
    }



    private Tuple nextTuple() throws IOException, InterruptedException {
        if (page == null){
            page = pageIterator.next();
            if (page == null){
                return null;
            }
            tDesc = page.getTupleDesc();
            tupleIndex = 0;
        }

//        todo use index for size in page, modify page.java
        int pageSize = page.pageSize();
        int offset = tupleIndex*tDesc.tupleSize();
        if(offset < pageSize){
            byte[] tupleByte = new byte[tDesc.tupleSize()];
            System.arraycopy(page.pageData,offset,tupleByte,0,tDesc.tupleSize());
            tupleIndex++;
            return new Tuple(tupleByte, tDesc);
        } else {
            page = null;
            return nextTuple();
        }
    }



    @Override
    public Tuple next() throws IOException, InterruptedException {
        return filterTuple();
    }

}
