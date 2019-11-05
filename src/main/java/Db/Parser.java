package Db;


import net.sf.jsqlparser.schema.Table;
import net.sf.jsqlparser.statement.*;
import net.sf.jsqlparser.parser.CCJSqlParserUtil;
import net.sf.jsqlparser.statement.create.table.CreateTable;
import net.sf.jsqlparser.statement.delete.Delete;
import net.sf.jsqlparser.statement.select.Select;
import net.sf.jsqlparser.statement.update.Update;
import net.sf.jsqlparser.util.TablesNamesFinder;

import net.sf.jsqlparser.util.SelectUtils;

//import net.sf.jsqlparser.expression.
import java.util.List;


public class Parser  {
//    refactor the class
//    handle create database queries

    public static void main(String[] args) throws Exception {
//        Statement stmt = CCJSqlParserUtil.parse("update tab1 set a = 2   where b=4");
        Statement stmt = CCJSqlParserUtil.parse ("CREATE TABLE table_name (column1 int)");
//        Statement stmt = CCJSqlParserUtil.parse ("CREATE DATABASE database_name");

        TablesNamesFinder tablesNamesFinder = new TablesNamesFinder();

        if(stmt instanceof Select){
            Select selectStatement = (Select) stmt;
            List<String> tableList = tablesNamesFinder.getTableList(selectStatement);
            if(tableList.size()>1){
                System.out.print("error");
            }else {
                String tableName = tableList.get(0);
            }
        }else if (stmt instanceof Update){
            Update updateStatement = (Update) stmt;
            List<String> tableList = tablesNamesFinder.getTableList(updateStatement);
            if(tableList.size()>1){
                System.out.print("error");
            }else {
                String tableName = tableList.get(0);
            }
        }
        else if (stmt instanceof CreateTable){

            CreateTable createStmt = (CreateTable) stmt;
            Table table = createStmt.getTable();

//            table.getName();
//            System.out.println(createStmt.getTable());

//            String tableName = (String) createStmt.getTable();

        }else if (stmt instanceof Delete){

        }else{
            System.out.println("operation not supported");
        }
    }
}
