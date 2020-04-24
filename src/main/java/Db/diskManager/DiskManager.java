package Db.diskManager;


import Db.Constants;
import Db.Utils;

import java.io.*;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;

public class DiskManager implements Constants {
    private File file;
    private FileOutputStream writer;
    private FileInputStream reader;
    private RandomAccessFile ffile;

    public DiskManager() throws Exception{
        this.file = new File("../abc.db");

        writer = new FileOutputStream(file, true);
        reader = new FileInputStream(file);
        ffile = new RandomAccessFile(file, "rws");
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

    public byte[] readPage(int id){
        byte[] page = new byte[Utils.pageSize+1];
        try {
            ffile.seek(id*Utils.pageSize);
            ffile.read(page);
//            int a = reader.read(page,id*Utils.pageSize, Utils.pageSize);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
        return page;
    }

}