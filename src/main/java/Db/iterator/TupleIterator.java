package Db.iterator;

import Db.Page;
import Db.catalog.Tuple;
import Db.catalog.TupleDesc;


public class TupleIterator implements Iterator{

    private Page page;
    private int index;
    private TupleDesc tDesc;

    public TupleIterator(Page page, TupleDesc tDesc){
        this.page = page;
        this.tDesc = tDesc;
        int index = 0;
    }

    @Override
    public void open(){

    }

    @Override
    public boolean hasNext(){
        int pageSize = page.getHeader("size");
        if(index<pageSize){
            return true;
        }else{
            return false;
        }
    }

    @Override
    public Tuple next(){

        if(hasNext()){
            int offset = index*tDesc.tupleSize();
            byte[] tupleByte = new byte[tDesc.tupleSize()];
            System.arraycopy(page.pageData,offset,tupleByte,0,tDesc.tupleSize());

            return new Tuple(tupleByte);
        }else{

//            throw exception
        }



        return null;
    }

    @Override
    public void close(){

    }

}
