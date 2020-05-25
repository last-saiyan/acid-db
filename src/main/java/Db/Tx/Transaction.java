package Db.Tx;

import java.util.ArrayList;
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
    private static LockTable locks = new LockTable();
    private Set<Integer> pagesLocked;


    public Transaction(){


        incrementID();
    }



    public synchronized void lockPage(int pageID, Permission perm){

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

    }


    public void commit(){

    }



    public void abort(){

    }

}
