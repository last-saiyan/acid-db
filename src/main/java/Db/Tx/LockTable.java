package Db.Tx;

/*
* locktable contains a hashmaps
* to keep tract of locks
* maps the page id to the transaction id
* locks page in shared mode or exclusive mode
* */

import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

public class LockTable {

//    map of exclusive locks pageID to transactionID
    private HashMap<Integer, Integer> PIDExclusiveLock;

//    map of shared locks pageID to transactionID set
    private HashMap<Integer, Set<Integer>> PIDSharedLock;

//    map of transaction id that is waiting to lock page id
//    todo implement fair locking
    private HashMap<Integer, Set<Integer>> tIDWaiting;
    private static final Logger logger = Logger.getLogger(LockTable.class.getName());



    public LockTable(){
        PIDExclusiveLock = new HashMap<>();
        PIDSharedLock = new HashMap<>();
        tIDWaiting = new HashMap<>();

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
            addToWaitingList(transactionID, pageID);
//            wait till the other transaction is completed
            logger.log(Level.INFO, "page - "+pageID+ " is held by exclusive lock by transaction "+transactionID  );
            return false;
        }

        if(perm == Permission.SHARED){
            Set<Integer> TID;
            if (PIDSharedLock.containsKey(pageID)){
                TID = PIDSharedLock.get(pageID);
                TID.add(transactionID);
                PIDSharedLock.put(pageID, TID);
            }else {
                TID = new HashSet<>();
                TID.add(transactionID);
                PIDSharedLock.put(pageID, TID);
            }
            removeFromWaitingList(transactionID);
            return true;
        }else {

            if(PIDSharedLock.containsKey(pageID)){
                Set<Integer> tidSet = PIDSharedLock.get(pageID);
                if(tidSet.size() == 1 && tidSet.contains(transactionID)){
                    return true;
                }
//                wait till the other transaction is completed
                logger.log(Level.INFO, "page - "+pageID+ " is held by shared lock by transaction "+ transactionID );
                addToWaitingList(transactionID, pageID);
                return false;
            }
            PIDExclusiveLock.put(pageID, transactionID);
            removeFromWaitingList(transactionID);
            return true;
        }
    }


    /*
    *
    * adds transactionId to waiting graph
    * it looks at other pages
    * */
    private synchronized void addToWaitingList(int transactionID, int pageID){
        Set<Integer> waitingTidSet;
        if(PIDExclusiveLock.containsKey(pageID)){
            if(tIDWaiting.containsKey(transactionID)){
                waitingTidSet = tIDWaiting.get(transactionID);
                waitingTidSet.add(PIDExclusiveLock.get(pageID));
            }else {
                waitingTidSet = new HashSet<>();
                waitingTidSet.add(PIDExclusiveLock.get(pageID));
            }
        }else {
            if(tIDWaiting.containsKey(transactionID)){
                waitingTidSet = tIDWaiting.get(transactionID);
                waitingTidSet.addAll(PIDSharedLock.get(pageID));
            }else {
                waitingTidSet = PIDSharedLock.get(pageID);
            }
        }
        tIDWaiting.put(transactionID, waitingTidSet);
    }


    /*
    * this updates the lock waiting list
    * to be called at end of transaction
    * commit or abort
    * */
    private synchronized void removeFromWaitingList(int transactionID){
        if(tIDWaiting.containsKey(transactionID)){
            tIDWaiting.remove(transactionID);
        }
        for(Integer currTID : tIDWaiting.keySet()){
            if(tIDWaiting.get(currTID).contains(transactionID)){
                tIDWaiting.get(currTID).remove(transactionID);
            }
        }
    }



    public synchronized boolean canLockPage(int pageID, int transactionID, Permission perm) {


        if (PIDExclusiveLock.containsKey(pageID)) {
            if (PIDExclusiveLock.get(pageID) == transactionID) {
                return true;
            }
//            wait till the other transaction is completed
            logger.log(Level.INFO, "page - " + pageID + " is held by exclusive lock by transaction " + transactionID);
            return false;
        }
        if (perm == Permission.SHARED) {
            Set<Integer> TID;
            if (PIDSharedLock.containsKey(pageID)) {
                TID = PIDSharedLock.get(pageID);
                TID.add(transactionID);
                PIDSharedLock.put(pageID, TID);
            } else {
                TID = new HashSet<>();
                TID.add(transactionID);
                PIDSharedLock.put(pageID, TID);
            }
            return true;
        } else {

            if (PIDSharedLock.containsKey(pageID)) {
                Set<Integer> tidSet = PIDSharedLock.get(pageID);
                if(tidSet.size() == 1 && tidSet.contains(transactionID)){
                    return true;
                }
//                wait till the other transaction is completed
                logger.log(Level.INFO, "page - " + pageID + " is held by shared lock by transaction "+transactionID);
                return false;
            }
            PIDExclusiveLock.put(pageID, transactionID);
            return true;
        }
    }


    /*
    *
    * look for loop in tIDWaiting
    * if transactionID is waiting on another transaction tempTid
    * and tempTid is waiting for transactionID return true
    *
    * */
    public synchronized boolean detectDeadLock(int transactionID){

        if(tIDWaiting.containsKey(transactionID)){

            Set<Integer> waitingTidSet = tIDWaiting.get(transactionID);
//            check if any ID in waitingTidSet is waiting for transactionID
            Iterator<Integer> tIDIter = waitingTidSet.iterator();
            while (tIDIter.hasNext()){
                int tempTid = tIDIter.next();
                if (tIDWaiting.containsKey(tempTid)) {
                    Set<Integer> tempTidSet = tIDWaiting.get(tempTid);
//                    cycle is detected
                    if(tempTidSet.contains(transactionID)){
                        return true;
                    }
                }
            }
        }

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
            if(PIDSharedLock.containsKey(tempPageID)) {
                transactionIDSet = PIDSharedLock.get(tempPageID);
                transactionIDSet.remove(transactionID);
                if (transactionIDSet.isEmpty()) {
                    PIDSharedLock.remove(tempPageID);
                }else {
                    PIDSharedLock.put(tempPageID, transactionIDSet);
                }
            }
        }
//        this is called to free up map, if the transaction is aborted
        removeFromWaitingList(transactionID);
    }


}

