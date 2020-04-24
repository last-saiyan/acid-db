package Db.bufferManager;

import Db.catalog.TupleDesc;
import Db.diskManager.Page;
import Db.diskManager.DiskManager;

public class Manager {


    private Page[] bufferPool;
//use concurrent hashmap when supporting concurrency
    private PageMeta[] pageMapping ;
    public int size;
    private Replacer replacer;
    private DiskManager diskManager;

    public Manager(int size, TupleDesc td) throws Exception{
        diskManager = new DiskManager(td);
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
        diskManager.writePage(bufferPool[id]);
        pageMapping[id].dirty = false;
        pageMapping[id].pinCounter = 0;
    }


    /*
    *
    * reads page of given ID from Disk
    * */
    private Page readPageFromDisk(int pageId){
        return diskManager.readPage(pageId);
    }


    /*
    * this gets called when a query needs to use a page
    * pinned pages cant be evicted
    * check the pagemapping if the page is in bufferpool
    * else get from disk
    * */
    public int pinPage(int pId){
        int bfPoolInd = getBufferPoolPageInd(pId);
        if(bfPoolInd > 0){
            pageMapping[bfPoolInd].pinCounter++;
            return bfPoolInd;
        }else{
            int victimID = replacer.pickVictim();
            if(victimID<0){
                return -1;
            }
            if(pageMapping[victimID].dirty){
                flushPageToDisk(victimID);
//                bufferPool[victimID]
                pageMapping[victimID].pId = pId;
                pageMapping[victimID].pinCounter++;
                Page page = readPageFromDisk(pId);
                bufferPool[victimID] = page;
                replacer.update(pId);
                return victimID;
            }else{
                pageMapping[victimID].pId = pId;
                pageMapping[victimID].pinCounter++;
                Page page = readPageFromDisk(pId);
                bufferPool[victimID] = page;
                replacer.update(pId);
                return victimID;
            }
        }
    }


    /*
    *
    * returns a page
    * */
    public Page getPage(int pId){
        int bufferPoolInd = pinPage(pId);
        if(bufferPoolInd != -1){
            return bufferPool[bufferPoolInd];
        }else{
//            throw exception
        }
        return null;
    }

    public boolean isPagePinned(int id){
        if(pageMapping[getBufferPoolPageInd(id)].pinCounter == 0){
            return false;
        }else {
            return true;
        }
    }

    private int getBufferPoolPageInd(int pId){
        for(int i=0;i<pageMapping.length;i++){
            if(pId == pageMapping[i].pId)
                return i;
        }
        return -1;
    }


}

class PageMeta{
    public int pId;
    public int pinCounter = 0;
    public boolean dirty = false;
}
