package Db.iterator;

import Db.Query.Value;
import Db.catalog.Tuple;

import java.util.ArrayList;
import java.util.Iterator;

public class InsertIterator implements DbIterator {

    ArrayList<Tuple> tuples;
    Iterator<Tuple> iterator;

    public InsertIterator(ArrayList<Value> values){


    }
    @Override
    public void open() {
        iterator = tuples.iterator();
    }

    @Override
    public void close() {

    }

    @Override
    public Tuple next() {
        return iterator.next();
    }

    @Override
    public boolean hasNext() {
        return iterator.hasNext();
    }
}
