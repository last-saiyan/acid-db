package Catalog;

import Db.query.ColValue;
import Db.query.Query;
import Db.catalog.Field;
import Db.catalog.TupleDesc;
import Db.catalog.TypesEnum;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.ArrayList;


public class TupleDescTest {

    @Test
    void testTupleSerializeDeSerialize() throws IOException, ClassNotFoundException {

        String queryString = "{\"type\": \"create\",\"database\": \"dbname\",\"values\": [{\"colName\" :\"column1\",\"value\": \"STRING 30\"},{\"colName\":\"column2\",\"value\": \"INTEGER 4\"}]}\n";
        Query query = new Query(queryString, null);

        ArrayList<ColValue> colNameType = query.getQuery().values;
        ArrayList<Field> fieldList = new ArrayList();
        ColValue temp;
        for(int i=0; i<colNameType.size(); i++){
            temp = colNameType.get(i);
            String[] valSize = temp.value.split(" ");
            Field field = new Field(temp.colName, TypesEnum.valueOf(valSize[0]), Integer.parseInt(valSize[1]));
            fieldList.add(field);

        }


        TupleDesc td = new TupleDesc(fieldList);
        String TdPath = "./tdfile.cat";
        td.serializeToDisk(TdPath);
        TupleDesc td1 = TupleDesc.deSerializeFromDisk(TdPath);

        Field f, f1;
        for(int i=0; i<td.getFieldList().size(); i++){
            f = td.getFieldList().get(i);
            f1 = td1.getFieldList().get(i);
            Assertions.assertEquals(f.size, f1.size);
            Assertions.assertEquals(f.fieldName, f1.fieldName);
            Assertions.assertEquals(f.typesEnum, f1.typesEnum);
            Assertions.assertEquals(f.id, f1.id);

        }


    }


}
