package Db.catalog;


/*
* field contains mapping of field name and field type, size
* */
public class Field {

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
