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
    private HashMap<Integer, Integer> PidTidExclusiveLock;

//    map of shared locks pageID to transactionID set
    private HashMap<Integer, Set<Integer>> PidTidSharedLock;

//    map of transaction id that is waiting to lock page id
//    todo implement fair locking
    private HashMap<Integer, Set<Integer>> tidWaitingForTid;
    private static final Logger logger = Logger.getLogger(LockTable.class.getName());



    public LockTable(){
        PidTidExclusiveLock = new HashMap<>();
        PidTidSharedLock = new HashMap<>();
        tidWaitingForTid = new HashMap<>();

    }


    /*
    * check if page is locked
    * if lock is held return false
    * else make entry in lock table
    * and return true
    * */
    public synchronized boolean grantLock(int pageID, int transactionID, Permission perm){

//        if same transaction holds exclusive lock on page return true else false
        if (PidTidExclusiveLock.containsKey(pageID)){
            if(PidTidExclusiveLock.get(pageID) == transactionID){
                return true;
            }
            addToWaitingList(transactionID, pageID);
//            wait till the other transaction is completed
            logger.log(Level.INFO, " transaction - "+ transactionID + " is waiting to acquire lock on page "+ pageID );

            return false;
        }

        if(perm == Permission.SHARED){
            Set<Integer> TID;
            if (PidTidSharedLock.containsKey(pageID)){
                TID = PidTidSharedLock.get(pageID);
                TID.add(transactionID);
                PidTidSharedLock.put(pageID, TID);
            }else {
                TID = new HashSet<>();
                TID.add(transactionID);
                PidTidSharedLock.put(pageID, TID);
            }
            removeFromWaitingList(transactionID);
            return true;
        }else {

            if(PidTidSharedLock.containsKey(pageID)){
                Set<Integer> tidSet = PidTidSharedLock.get(pageID);
                if(tidSet.size() == 1 && tidSet.contains(transactionID)){
                    return true;
                }
//                wait till the other transaction is completed
                logger.log(Level.INFO, " transaction - "+ transactionID + " is waiting to acquire lock on page "+ pageID );
                addToWaitingList(transactionID, pageID);
                return false;
            }
            PidTidExclusiveLock.put(pageID, transactionID);
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
        Set<Integer> waitingPidSet;
        if(PidTidExclusiveLock.containsKey(pageID)){
            if(tidWaitingForTid.containsKey(transactionID)){
                waitingPidSet = tidWaitingForTid.get(transactionID);
                waitingPidSet.add(PidTidExclusiveLock.get(pageID));
            }else {
                waitingPidSet = new HashSet<>();
                waitingPidSet.add(PidTidExclusiveLock.get(pageID));
            }
        }else {
            if(tidWaitingForTid.containsKey(transactionID)){
                waitingPidSet = tidWaitingForTid.get(transactionID);
                waitingPidSet.addAll(PidTidSharedLock.get(pageID));
            }else {
                waitingPidSet = PidTidSharedLock.get(pageID);
            }
        }
        tidWaitingForTid.put(transactionID, waitingPidSet);
    }


    /*
    * this updates the lock waiting list
    * to be called at end of transaction
    * commit or abort
    * */
    private synchronized void removeFromWaitingList(int transactionID){
        if(tidWaitingForTid.containsKey(transactionID)){
            tidWaitingForTid.remove(transactionID);
        }
        for(Integer currTID : tidWaitingForTid.keySet()){
            if(tidWaitingForTid.get(currTID).contains(transactionID)){
                tidWaitingForTid.get(currTID).remove(transactionID);
            }
        }
    }



    public synchronized boolean canLockPage(int pageID, int transactionID, Permission perm) {


        if (PidTidExclusiveLock.containsKey(pageID)) {
            if (PidTidExclusiveLock.get(pageID) == transactionID) {
                return true;
            }
//            wait till the other transaction is completed
            logger.log(Level.INFO, "page - " + pageID + " is held by exclusive lock by transaction " + transactionID);
            return false;
        }
        if (perm == Permission.SHARED) {
            Set<Integer> TID;
            if (PidTidSharedLock.containsKey(pageID)) {
                TID = PidTidSharedLock.get(pageID);
                TID.add(transactionID);
                PidTidSharedLock.put(pageID, TID);
            } else {
                TID = new HashSet<>();
                TID.add(transactionID);
                PidTidSharedLock.put(pageID, TID);
            }
            return true;
        } else {

            if (PidTidSharedLock.containsKey(pageID)) {
                Set<Integer> tidSet = PidTidSharedLock.get(pageID);
                if(tidSet.size() == 1 && tidSet.contains(transactionID)){
                    return true;
                }
//                wait till the other transaction is completed
                logger.log(Level.INFO, "page - " + pageID + " is held by shared lock by transaction "+transactionID);
                return false;
            }
            PidTidExclusiveLock.put(pageID, transactionID);
            return true;
        }
    }


    /*
    *
    * look for loop in tidWaitingForTid
    * if transactionID is waiting on another transaction tempTid
    * and tempTid is waiting for transactionID return true
    *
    * */
    public synchronized boolean detectDeadLock(int transactionID){

        if(tidWaitingForTid.containsKey(transactionID)){

            Set<Integer> waitingTidSet = tidWaitingForTid.get(transactionID);
//            check if any ID in waitingTidSet is waiting for transactionID
            Iterator<Integer> tIDIter = waitingTidSet.iterator();
            while (tIDIter.hasNext()){
                int tempTid = tIDIter.next();
                if (tidWaitingForTid.containsKey(tempTid)) {
                    Set<Integer> tempTidSet = tidWaitingForTid.get(tempTid);
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
        if (PidTidExclusiveLock.containsKey(pageID)){
            return PidTidExclusiveLock.get(pageID);
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
            PidTidExclusiveLock.remove(tempPageID);
//            shared lock - remove transactionID from set
            if(PidTidSharedLock.containsKey(tempPageID)) {
                transactionIDSet = PidTidSharedLock.get(tempPageID);
                transactionIDSet.remove(transactionID);
                if (transactionIDSet.isEmpty()) {
                    PidTidSharedLock.remove(tempPageID);
                }else {
                    PidTidSharedLock.put(tempPageID, transactionIDSet);
                }
            }
        }
//        this is called to free up map, if the transaction is aborted
        removeFromWaitingList(transactionID);
    }


}

