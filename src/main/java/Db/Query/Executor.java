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
    public void run() throws IOException {

        Tuple temp;
        iterator.open();
        while (iterator.hasNext()){
            temp = iterator.next();
            outputStream.write(temp.toString().getBytes());
        }
    }
}
