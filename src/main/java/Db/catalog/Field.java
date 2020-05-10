package Db.catalog;


import java.io.Serializable;

/*
* field contains mapping of field name and field type, size
* */
public class Field implements Serializable {

    public Field(String name, TypesEnum type, int size){
        this.size = size;
        this.fieldName = name;
        typesEnum = type;
    }

    public String fieldName;
    public int id;
    public TypesEnum typesEnum;
    public int size;

    public int getSize(){
        return this.size;
    }

}
