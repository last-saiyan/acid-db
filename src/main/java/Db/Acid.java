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
     public String dbFile;
     public Server server;

     private Acid() throws Exception {

         bufferPoolManager = new Manager(this);
         diskManager = new DiskManager(null);
         server = new Server(Utils.port);

     }


     void run() {
         server.run();


     }

     void stop(){
         server.stop();

     }

     static Acid getDatabase() throws Exception {
         if(database!= null) {
             return database;
         }else {
             database = new Acid();
             return database;
         }
     }

}
