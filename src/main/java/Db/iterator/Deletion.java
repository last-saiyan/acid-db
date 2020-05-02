package Db.iterator;

import Db.catalog.Tuple;

public class Deletion extends Operator {
    private DbIterator child;

    public Deletion(DbIterator child){
        this.child = child;

    }

    @Override
    public void open() {
        child.open();
    }

    @Override
    public void close() {
        child.close();
    }

    @Override
    public Tuple fetchNext() {
        if(child.hasNext()) {
            Tuple temp = child.next();
            ((TupleIterator) child).delete();
            return temp;
        }
        return null;
    }

    @Override
    public boolean hasNext() {
        return child.hasNext();
    }
}
