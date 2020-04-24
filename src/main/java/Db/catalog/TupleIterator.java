package Db.catalog;

import Db.diskManager.Page;

import java.util.Iterator;

public class TupleIterator implements Iterator<Tuple> {

    private Page page;
    private int index;
    private byte[] data;
    private int tupleSize;
    private int limit;
    private byte[] temp;

    public TupleIterator(Page page){
        this.page = page;
        this.data = page.pageData;
        tupleSize = page.td.tupleSize();
        limit = page.getHeader("size");
        index = 0;
        temp = new byte[tupleSize];
    }


    @Override
    public boolean hasNext() {

        if( (index + 1)*tupleSize > limit ){
            return false;
        }else {
            return true;
        }
    }

    @Override
    public Tuple next() {
        if(hasNext()){
            int src = index*tupleSize;
            System.arraycopy(data, src, temp, 0, tupleSize);
            index++;
            return new Tuple(temp, page.td);
        }else{
//            throw exception
        }
        return null;
    }

    @Override
    public void remove(){
//        remove using delete operator

    }
}
