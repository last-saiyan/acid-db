package Db.Query;

import Db.catalog.Tuple;
import Db.iterator.DbIterator;

public class Executor {
    private DbIterator iterator;

    public Executor(DbIterator iterator){
        this.iterator = iterator;
    }


    public void run(){

        Tuple temp;

        while (iterator.hasNext()){
            temp = iterator.next();
//            return this in batches to other end of the network
        }


    }
}
