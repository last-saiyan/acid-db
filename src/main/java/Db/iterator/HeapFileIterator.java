package Db.iterator;

import Db.Tx.Permission;
import Db.Tx.Transaction;
import Db.bufferManager.Manager;
import Db.diskManager.DiskManager;
import Db.diskManager.Page;

import java.io.IOException;

/*
* iterates heapfile gives next heapfile
* interacts with the bufferpool
* */

public class HeapFileIterator {

    Manager bfPoolManager;
    int pageCount;
    DiskManager dskMgr;
    Transaction tx;
    Permission perm;

    public HeapFileIterator(Manager mgr, DiskManager dskMgr, Transaction tx, Permission perm){
        bfPoolManager = mgr;
        this.dskMgr = dskMgr;
        this.tx = tx;
        this.perm = perm;
    }


    public void open(){
        pageCount = 0;
    }


    public Page next() throws IOException, InterruptedException {
        if(pageCount < dskMgr.dbSize()){
            Page page = bfPoolManager.getPage(pageCount, tx, perm);
            pageCount++;
            return page;
        }else {
            return null;
        }
    }

    public Transaction getTx(){
        return tx;
    }

    public void close(){
        pageCount = 0;
    }

}
