package Db.query;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class QueryTest {

    @Test
    void TestCreateQueryMapper(){
        String queryString = "{\"type\": \"create\",\"database\": \"dbname\",\"values\": [{\"colName\" :\"column1\",\"value\": \"string 30\"},{\"colName\":\"column2\",\"value\": \"int\"}]}\n";
        Query query = new Query(queryString, null);

        Assertions.assertEquals("dbname",query.query.database);
        Assertions.assertEquals("create", query.query.type);
        Assertions.assertEquals("column1", query.query.values.get(0).colName);
        Assertions.assertEquals("string 30", query.query.values.get(0).value);

    }
}
