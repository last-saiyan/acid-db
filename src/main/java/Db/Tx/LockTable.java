package Db.Tx;

/*
* locktable contains a hashmaps
* to keep tract of locks
* maps the page id to the transaction id
* locks page in shared mode or exclusive mode
* */

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

public class LockTable {

//    map of exclusive locks pageID to transactionID
    private HashMap<Integer, Integer> PIDToTIDExclusiveLock;

//    map of shared locks pageID to transactionID set
    private HashMap<Integer, Set<Integer>> PIDToTIDSharedLock;


    public LockTable(){
        PIDToTIDExclusiveLock = new HashMap<>();
        PIDToTIDSharedLock = new HashMap<>();
    }


    /*
    * check if page is locked
    * if lock is held return false
    * else make entry in lock table
    * and return true
    * */
    public synchronized boolean grantLock(int pageID, int transactionID, Permission perm){

//        check if page has exclusive lock
        if (PIDToTIDExclusiveLock.containsKey(pageID)){
//                wait till the other transaction is completed
            System.out.println("page - " +  pageID + " is held by exclusive lock by transaction - " + transactionID);
            return false;
        }

        if(perm == Permission.SHARED){
            Set<Integer> TID;
            if (PIDToTIDSharedLock.containsKey(pageID)){
                TID = PIDToTIDSharedLock.get(pageID);
                if(!TID.contains(transactionID)){
                    TID.add(transactionID);
                }
                PIDToTIDSharedLock.put(pageID, TID);
            }else {
                TID = new HashSet<>();
                TID.add(pageID);
                PIDToTIDSharedLock.put(pageID, TID);
            }
            return true;

        }else {

            if(PIDToTIDSharedLock.containsKey(pageID)){
//                wait till the other transaction is completed
                System.out.println("page - " +  pageID + " is held by shared lock by transaction - " + transactionID);

                return false;
            }
            PIDToTIDExclusiveLock.put(pageID, transactionID);
            return true;
        }
    }



    /*
    * to be called after the transaction commits
    * following 2PL
    *
    * */
    public synchronized void releaseAllLock(int transactionID, Set<Integer> pageID){
        Iterator<Integer> pageIter = pageID.iterator();
        int tempPageID;
        Set<Integer> transactionIDSet;
        if (pageIter.hasNext()){
            tempPageID = pageIter.next();
            PIDToTIDExclusiveLock.remove(tempPageID);
//            shared lock - remove transactionID from set
            transactionIDSet = PIDToTIDSharedLock.get(pageID);
            transactionIDSet.remove(transactionID);
            PIDToTIDSharedLock.put(tempPageID, transactionIDSet);
        }
    }


}
