package Db.iterator;

import Db.catalog.Tuple;

import java.util.NoSuchElementException;

public abstract  class Operator implements DbIterator {
    private  Tuple currentTuple;


    public abstract void open();

    public abstract void close();

    public Tuple next(){
        if(currentTuple == null) {
            currentTuple = fetchNext();
            if(currentTuple == null){
                throw new NoSuchElementException();
            }
        }
        Tuple tempTuple = currentTuple;
        currentTuple = null;
        return tempTuple;
    }

    public  boolean hasNext(){
        if(currentTuple == null){
            currentTuple = next();
        }
        if(currentTuple== null){
            return false;
        }else {
            return true;
        }
    }
    protected abstract Tuple fetchNext();



}
