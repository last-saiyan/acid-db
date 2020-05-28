package Db.iterator;

import Db.Tx.Permission;
import Db.Tx.Transaction;
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
    Transaction tx;
    Permission perm;

    public HeapFileIterator(Manager mgr, DiskManager dskMgr, Transaction tx, Permission perm){
        bfPoolManager = mgr;
        pageCount = 0;
        this.dskMgr = dskMgr;
        this.tx = tx;
        this.perm = perm;
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
        return bfPoolManager.getPage(pageCount, tx, perm);
    }

}
