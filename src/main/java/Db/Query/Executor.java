package Db.query;

import Db.Tx.Transaction;
import Db.catalog.Tuple;
import Db.iterator.DbIterator;

import java.io.IOException;
import java.io.OutputStream;

public class Executor {
    private DbIterator iterator;
    private OutputStream outputStream;

    public Executor(DbIterator iterator, OutputStream outputStream){
        this.outputStream = outputStream;
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
            outputStream.write(tuple.toString().getBytes());
        }
    }
}
