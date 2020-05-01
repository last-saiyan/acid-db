package Db.iterator;

import Db.diskManager.Page;
import Db.catalog.Tuple;
import Db.catalog.TupleDesc;


public class TupleIterator implements DbIterator {

    private Page page;
    private int tupleIndex;
    private TupleDesc tDesc;
    private HeapFileIterator pageIterator;

    public TupleIterator(HeapFileIterator pageIterator, TupleDesc tDesc){
        this.pageIterator = pageIterator;
        this.tDesc = tDesc;

    }

    @Override
    public void open(){
        this.page = pageIterator.getNextPage();
        tupleIndex = 0;
    }

    public void delete(int id){
        page.deleteTuple(id);
    }

    public void insert(Tuple tuple){
        page.insertTuple(tuple);
    }

    /*
    *
    * the tuple is calculated by merging the current value
    * with the updated value
    * */
    public void update(int id, Tuple tuple){
        delete(id);
        insert(tuple);
    }

    /*
    * checks if index is less than page size
    * if no more tuples in current page
    * gets new page from pageIterator
    * */
    @Override
    public boolean hasNext(){
        int pageSize = page.getHeader("size");
        if(index<pageSize){
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
            int offset = index*tDesc.tupleSize();
            byte[] tupleByte = new byte[tDesc.tupleSize()];
            System.arraycopy(page.pageData,offset,tupleByte,0,tDesc.tupleSize());

            return new Tuple(tupleByte, tDesc);
        }else{

//            throw exception
        }



        return null;
    }

    @Override
    public void close(){

    }

}
