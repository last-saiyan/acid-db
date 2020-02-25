package Db.catalog;


/*
* field contains mapping of field name and field type
* */
public class Field {

    public Field(Type type, String name){
        this.type = type;
        this.fieldName = name;
    }

    public Type type;
    public String fieldName;

    public int getSize(){
        return this.type.size;
    }

}
