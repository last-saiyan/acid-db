package Db.iterator;


import Db.query.ColValue;
import Db.catalog.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

public class InsertIterator implements DbIterator {

    ArrayList<Tuple> tuples;
    Iterator<Tuple> iterator;

    /*
    * todo
    * handle ordering bug (sort values by keys)
    *  */
    public InsertIterator(ArrayList<ColValue> values, TupleDesc td){
        tuples = new ArrayList();
        HashMap<String, Value> tupleMap = new HashMap();
        HashMap<String, Field> fieldMap = td.getFieldMap();
        Value tempValue;
        for (int i=0; i<values.size(); i++){
            ColValue colValue = values.get(i);
            TypesEnum type = fieldMap.get(colValue.colName).typesEnum;
            int size = fieldMap.get(colValue.colName).size;
            tempValue = ValueFactory.getValue(type,size, colValue.value);
            tupleMap.put(colValue.colName, tempValue);
        }
        Tuple temp = new Tuple(tupleMap);
        tuples.add(temp);
    }
    @Override
    public void open() {
        iterator = tuples.iterator();
    }



    @Override
    public Tuple next() {
        if (iterator.hasNext()) {
            return iterator.next();
        }else {
            return null;
        }
    }

}
