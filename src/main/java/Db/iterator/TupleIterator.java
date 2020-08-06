package Db.iterator;

import Db.Acid;
import Db.Tx.Transaction;
import Db.Utils;
import Db.diskManager.Page;
import Db.catalog.Tuple;
import Db.catalog.TupleDesc;
import Db.query.predicate.Predicate;


public class TupleIterator implements DbIterator {

    private Page page;
    private int tupleIndex;
    private TupleDesc tDesc;
    private HeapFileIterator pageIterator;

    public TupleIterator(HeapFileIterator pageIterator, Predicate predicate){
        this.pageIterator = pageIterator;
    }

    @Override
    public void open(){
        this.page = pageIterator.getNextPage();
        this.tDesc = page.getTupleDesc();
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

    /*
    * checks if index is less than page size
    * if no more tuples in current page
    * gets new page from pageIterator
    * */
    @Override
    public boolean hasNext(){

        int pageSize = page.pageSize();
        if( (tupleIndex*tDesc.tupleSize()) < pageSize){
            return true;
        }else{
            if(pageIterator.hasNext()){
                 page = pageIterator.getNextPage();
                return true;
            }else {
                return false;
            }
        }
    }


    @Override
    public Tuple next(){

        if(hasNext()){
            int offset = tupleIndex*tDesc.tupleSize();

            byte[] tupleByte = new byte[tDesc.tupleSize()];
            System.arraycopy(page.pageData,offset,tupleByte,0,tDesc.tupleSize());
            tupleIndex++;
            return new Tuple(tupleByte, tDesc);
        }else{
            throw new RuntimeException("no more tuples ");
//            throw exception
        }
    }

    @Override
    public void close(){
        tupleIndex = 0;
        pageIterator.close();
    }

}
