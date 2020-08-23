package Diskmanager;

import Db.catalog.*;
import Db.diskManager.Page;
import Db.iterator.HeapFileIterator;
import Db.iterator.TupleIterator;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class TestPage {


    TupleDesc getTD() {
        ArrayList<Field> fieldArrayList = new ArrayList<>();
        Field f1 = new Field("columnA", TypesEnum.STRING, 10);
        fieldArrayList.add(f1);
        f1 = new Field("columnB", TypesEnum.INTEGER, 4);
        fieldArrayList.add(f1);
        TupleDesc td = new TupleDesc(fieldArrayList);
        return td;
    }

    @Mock
    HeapFileIterator hfIter = mock(HeapFileIterator.class);

    private String RandomString( int size){
        String AlphaNumericString = "ABCDEFGHIJKLMNOPQRSTUVWXYZ" +
                "0123456789"+
                "abcdefghijklmnopqrstuvxyz";
        StringBuilder randomString = new StringBuilder(size);
        for (int i = 0; i < size; i++) {
            int index = (int)(AlphaNumericString.length()
                    * Math.random());
            randomString.append(AlphaNumericString.charAt(index));
        }
        return randomString.toString();
    }


    @Test
    void testEncodeDecode() throws IOException, InterruptedException {
        TupleDesc td = getTD();

        Page page = new Page(1,td);
        HashMap<String, Value> colVal;
        Tuple tuple;

        ArrayList<String> column1 = new ArrayList();
        ArrayList<Integer> column2 = new ArrayList();
        int count = 0;
        while (count <5){
            colVal = new HashMap<>();
            String generatedString = RandomString(5);
            colVal.put("columnA", new StringValue(generatedString, 10));
            column1.add(generatedString);
            int generatedInt = new Random().nextInt();
            colVal.put("columnB", new IntValue(generatedInt));
            column2.add(generatedInt);
            tuple = new Tuple(colVal);
            page.insertTuple(tuple);
            count++;
        }

        when(hfIter.next()).thenReturn(page, null);

        TupleIterator tupleIterator = new TupleIterator(hfIter, null);

        int i = 0;
        tupleIterator.open();
        while (true){
            tuple = tupleIterator.next();
            if (tuple == null){
                break;
            }
            Value<String> value = tuple.getMapValue().get("columnB");
            int temp = column2.get(i);
            Assertions.assertEquals(value.toString(), Integer.toString(temp));
            i++;
        }
    }


}
