package Db.iterator;

import Db.bufferManager.Manager;
import Db.catalog.Tuple;
import Db.diskManager.DiskManager;
import Db.diskManager.Page;

/*
* iterates heapfile gives next heapfile
* interacts with the bufferpool
* */

public class HeapFileIterator {

    Manager bfPoolManager;
    int pageCount;
    Page currentPage;
    DiskManager dskMgr;

    public HeapFileIterator(Manager mgr,DiskManager dskMgr){
        bfPoolManager = mgr;
        pageCount = 0;
        this.dskMgr = dskMgr;
    }


    public boolean hasNext(){

        if(pageCount < dskMgr.dbSize()){
            return true;
        }else {
            return false;
        }
    }

    public void close(){
        pageCount = 0;
    }


    public Page getNextPage(){
        return bfPoolManager.getPage(pageCount);
    }

}
