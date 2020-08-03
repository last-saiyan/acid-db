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
            System.out.println("error query is not correct json parsing");
//            throw exception
        }
        if(!validateQuery(query)){
            System.out.println("error query is not validated");
//            throw exception
        }
        System.out.println(query.type +" asdfasd");
        if(!(query.type.equals("init") || query.type.equals("select") || query.type.equals("create") || query.type.equals("insert"))){
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
            QueryMapper query = gson.fromJson(queryString, QueryMapper.class);
            return query;
        }catch (com.google.gson.JsonSyntaxException e){
            return null;
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

