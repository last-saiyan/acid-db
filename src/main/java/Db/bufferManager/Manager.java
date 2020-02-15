package Db.bufferManager;

import Db.Page;
import Db.Utils;

import java.util.HashMap;

public class Manager {


    private Page[] bufferPool;
//use concurrent hashmap when supporting concurrency
    private HashMap<Integer,PageMeta> pageMapping ;



    public Manager(int size){
        bufferPool = new Page[size];
        pageMapping = new HashMap();

    }

    private void flushPageToDisk(int id){
//        if the page is dirty the page has to be flushed to the disk



    }

    public void pinPage(int id){
//        this gets called when a query needs to use a page
//        pinned pages cant be evicted
//        check the pagemapping if the page is in bufferpool else get from disk


    }

    private void evictPage(){
//        when the buffer manager does not have any more space for new page
//        this function is called to evict page

    }






}

class PageMeta{
    public int id;
    public int pinCounter;
    public boolean dirty;
}
