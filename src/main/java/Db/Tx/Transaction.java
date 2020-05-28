package Db.Tx;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

/*
* new transaction is created when new query comes
* when heap iterator asks for a page
* this checks if lock for the page is present in the table
* if valid gets the page
*
* */
public class Transaction {

    private static int tID = 0;
    private static LockTable lockManager = new LockTable();
    private Set<Integer> pagesSLocked;
    private Set<Integer> pagesXLocked;



    public Transaction(){
        pagesSLocked = new HashSet<>();
        pagesXLocked = new HashSet<>();
        incrementID();
    }



    /*
    * acquires lock for a page
    * if lock is not available
    * sleep the thread for some time
    * if lock is not acquired within the timeout
    * check for deadlock ?
    * abort the transaction
    * */
    public synchronized void lockPage(int pageID, Permission perm) throws InterruptedException {

        if(!lockManager.grantLock(pageID, tID, perm)) {
            int timeout = 5000;
            Thread.sleep(timeout);

            if(lockManager.grantLock(pageID, tID, perm)){
                if(perm == Permission.SHARED) {
                    pagesSLocked.add(pageID);
                }else {
                    pagesXLocked.add(pageID);
                }
                return;
            }else {
//                improve logic of which transaction gets aborted
                if(detectDeadLocks()){
                    abort();
                }
            }
        }else{
            if(perm == Permission.SHARED) {
                pagesSLocked.add(pageID);
            }else {
                pagesXLocked.add(pageID);
            }
        }
    }


    public Set<Integer> getPagesXLocked(){
        return pagesXLocked;
    }


    /*
    * checks if transaction holds lock on page with required permission
    * */
    public synchronized boolean holdsLock(int pageID, Permission perm){
        if(pagesXLocked.contains(pageID)){
            return true;
        }
        if(perm == Permission.SHARED && pagesSLocked.contains(pageID)){
            return true;
        }
        return false;
    }


    public boolean canLockPage(int pageID, Permission perm){
        return lockManager.canLockPage(pageID, tID, perm);
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


    static synchronized void incrementID(){
        tID++;
    }




    /*
    * following strict 2pl
    * this is called after transaction is committed
    * to release all the acquired locks by this transactions
    * */

    public void releaseAllLocks(){
        pagesSLocked.addAll(pagesXLocked);
        lockManager.releaseAllLock(tID, pagesSLocked);
    }


    public void commit(){
//        need to do more work here
        releaseAllLocks();
    }



    public void abort(){
//        need to do more work here
        releaseAllLocks();
    }

}
