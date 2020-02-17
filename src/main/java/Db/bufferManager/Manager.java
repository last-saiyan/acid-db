package Db.bufferManager;

import Db.DiskManager;
import Db.Page;
import Db.Utils;

import java.util.HashMap;

public class Manager {


    private Page[] bufferPool;
//use concurrent hashmap when supporting concurrency
    private PageMeta[] pageMapping ;



    private int getPage(int id){
        for(int i=0;i<pageMapping.length;i++){
            if(id == pageMapping[i].id)
                return i;
        }
        return -1;
    }


    private Replacer replacer;

    public Manager(int size){
        bufferPool = new Page[size];
        pageMapping = new PageMeta[size];
        replacer = new Lru(this);
        for(int i=0;i<size;i++){
            pageMapping[i] = new PageMeta();
        }
    }

    private void flushPageToDisk(int id){
//        if the page is dirty the page has to be flushed to the disk

        int pageInd = getPage(id);
        pageMapping[pageInd].dirty = false;

    }

    private void discardPage(int id){

    }

    public void pinPage(int id){
//        this gets called when a query needs to use a page
//        pinned pages cant be evicted
//        check the pagemapping if the page is in bufferpool else get from disk

        int pageInd = getPage(id);

        if(pageInd > 0){
//            present in bufferpool
            pageMapping[pageInd].pinCounter++;
        }else{
//            not in bufferpool
            int victimID = replacer.pickVictim();
            if(victimID<0){
//                throw exception
            }

            if(pageMapping[victimID].dirty){
//                write dirty page to disk
                flushPageToDisk(victimID);
            }else{

                pageMapping[victimID] =

            }

        }
    }

    public boolean isPagePinned(int id){
        if(pageMapping[getPage(id)].pinCounter == 0){
            return false;
        }else {
            return true;
        }
    }



    private void evictPage(){
//        when the buffer manager does not have any more space for new page
//        this function is called to evict page

    }






}

class PageMeta{
    public int id;
    public int pinCounter = 0;
    public boolean dirty = false;
}
