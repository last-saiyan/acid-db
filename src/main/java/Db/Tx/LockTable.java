package Db.Tx;

/*
* locktable contains a hashmaps
* to keep tract of locks
* maps the page id to the transaction id
* locks page in shared mode or exclusive mode
* */

import java.util.*;

public class LockTable {

//    map of exclusive locks pageID to transactionID
    private HashMap<Integer, Integer> PIDExclusiveLock;

//    map of shared locks pageID to transactionID set
    private HashMap<Integer, Set<Integer>> PIDSharedLock;

//    map of transaction id that is waiting to lock page id

    private HashMap<Integer, Integer> TIDWaitingforSLocks;
    private HashMap<Integer, Integer> TIDWaitingforXLocks;


    public LockTable(){
        PIDExclusiveLock = new HashMap<>();
        PIDSharedLock = new HashMap<>();
    }


    /*
    * check if page is locked
    * if lock is held return false
    * else make entry in lock table
    * and return true
    * */
    public synchronized boolean grantLock(int pageID, int transactionID, Permission perm){

//        if same transaction holds exclusive lock on page return true else false
        if (PIDExclusiveLock.containsKey(pageID)){

            if(PIDExclusiveLock.get(pageID) == transactionID){
                return true;
            }
            addToWaitingList(transactionID, pageID, perm);
//            wait till the other transaction is completed
            System.out.println("page - " +  pageID + " is held by exclusive lock by transaction - " + transactionID);
            return false;
        }

        if(perm == Permission.SHARED){
            Set<Integer> TID;
            if (PIDSharedLock.containsKey(pageID)){
                TID = PIDSharedLock.get(pageID);
                if(!TID.contains(transactionID)){
                    TID.add(transactionID);
                }
                PIDSharedLock.put(pageID, TID);
            }else {
                TID = new HashSet<>();
                TID.add(pageID);
                PIDSharedLock.put(pageID, TID);
            }
            removeFromWaitingList(transactionID, perm);
            return true;
        }else {

            if(PIDSharedLock.containsKey(pageID)){
//                wait till the other transaction is completed
                System.out.println("page - " +  pageID + " is held by shared lock by transaction - " + transactionID);
                addToWaitingList(transactionID, pageID, perm);
                return false;
            }
            PIDExclusiveLock.put(pageID, transactionID);
            removeFromWaitingList(transactionID, perm);
            return true;
        }
    }


    /*
    *
    * */
    private void addToWaitingList(int transactionID, int pageID, Permission perm){
        if(perm == Permission.SHARED){
            TIDWaitingforSLocks.put(transactionID, pageID);
        }else {
            TIDWaitingforXLocks.put(transactionID, pageID);
        }
    }


    /*
    * if transaction is present
    * */
    private void removeFromWaitingList(int transactionID, Permission perm){
        if(perm == Permission.SHARED){
            if(TIDWaitingforSLocks.containsKey(transactionID)){
                TIDWaitingforSLocks.remove(transactionID);
            }
        }else {
            if(TIDWaitingforXLocks.containsKey(transactionID)){
                TIDWaitingforXLocks.remove(transactionID);
            }
        }
    }


    //            get the pageid that has to be locked
//            check if other transaction is waiting for this
//            and check if other is transaction is also waiting for lock on a page
//            held by this transactionID

    public boolean detectDeadLock(int transactionID){



        return false;
    }

    boolean transactionHoldsLock(int transactionID, int pageID){

        return false;
    }


    /*
    * this is returns the transactionID that holds the
    * exclusive lock on the given pageID
    * */
    public int getTransactionID(int pageID){
        if (PIDExclusiveLock.containsKey(pageID)){
            return PIDExclusiveLock.get(pageID);
        }
        return -1;
    }


    /*
    * to be called after the transaction commits or aborts
    * following 2PL
    *
    * */
    public synchronized void releaseAllLock(int transactionID, Set<Integer> pageID){
        Iterator<Integer> pageIter = pageID.iterator();
        int tempPageID;
        Set<Integer> transactionIDSet;
        if (pageIter.hasNext()){
            tempPageID = pageIter.next();
            PIDExclusiveLock.remove(tempPageID);
//            shared lock - remove transactionID from set
            transactionIDSet = PIDSharedLock.get(pageID);
            transactionIDSet.remove(transactionID);
            PIDSharedLock.put(tempPageID, transactionIDSet);
        }

//        this is called to free up map, if the transaction is aborted
        removeFromWaitingList(transactionID, Permission.SHARED);
        removeFromWaitingList(transactionID, Permission.EXCLUSIVE);

    }


}
