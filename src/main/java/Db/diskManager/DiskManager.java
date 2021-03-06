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
        return dbFile.isFile();
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
                    throw new RuntimeException("unable to create a db");
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


    public int dbSize(){
        if(db.dbPageCount == -1) {
            if (file.length() % Utils.pageSize == 0) {
                return (int) file.length() / Utils.pageSize;
            }
            throw new RuntimeException("some thing is wrong with page size ");
        }else {
            return db.dbPageCount;
        }
    }

    public Page getNewPage(){
        int pageId = db.dbPageCount;
        db.dbPageCount++;
        return new Page(pageId, db.tupleDesc);
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
        return writePage(page.pageID(), page.getPageData());
    }


    /*
    * todo fix if page is greater than dbsize
    * */
    public Page readPage(int id){
        if(id >= dbSize()){
            throw new RuntimeException("accessing page not present in db");
        }
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