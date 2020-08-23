package Db.iterator;

import Db.catalog.Tuple;

import java.io.IOException;

public interface DbIterator {


    public void open();

    public Tuple next() throws IOException, InterruptedException;

}
