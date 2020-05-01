package Db.iterator;

import Db.Tuples.Tuple;

public abstract  class Operator implements DbIterator {

    public void open();

    public void close();

    public Tuple next();

    public boolean hasNext();


}
