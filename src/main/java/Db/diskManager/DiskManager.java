package Db.diskManager;


import Db.Acid;
import Db.Constants;
import Db.Utils;
import Db.catalog.Field;
import Db.catalog.TupleDesc;

import java.io.*;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;

public class DiskManager implements Constants {
    private File file;
    RandomAccessFile ffile;
    private TupleDesc td;
    private Acid db;
    private String dbFolderPath;


//    public DiskManager(String path,  TupleDesc td) throws FileNotFoundException{
//        this.file = new File(path);
//        this.td = td;
//        ffile = new RandomAccessFile(file, "rws");
//    }


    public DiskManager(Acid db, String dbFolderPath) {
        this.db = db;
        setDbFolder(dbFolderPath);
    }


    /*
    *
    * check if the dbName.db file exists
    * in db folder
    *
    * */
    public boolean databaseExist(String dbName){
//        should i validate the file size?
        File dbFile =  new File(dbFolderPath +"/" + dbName + ".cat");
        boolean a=  dbFile.isFile();
        return a;
    }


    /*
    *
    * check if given path string is a folder
    * if not create one and assign it to dbFolderPath
    *
    * */
    public void setDbFolder(String dbFolderPath){
        File dbFolder = new File(dbFolderPath);
        if(dbFolder.exists()){
            if(dbFolder.isDirectory()){
                this.dbFolderPath = dbFolderPath;
            }else {
                if(!dbFolder.mkdir()){
                    System.out.println("unable to create a db");
//                    throw error
                }
                this.dbFolderPath = dbFolderPath;
            }
        }else {
            dbFolder.mkdir();
            this.dbFolderPath = dbFolderPath;
        }
    }


    public void createDbFile(String dbName) throws IOException {
        new File(dbFolderPath+ "/" + dbName + ".db").createNewFile();
    }

    public void setDatabase(String file) throws FileNotFoundException {
        this.file = new File(file);
        ffile = new RandomAccessFile(this.file, "rws");
    }


    private static int startRange(int id){
        int pageSize = Constants.pageSize;
        int start = pageSize + pageSize*id +1;
        return start;
    }


    private static int endRange(int startRange){
        return startRange + Constants.pageSize;
    }


    public boolean writePage(int id, byte[] page){
        try {
            ffile.seek(id* Utils.pageSize);
            ffile.write(page);
//            writer.write(page,0, Utils.pageSize);
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }


    public boolean writePage(Page page){
        return writePage(page.getHeader("id"), page.getPageData());
    }


    public Page readPage(int id){
        byte[] page = new byte[Utils.pageSize+1];
        try {
            ffile.seek(id*Utils.pageSize);
            ffile.read(page);
//            int a = reader.read(page,id*Utils.pageSize, Utils.pageSize);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
        return new Page(page,td);
    }

}