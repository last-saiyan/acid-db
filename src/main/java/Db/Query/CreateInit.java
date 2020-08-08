package Db.query;

import Db.Acid;
import Db.catalog.Field;
import Db.catalog.TupleDesc;
import Db.catalog.TypesEnum;
import Db.diskManager.DiskManager;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.FileAlreadyExistsException;
import java.util.ArrayList;

public class CreateInit {
    Acid db;
    QueryMapper query;
    public CreateInit(Acid db, QueryMapper query){
        this.db = db;
        this.query = query;
    }



    /*
    *
    * creates catalog file using the schema
    * creates a db file
    * */
    public void handleCreate() throws IOException {

        Acid db = Acid.getDatabase();
        DiskManager diskManager =  db.diskManager;
        if(!diskManager.databaseExist(query.database)){

            ArrayList<ColValue> colNameType = query.values;
            ArrayList<Field> fieldList = new ArrayList();

            for(int i=0; i<colNameType.size(); i++){
                ColValue temp = colNameType.get(i);
                String[] valSize = temp.value.split(" ");

                Field field = new Field(temp.colName, TypesEnum.valueOf(valSize[0].toUpperCase()), Integer.parseInt(valSize[1]));
                fieldList.add(field);
            }

            TupleDesc td = new TupleDesc(fieldList);
            db.setTupleDesc(td);
            diskManager.createDbFile(query.database);

            try {
                td.serializeToDisk(db.dbFolderPath + "/"+ query.database + ".cat");
            }catch (IOException e){
                e.printStackTrace();
            }

        }else {
            throw new FileAlreadyExistsException(query.database,"", "database already exists");
        }

    }


    /*
    *
    * check if TupleDesc file exits
    * in the path if it does not exits return error
    * else if present initialize TupleDesc and database file
    *
    * */
    public void handleInit() throws IOException, ClassNotFoundException {
        DiskManager diskManager = db.diskManager;

        if(diskManager.databaseExist(query.database)){
            TupleDesc td = TupleDesc.deSerializeFromDisk(db.dbFolderPath + "/" +query.database + ".cat");
            diskManager.setDatabase(query.database);
            db.dbPageCount = diskManager.dbSize();
            db.setTupleDesc(td);
        }else {
            throw new FileNotFoundException(query.database);
        }
    }
}
