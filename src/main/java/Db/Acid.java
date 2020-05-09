package Db;

/*
* main class
*
* */

import Db.bufferManager.Manager;
import Db.catalog.TupleDesc;
import Db.diskManager.DiskManager;

import Db.diskManager.Page;
import Db.server.Server;


public class Acid implements Utils{

     public Manager bufferPoolManager;
     public DiskManager diskManager;
     public TupleDesc tupleDesc;
     static Acid database;
     public String dbFolderPath;
     public String dbName;
     public Server server;

     private Acid()  {

         bufferPoolManager = new Manager(this);
         diskManager = new DiskManager(null);
         server = new Server(Utils.port);

     }


     void run() {
         server.run();
         diskManager.setDbFolder(Utils.dbFolderPath);
     }

     void stop(){
         server.stop();

     }

     public void setTupleDesc(TupleDesc td){
         this.tupleDesc = td;
     }

     public static Acid getDatabase() {
         if(database!= null) {
             return database;
         }else {
             database = new Acid();
             return database;
         }
     }

}
