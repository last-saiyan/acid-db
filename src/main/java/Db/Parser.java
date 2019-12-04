package Db;


import net.sf.jsqlparser.expression.Expression;
import net.sf.jsqlparser.schema.Column;
import net.sf.jsqlparser.schema.Table;
import net.sf.jsqlparser.statement.*;
import net.sf.jsqlparser.parser.CCJSqlParserUtil;
import net.sf.jsqlparser.statement.create.table.CreateTable;
import net.sf.jsqlparser.statement.delete.Delete;
import net.sf.jsqlparser.statement.insert.Insert;
import net.sf.jsqlparser.statement.select.PlainSelect;
import net.sf.jsqlparser.statement.select.Select;
import net.sf.jsqlparser.statement.update.Update;
import net.sf.jsqlparser.util.TablesNamesFinder;

import net.sf.jsqlparser.util.SelectUtils;

//import net.sf.jsqlparser.expression.
import java.util.List;


public class Parser  {
//    refactor the class
//    handle create database queries

    public static void parse(String statement) throws Exception{

        Statement stmt = CCJSqlParserUtil.parse(statement);
        TablesNamesFinder tablesNamesFinder = new TablesNamesFinder();


        if(stmt instanceof Select){
//            supports simple case
//            later refactor make use of the visitor design pattern
//            and handle the edge cases

            Select selectStatement = (Select) stmt;
            List<String> tableList = tablesNamesFinder.getTableList(selectStatement);
            PlainSelect plain = (PlainSelect) selectStatement.getSelectBody();

            if(tableList.size()>1 || plain.getGroupByColumnReferences()!= null ){
//                does not support operation
            }

             List selectitems = plain.getSelectItems();
             Expression whereExpression = plain.getWhere();
             plain.getOrderByElements();


        }else if (stmt instanceof Update){



        }else if (stmt instanceof Delete){


        }else if (stmt instanceof CreateTable){

            CreateTable createStmt = (CreateTable) stmt;

            createStmt.getTable();
            createStmt.getColumnDefinitions();


        }else if (stmt instanceof Insert){

            Insert insertStatement = (Insert) stmt;

            insertStatement.getColumns();
            insertStatement.getItemsList();
            insertStatement.getTable();



        }

    }

    public static void main(String[] args) throws Exception {

        String joinQuery = "SELECT Orders.OrderID, Customers.CustomerName, Orders.OrderDate FROM Orders INNER JOIN Customers ON Orders.CustomerID=Customers.CustomerID;";
        String whereQuery = "SELECT Orders.OrderID, Customers.CustomerName, Orders.OrderDate FROM Orders ,Customers where Orders.CustomerID=Customers.CustomerID;";
        String grpBy = "SELECT COUNT(CustomerID), Country FROM Customers GROUP BY Country;";
        String orderBy = "SELECT * FROM Customers ORDER BY Country DESC, CustomerName;";
        String updateQuery = "update tab1 set a = 2   where b=4";
        String CreateQuery = "CREATE TABLE table_name (column1 int, column1 int)";
        String insertQuery = "INSERT INTO Customers (CustomerName, City, Country) VALUES ('Cardinal', 'Stavanger', 'Norway');";



        Statement stmt = CCJSqlParserUtil.parse(CreateQuery);
//        Statement stmt = CCJSqlParserUtil.parse ("CREATE DATABASE database_name");

        TablesNamesFinder tablesNamesFinder = new TablesNamesFinder();

        if(stmt instanceof Select){
            Select selectStatement = (Select) stmt;
            List<String> tableList = tablesNamesFinder.getTableList(selectStatement);
            if(tableList.size()>1){
                System.out.print(tableList);
                PlainSelect plain=(PlainSelect)selectStatement.getSelectBody();
                List selectitems = plain.getSelectItems();


                System.out.print("error " + plain.getGroupByColumnReferences());
            }else {
                PlainSelect plain=(PlainSelect)selectStatement.getSelectBody();

                String tableName = tableList.get(0);
//                plain.

                System.out.println(tableName + " tablename " + plain.getOrderByElements() );
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
            System.out.println(createStmt.getTable());
            System.out.println(createStmt.getColumnDefinitions());

//            String tableName = (String) createStmt.getTable();

        }else if (stmt instanceof Delete){

        }else{
            System.out.println("operation not supported");
        }
    }
}
