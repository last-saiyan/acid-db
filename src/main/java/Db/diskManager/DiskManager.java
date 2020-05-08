package Db.diskManager;


import Db.Acid;
import Db.Constants;
import Db.Utils;
import Db.catalog.TupleDesc;

import java.io.*;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;

public class DiskManager implements Constants {
    private File file;
    RandomAccessFile ffile;
    private TupleDesc td;
    private Acid db;


    public DiskManager(String path,  TupleDesc td) throws FileNotFoundException{
        this.file = new File(path);
        this.td = td;
        ffile = new RandomAccessFile(file, "rws");
    }


    public DiskManager(Acid db) {
        this.db = db;

    }


    public boolean databaseExist(){


        return false;
    }


    public void createDatabase(String file) throws FileNotFoundException {
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