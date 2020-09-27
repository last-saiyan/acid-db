package Db.Tx;

import Db.catalog.TupleDesc;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

/*
* new transaction is created when new query comes
* when heap iterator asks for a page
* this checks if lock for the page is present in the table
* if valid gets the page
*
* */
public class Transaction {

    private static int tIdCounter;
    private  int tID;
    private static LockTable lockManager = new LockTable();
    private Set<Integer> pagesSLocked;
    private Set<Integer> pagesXLocked;
    private boolean explicit;
    private static Recovery recoveryManager = new Recovery();
    private int prevLsn = -1;
    private static final Logger logger = Logger.getLogger(Transaction.class.getName());


    public Transaction(boolean explicit){
        pagesSLocked = new HashSet<>();
        pagesXLocked = new HashSet<>();
        tID = incrementID();
        this.explicit = explicit;
//        recoveryManager.newTransaction(tID);
    }



    public boolean isExplicit(){
        return explicit;
    }


    /*
    * acquires lock for a page
    * if lock is not available
    * sleep the thread for some time
    * if lock is not acquired within the timeout
    * check for deadlock ?
    * abort the transaction
    * */
    public void lockPage(int pageID, Permission perm) {
        if(!lockManager.grantLock(pageID, tID, perm)) {
            doWait(pageID, perm);
        }else{
            if(perm == Permission.SHARED) {
                pagesSLocked.add(pageID);
            }else {
                pagesXLocked.add(pageID);
            }
        }
    }


    private void doWait(int pageID, Permission perm){
        synchronized(lockManager){

            while (!lockManager.grantLock(pageID, tID, perm)){
                try{
                    lockManager.wait(10000);

                    if(!lockManager.grantLock(pageID, tID, perm)){
                        if (detectDeadLocks()){
                            logger.log(Level.SEVERE,  "Deadlock");
                            throw new RuntimeException("deadlock detected");
                        }
                    }
                } catch(InterruptedException e){
                    logger.log(Level.SEVERE, tID + " - has exception when waiting");
                }
            }
        }
    }


    private void doNotify(){
        synchronized(lockManager){
            lockManager.notify();
        }
    }



    public Set<Integer> getPagesXLocked(){
        return pagesXLocked;
    }


    /*
    * checks if transaction holds lock on page with required permission
    * */
    public boolean holdsLock(int pageID, Permission perm){
        if(pagesXLocked.contains(pageID)){
            return true;
        }
        if(perm == Permission.SHARED && pagesSLocked.contains(pageID)){
            return true;
        }
        return false;
    }


    public boolean canLockPage(int pageID, Permission perm){
        if(lockManager.canLockPage(pageID, tID, perm)){
            if(perm == Permission.EXCLUSIVE){
                pagesXLocked.add(pageID);
            }else {
                pagesSLocked.add(pageID);
            }
            return true;
        }
        return false;
    }


    /*
    * checks if there is a cycle in graph
    * */
    private boolean detectDeadLocks(){
        return lockManager.detectDeadLock(tID);
    }


    public int getTID(){
        return tID;
    }


    public void recover(String dbName, TupleDesc td) throws IOException, InterruptedException {
        recoveryManager.recovery(dbName, td, this);
    }


    /*
    * when this is called return the lsn
    * lsn is used to update the page LSN
    * */
    public int addLogRecord(LogRecord record){
        try {
            prevLsn = recoveryManager.addLogRecord(record, tID);
        } catch (IOException e) {
            this.abort();
            e.printStackTrace();
        }
        return prevLsn;
    }



    public int getPrevLsn() {
        return prevLsn;
    }



    static synchronized int incrementID(){
        return tIdCounter++;
    }


    /*
    * following strict 2pl
    * this is called after transaction is committed
    * to release all the acquired locks by this transactions
    * */

    public void releaseAllLocks(){
        pagesSLocked.addAll(pagesXLocked);
        lockManager.releaseAllLock(tID, pagesSLocked);
        doNotify();
    }


    public void commit(){
        try {
            recoveryManager.commit(tID);
        } catch (IOException e) {
            e.printStackTrace();
            abort();
        }
        releaseAllLocks();
        logger.log(Level.INFO, "Transaction - {0} is committed", tID );
    }



    public void abort() {
        try {
            recoveryManager.abort(tID, this);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        releaseAllLocks();
        logger.log(Level.INFO, "Transaction - {0} is Aborted", tID );
    }

}
