package Db.query;

import Db.catalog.TupleDesc;
import Db.query.predicate.Predicate;
import com.google.gson.Gson;

public class Query {

    String queryString;
    QueryMapper query;
    Predicate predicate;

    public Query(String queryString, TupleDesc td){
        this.queryString = queryString;
        query = queryObject(queryString);
        if(query == null){
            throw new RuntimeException("error query is not correct json parsing");
        }

//        if(!validateQuery(query)){
//            System.out.println("error query is not validated");
//            throw exception
//        }

        if(!(query.type.equals("init") || query.type.equals("create") || query.type.equals("insert"))){
            TupleDesc tupleDesc = td;
            predicate = new Predicate(query.where, tupleDesc);
        }

    }




    public QueryMapper getQuery(){
        return query;
    }


    public Predicate getPredicate(){
        return predicate;
    }

    private boolean validateQuery(QueryMapper query){


        switch (query.type){
            case "init":
                return validateInit(query);
            case "select":
                return validateSelect(query);
            case "update":
                return validateUpdate(query);
            case "create":
                return validateCreate(query);
            case "delete":
                return validateDelete(query);
        }

        return false;
    }

    private boolean validateInit(QueryMapper query){
        return false;
    }
    private boolean validateSelect(QueryMapper query){
        return false;
    }
    private boolean validateUpdate(QueryMapper query){
        return false;
    }
    private boolean validateCreate(QueryMapper query){
        return false;
    }
    private boolean validateDelete(QueryMapper query){
        return false;
    }

    private QueryMapper queryObject(String queryString){
        Gson gson = new Gson();
        try {
            return gson.fromJson(queryString, QueryMapper.class);
        }catch (com.google.gson.JsonSyntaxException e){
            throw new RuntimeException(e);
        }
    }

    private static  boolean isValidJson(String jsonString){
        Gson gson = new Gson();
        try {
            gson.fromJson(jsonString, Object.class);
            return true;
        }catch (com.google.gson.JsonSyntaxException e){
            return false;
        }
    }
}

