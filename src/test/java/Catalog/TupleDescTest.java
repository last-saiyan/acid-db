package Catalog;

import Db.Query.ColValue;
import Db.Query.Query;
import Db.catalog.Field;
import Db.catalog.TupleDesc;
import Db.catalog.TypesEnum;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.ArrayList;

import static com.sun.org.apache.xalan.internal.lib.ExsltStrings.split;

public class TupleDescTest {

    @Test
    void testTupleSerializeDeSerialize() throws IOException, ClassNotFoundException {

        String queryString = "{\"type\": \"create\",\"database\": \"dbname\",\"values\": [{\"colName\" :\"column1\",\"value\": \"STRING 30\"},{\"colName\":\"column2\",\"value\": \"INTEGER 4\"}]}\n";
        Query query = new Query(queryString);

        ArrayList<ColValue> colNameType = query.getQuery().values;
        ArrayList<Field> fieldList = new ArrayList();
        ColValue temp;
        for(int i=0; i<colNameType.size(); i++){
            temp = colNameType.get(i);
            String[] valSize = temp.value.split(" ");
            Field field = new Field(temp.colName, TypesEnum.valueOf(valSize[0]), Integer.parseInt(valSize[1]));
            fieldList.add(field);

        }
        System.out.println(fieldList.size());

        TupleDesc td = new TupleDesc(fieldList);
        System.out.println(td.getFieldList().size());

        String TdPath = "./tdfile.cat";
        td.serializeToDisk(TdPath);

        td = TupleDesc.deSerializeFromDisk(TdPath);
        System.out.println(td.getFieldList().size());




    }


}
