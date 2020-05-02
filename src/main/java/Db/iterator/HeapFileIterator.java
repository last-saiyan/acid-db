package Db.iterator;

import Db.bufferManager.Manager;
import Db.catalog.Tuple;
import Db.diskManager.Page;

/*
* iterates heapfile gives next heapfile
* interacts with the bufferpool
* */

public class HeapFileIterator {

    Manager bfPoolManager;
    int pageCount;
    Page currentPage;

    public HeapFileIterator(Manager mgr){
        bfPoolManager = mgr;
        pageCount = 0;
    }


    public boolean hasNext(){

        return false;
    }

    public void close(){
        pageCount = 0;
    }


    public Page getNextPage(){
        return bfPoolManager.getPage(pageCount);
    }

}
