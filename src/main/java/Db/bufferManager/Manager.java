package Db.bufferManager;

import Db.Acid;
import Db.Tx.LogRecord;
import Db.Tx.Permission;
import Db.Tx.Transaction;
import Db.Utils;
import Db.catalog.Tuple;
import Db.diskManager.Page;
import Db.diskManager.DiskManager;
import Db.diskManager.PageHeaderEnum;

import java.io.IOException;
import java.util.Iterator;
import java.util.Set;

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
        if(diskManager.dbSize() <= pageId){
            return diskManager.getNewPage();
        }
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

    public void insertTuple(Tuple tuple, Transaction tx) throws InterruptedException, IOException {
        int i = 0;
        Page tempPage = bufferPool[0];

        boolean found = false;
//        check if transaction holds exclusive lock on page and
//        page has space and page is in buffer pool

        Set<Integer> pageIDSet = tx.getPagesXLocked();
        Iterator<Integer> pageIDSetIterator = pageIDSet.iterator();

        while (pageIDSetIterator.hasNext()){
            i = getBufferPoolPageInd(pageIDSetIterator.next());
            if((i != -1) &&
                    (bufferPool[i].pageDataCapacity < tuple.size() + tempPage.pageSize())
            ){
                replacer.updateEntry(i);
                found = true;
                break;
            }
        }
        i = 0;

        while (!found && i < bufferPool.length){
            tempPage = bufferPool[i];
            if(
                    (tempPage != null) &&
                    (tempPage.pageDataCapacity > tuple.size() + tempPage.pageSize()) &&
                    tx.canLockPage(tempPage.pageID(), Permission.EXCLUSIVE)
            ){
                replacer.updateEntry(i);
                found = true;
                break;
            }
            i++;
        }

        if(found == true){
            tempPage = bufferPool[i];
        }else {
            tempPage = insertNewPage(tx);
        }
        LogRecord insertLogRecord = new LogRecord(tx.getPrevLsn(),
                LogRecord.LogType.UPDATE, null, tuple.getBytes(),
                tempPage.getHeader(PageHeaderEnum.ID),
                tx.getTID(),
                (tempPage.getHeader(PageHeaderEnum.SIZE)/tuple.getBytes().length)
        );
        int lsn = tx.addLogRecord(insertLogRecord);
        tempPage.setLsn(lsn);
        tempPage.insertTuple(tuple);
    }



    /*
    * creates new page adds it into bufferPool
    * throws exception when there is no memory
    * in the bufferPool (all pages are filled)
    *
    * */
    public synchronized Page insertNewPage(Transaction tx) throws InterruptedException, IOException {
        int buffPoolInd = replacer.pickVictim();

        Page page = diskManager.getNewPage();
        if(pageMapping[buffPoolInd]!= null && pageMapping[buffPoolInd].dirty){
            flushPageToDisk(buffPoolInd);
        }

        bufferPool[buffPoolInd] = page;
        pageMapping[buffPoolInd].pinCounter++;
        pageMapping[buffPoolInd].pId = page.pageID();
        tx.lockPage(page.pageID(), Permission.EXCLUSIVE);

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
    public Page getPage(int pId, Transaction tx, Permission perm) throws InterruptedException, IOException {
        tx.lockPage(pId, perm);
//        below lines of code will not be executed if lock is not obtained

        int bufferPoolInd = pinPage(pId);
        if(bufferPoolInd != -1){
            return bufferPool[bufferPoolInd];
        }else{
            throw new RuntimeException("all pages in buffer pool is pinned");
        }
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
