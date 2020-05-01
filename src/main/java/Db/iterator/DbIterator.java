package Db.iterator;

import Db.catalog.Tuple;

public interface DbIterator {
    int  index = 0;

    public void open();

    public void close();

    public Tuple next();

    public boolean hasNext();


}
