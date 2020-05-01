package Db.iterator;

import Db.Page;
import Db.bufferManager.Manager;


public class PageIterator implements DbIterator {


    public PageIterator(Manager bm){
        this.bm = bm;
    }

    Manager bm;
    Page page;
    int pageID;


    @Override
    public void open(){



    }

    @Override
    public boolean hasNext(){

        return false;
    }


    @Override
    public Page next(){

        return null;
    }


    @Override
    public void close(){

    }
}
