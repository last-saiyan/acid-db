package Db.query;

import Db.Acid;
import Db.Tx.Recovery;
import Db.Tx.Transaction;
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



    public void execute() throws InterruptedException, IOException, ClassNotFoundException {
        System.out.println(query.type + " type");
        if (query.type.equals("create")){
            handleCreate();
        }
        if(query.type.equals("init")){
            handleInit();
        }
    }

    /*
    *
    * creates catalog file using the schema
    * creates a db file
    * */
    private void handleCreate() throws IOException {

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

            Recovery.setupLogFile(query.database, td);

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
    private void handleInit() throws IOException, ClassNotFoundException, InterruptedException {
        DiskManager diskManager = db.diskManager;

        if(diskManager.databaseExist(query.database)){
            TupleDesc td = TupleDesc.deSerializeFromDisk(db.dbFolderPath + "/" +query.database + ".cat");
            diskManager.setDatabase(query.database);
            db.dbPageCount = diskManager.dbSize();
            db.setTupleDesc(td);
            Transaction tx = new Transaction(false);
            Recovery.setupLogFile(query.database,  td);
            tx.recover(query.database, td);
        }else {
            throw new FileNotFoundException(query.database);
        }
    }
}
