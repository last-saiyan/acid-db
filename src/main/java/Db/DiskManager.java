package Db;


import java.io.File;
import java.io.FileInputStream;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;

public class DiskManager implements Constants {


    DiskManager() throws Exception{
        this.file = new File("../abc.txt");

        FileChannel fc = new FileInputStream(file).getChannel();

    }

    private File file;


    private static int startRange(int id){
        int pageSize = Constants.pageSize;
        int start = pageSize + pageSize*id +1;
        return start;
    }

    private static int endRange(int startRange){
        return startRange + Constants.pageSize;
    }


    private static ByteBuffer getPage(int id){
        int startMemory = startRange(id);
        int endMemory = endRange(startMemory);

        return null;
    }

    private static boolean writePage(int id){

        return false;
    }


}
