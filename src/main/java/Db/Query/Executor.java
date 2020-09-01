package Db.query;

import Db.catalog.Tuple;
import Db.iterator.DbIterator;
import Db.server.ServerIO;

import java.io.IOException;

public class Executor {
    private DbIterator iterator;
    private ServerIO io;

    public Executor(DbIterator iterator, ServerIO io){
        this.io = io;
        this.iterator = iterator;
    }



    /*
    * return this in batches to other end of the network
    * */
    public void run() throws IOException, InterruptedException {

        Tuple tuple;
        iterator.open();
        while (true){
            tuple = iterator.next();
            if (tuple == null){
                break;
            }
            io.write(tuple.toString());
        }
    }
}
