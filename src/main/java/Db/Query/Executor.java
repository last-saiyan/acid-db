package Db.query;

import Db.Tx.Transaction;
import Db.catalog.Tuple;
import Db.iterator.DbIterator;

import java.io.IOException;
import java.io.OutputStream;

public class Executor {
    private DbIterator iterator;
    private OutputStream outputStream;
    private Transaction tx;

    public Executor(DbIterator iterator, OutputStream outputStream, Transaction tx){
        this.outputStream = outputStream;
        this.iterator = iterator;
        this.tx = tx;
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
        tx.commit();
        tx = null;

    }
}
