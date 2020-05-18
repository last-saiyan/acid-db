package Db.bufferManager;

import Db.Acid;
import Db.Utils;
import Db.catalog.Tuple;
import Db.diskManager.Page;
import Db.diskManager.DiskManager;

public class Manager {


    private Page[] bufferPool;
//use concurrent hashmap when supporting concurrency
    private PageMeta[] pageMapping ;
    public int size;
    private Replacer replacer;
    private DiskManager diskManager;

    public Manager(DiskManager diskManager) {
        this.diskManager = diskManager;
        replacer = new Lru(this);
        this.size = Utils.bfPoolsize;
        bufferPool = new Page[size];
        pageMapping = new PageMeta[size];
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
    * it returns the buffer pool index of the pinned page
    *  needs to use a page using getPage(int id)
    * pinned pages cant be evicted
    * check the pageMapping if the page is in bufferPool
    * else get from disk
    * */
    public int pinPage(int pId) {
        int bfPoolInd = getBufferPoolPageInd(pId);
        if(bfPoolInd >= 0){
            pageMapping[bfPoolInd].pinCounter++;
            replacer.updateEntry(bfPoolInd);
            return bfPoolInd;
        }else{
//            this line throws exception
//            if all pages in buffer pool is pinned
            int victimID = replacer.pickVictim();
            if(pageMapping[victimID].dirty){
                flushPageToDisk(victimID);
                pageMapping[victimID].pId = pId;
                pageMapping[victimID].pinCounter++;
                Page page = readPageFromDisk(pId);
                bufferPool[victimID] = page;
                replacer.updateEntry(pId);
                return victimID;
            }else{
                pageMapping[victimID].pId = pId;
                pageMapping[victimID].pinCounter++;
                Page page = readPageFromDisk(pId);
                bufferPool[victimID] = page;
                replacer.updateEntry(pId);
                return victimID;
            }
        }
    }

    /*
    * this is called to unpin the page
    * when query is done using the page
    * page has to be un pinned
    *
    * */
    public void unPinPage(int pId){
        int bfPoolId = getBufferPoolPageInd(pId);
        if(bfPoolId != -1){
            pageMapping[bfPoolId].pinCounter--;
        }else {
            throw new ArrayIndexOutOfBoundsException("trying to unpin page that is not in buffer pool");
        }
    }

    /*
    * a record can be inserted into any page
    * that has memory
    * search buffer pool for page with empty slot
    * if none is found create a new page (need think about this)
    *
    * */

    public void insertTuple(Tuple tuple){
        int i = 0;
        Page tempPage = bufferPool[0];

        boolean found = false;
        while (i < bufferPool.length){
            tempPage = bufferPool[i];
            if(tempPage != null && tempPage.pageDataCapacity < tuple.size() + tempPage.pageSize()){
                replacer.updateEntry(i);
                found = true;
                break;
            }
            i++;
        }
        if(found == true){
            tempPage = bufferPool[i];
        }else {
            tempPage = insertNewPage();
        }
        tempPage.insertTuple(tuple);

    }



    /*
    * creates new page adds it into bufferPool
    * throws exception when there is no memory
    * in the bufferPool (all pages are filled)
    *
    * */
    public Page insertNewPage(){
        int buffPoolInd = replacer.pickVictim();

        Page page = diskManager.getNewPage();
        if(pageMapping[buffPoolInd]!= null && pageMapping[buffPoolInd].dirty){
            flushPageToDisk(buffPoolInd);
        }
        bufferPool[buffPoolInd] = page;
        pageMapping[buffPoolInd].pinCounter++;
        pageMapping[buffPoolInd].pId = page.getHeader("id");

        replacer.updateEntry(buffPoolInd);
        return bufferPool[buffPoolInd];
    }


    /*
    *
    * returns a page
    * with the given page ID
    * checks if its in the bufferpool
    * else tries to fetch the page from disk
    * if all pages in bufferpool is pinned
    * it throws a exception
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


    public boolean isPagePinned(int bufferPoolInd){
        if(pageMapping[getBufferPoolPageInd(bufferPoolInd)].pinCounter == 0){
            return false;
        }else {
            return true;
        }
    }


    /*
    * for a page with given page id
    * it returns the position
    * of the page in bufferPool
    * */
    private int getBufferPoolPageInd(int pId){
        for(int i=0; i<pageMapping.length; i++){
            if(pageMapping[i]!= null  && pId == pageMapping[i].pId) {
                return i;
            }
        }
        return -1;
    }


}

class PageMeta{
    public int pId = -1;
    public int pinCounter = 0;
    public boolean dirty = false;
}
