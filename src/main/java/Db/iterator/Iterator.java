package Db.iterator;

import Db.Tuples.Tuple;

public interface Iterator {
    int  index = 0;

    public void open();

    public void close();

    public Object next();

    public boolean hasNext();


}
