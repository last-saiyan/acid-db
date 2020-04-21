package Db.bufferManager;

import Db.diskManager.Page;

public class Manager {


    private Page[] bufferPool;
//use concurrent hashmap when supporting concurrency
    private PageMeta[] pageMapping ;
    public int size;


    private int getPageInd(int pId){
        for(int i=0;i<pageMapping.length;i++){
            if(pId == pageMapping[i].pId)
                return i;
        }
        return -1;
    }


    private Replacer replacer;

    public Manager(int size){
        bufferPool = new Page[size];
        pageMapping = new PageMeta[size];
        replacer = new Lru(this);
        this.size = size;
        for(int i=0;i<size;i++){
            pageMapping[i] = new PageMeta();
        }
    }

    /*
    * if the page is dirty the page
    * has to be flushed to the disk
    *
    * */

    private void flushPageToDisk(int id){
//        int pageInd = getPageInd(id);
        pageMapping[id].dirty = false;
        pageMapping[id].pinCounter = 0;
        discardPage(id);
    }

    private void discardPage(int id){


    }

    public void pinPage(int pId){
//        this gets called when a query needs to use a page
//        pinned pages cant be evicted
//        check the pagemapping if the page is in bufferpool else get from disk

        int pageInd = getPageInd(pId);

        if(pageInd > 0){
//            present in bufferpool
            pageMapping[pageInd].pinCounter++;
        }else{
//            not in bufferpool
            int victimID = replacer.pickVictim();
            if(victimID<0){
//                need to wait till queries get executed and pages to flushed
//                throw exception
            }

            if(pageMapping[victimID].dirty){
//                write dirty page to disk
                flushPageToDisk(victimID);
                pageMapping[victimID].pId = pId;
                pageMapping[victimID].pinCounter++;
                replacer.update(pId);
            }else{
                pageMapping[victimID].pId = pId;
                pageMapping[victimID].pinCounter++;
                replacer.update(pId);
            }

        }
    }

    public boolean isPagePinned(int id){
        if(pageMapping[getPageInd(id)].pinCounter == 0){
            return false;
        }else {
            return true;
        }
    }



    private void evictPage(int id){

//        when the buffer manager does not have any more space for new page
//        this function is called to evict page

    }






}

class PageMeta{
    public int pId;
    public int pinCounter = 0;
    public boolean dirty = false;
}
