package Db.server;

import Db.catalog.Field;
import Db.catalog.Tuple;
import Db.catalog.TupleDesc;

import java.util.ArrayList;
import java.util.EmptyStackException;
import java.util.HashSet;
import java.util.Stack;

public class ExpTree {
    public  Item root;
    private Stack<Item> itemStack;
    private TupleDesc td;

    public ExpTree(ArrayList<String> postFixExp, TupleDesc td){
        this.td = td;
        buildTree(postFixExp);
    }


    private void buildTree(ArrayList<String> postFixExp){
        for(int i=0;i<postFixExp.size();i++){
            String currentToken = postFixExp.get(i);
            String type = getType(currentToken);
            Item item = null;
            if(type == "field"){
                item = new OperandField(currentToken);
                itemStack.push(item);
            }
            if(type == "integer"||type == "string"){
                if(type == "string") {
                    currentToken = currentToken.substring(1, currentToken.length()-1);
                    item = new OperandValue(currentToken.getBytes());
                    itemStack.push(item);
                }else {
                    item = new OperandValue(currentToken.getBytes());
                    itemStack.push(item);
                }
            }

            if(type == "operator"){
                Item rootItem = null;
                try {
                    rootItem = new Operator(currentToken);
                    rootItem.right = itemStack.pop();
                    rootItem.left = itemStack.pop();
                }catch (EmptyStackException e){
//                    invalid predicate
                }
                itemStack.push(rootItem);
            }
        }
    }

    private String getType(String item){
        item = item.trim();

//        field
        if(td.getFieldMap().containsKey(item)){
            return "field";
        }

//        string
        if(item.charAt(0)=='\'' && item.charAt(item.length()-1) == '\''){
            return "String";
        }

//        integer
        boolean isInt;
        try{
            Integer.parseInt(item);
            isInt = true;
        }catch (NumberFormatException e){
            isInt = false;
        }
        if(isInt){
            return "integer";
        }

//        operator
        if(item.length() ==1) {
            char first = item.charAt(0);
            if (first=='+'||first=='-'||first=='*'||first=='/'){
                return "operator";
            }else{
                return "unknown";
            }
        }else {
            return "unknown";
        }
    }

    public boolean evalExp(Tuple tuple){

        OperandValue value = evalTree(root, tuple);

        return false;
    }
    private OperandValue evalTree(Item root, Tuple tuple){
        if(root instanceof OperandField){
            return ((OperandField) root).convertFieldToValue(tuple);
        }
        if(root instanceof OperandValue){
            return (OperandValue) root;
        }
        Item left = root.left;
        Item right = root.right;
        if(!(left instanceof OperandValue)){
            left = evalTree(left, tuple);
        }
        if(!(right instanceof OperandValue)){
            right = evalTree(right, tuple);
        }
        Operator operator = (Operator) root;
        return operator.evaluate((OperandValue)left, (OperandValue)right);
    }

}

abstract class Item{
    public String type;
    public Item(String type){
        this.type = type;
    }
    public Item left;
    public Item right;
}



class Operator extends Item{
    public String operatorName;
    public Operator(String name){
        super("operator");
        this.operatorName = name;
    }
    public OperandValue evaluate(OperandValue op1, OperandValue op2){
        switch (operatorName){
            case "|":
                return logicalOrExp(op1, op2);

        }
//        throw unknown operator error
        return null;
    }
    private OperandValue logicalOrExp(OperandValue left, OperandValue right){
        byte[] leftValue = left.value;
        byte[] rightValue = right.value;

        if(left.value.length==1 && right.value.length==1){
            if(left.value[0] == 1 || right.value[0] == 1){
                byte[] temp = {1};
                return new OperandValue(temp);
            }else {
                byte[] temp = {0};
                return new OperandValue(temp);
            }
        }else {
//            error?
        }

        return null;
    }
    private OperandValue equalOperator(OperandValue left, OperandValue right){
        if(left.value == right.value){
            byte[] temp = {1};
            return new OperandValue(temp);
        }else {
            byte[] temp = {0};
            return new OperandValue(temp);
        }
    }

}

class OperandField extends Item{
    public String fieldName;
    public OperandField(String fieldName){
        super("field");
        this.fieldName = fieldName;
    }
    public OperandValue convertFieldToValue(Tuple tuple){
        return new OperandValue(tuple.getValue(fieldName));
    }
}

class OperandValue extends Item{
    public byte[] value;
    public OperandValue(byte[] value){
        super("value");
        this.value = value;
    }
}