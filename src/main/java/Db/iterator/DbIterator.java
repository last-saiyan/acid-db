package Db.iterator;

import Db.catalog.Tuple;

public interface DbIterator {


    public void open();

    public void close();

    public Tuple next();

    public boolean hasNext();


}
