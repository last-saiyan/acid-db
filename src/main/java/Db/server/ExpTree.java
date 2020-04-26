package Db.server;

import Db.catalog.Tuple;

import java.util.ArrayList;

public class ExpTree {
    public  Node root;

    public ExpTree(ArrayList<String> postFixExp){


    }

    public boolean evalExp(Tuple tuple){

        return true;
    }

    public void addItem(){

    }

}

class Node{

    public Item item;
    public Node left;
    public Node right;
    public Item calculate(){

        return null;
    }
}


abstract class Item{
    public String type;
    public Item(String type){
        this.type = type;
    }
}

class operator extends Item{
    public String name;
    public operator(String name){
        super("operator");
        this.name = name;
    }
    public void evaluate(){

    }

}

class operand extends Item {
    public operand (){
        super("operand");
    }

}