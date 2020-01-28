package Db.server;

import com.google.gson.Gson;

import java.util.ArrayList;

public class Query {
    String type;
    ArrayList<String> columns;
    String where;
    ArrayList<Values> values;

    static  boolean isValid(String jsonString){
        Gson gson = new Gson();
        try {
            gson.fromJson(jsonString, Object.class);
            return true;
        }catch (com.google.gson.JsonSyntaxException e){
            return false;
        }
    }
}

class Values{
    String colName;
    String value;
}