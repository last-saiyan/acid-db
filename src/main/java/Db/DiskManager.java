package Db;


import java.io.*;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;

public class DiskManager implements Constants {
    private File file;

    private FileOutputStream writer;
    private FileInputStream reader;

    public DiskManager() throws Exception{
        this.file = new File("../abc.db");

//        FileChannel fc = new FileOutputStream(file).getChannel();

        writer = new FileOutputStream(file, true);
        reader = new FileInputStream(file);

    }


    private static int startRange(int id){
        int pageSize = Constants.pageSize;
        int start = pageSize + pageSize*id +1;
        return start;
    }

    private static int endRange(int startRange){
        return startRange + Constants.pageSize;
    }


    public static ByteBuffer getPage(int id){
        int startMemory = startRange(id);
        int endMemory = endRange(startMemory);

        return null;
    }

    public boolean writePage(int id, byte[] page){
        try {
            writer.write(page,0, Utils.pageSize);
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    public byte[] readPage(int id){
        byte[] page = new byte[Utils.pageSize+1];
        try {
            int a = reader.read(page,0, Utils.pageSize);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
        return page;
    }

}
