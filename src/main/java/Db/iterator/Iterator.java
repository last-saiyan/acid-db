package Db.iterator;

import Db.Tuples.Tuple;

public interface Iterator {

    public void open();

    public void close();

    public Tuple next();

    public boolean hasNext();


}
