package Db.bufferManager;

import Db.Utils;
import java.util.Deque;
import java.util.LinkedList;
import java.util.HashSet;
import java.util.Iterator;

public class Lru extends Replacer {

    int nframes;
    Deque<Integer> frames;
    static HashSet<Integer> map;

    public Lru(Manager buffMan){
        super(buffMan);
        map = new HashSet<>();
        nframes = Utils.bfPoolsize;
        frames = new LinkedList<>();
        int size = 0;
    }

    @Override
    public void update(int frameNo) {
        if (!map.contains(frameNo) && (frames.size() == nframes)) {
//                throw exception
//                need to pick a victim and then update the
        }else{
            /* The found page may not be always the last element, even if it's an
               intermediate element that needs to be removed and added to the start
               of the Queue */
            int index = -1, i = 0;
            Iterator<Integer> itr = frames.iterator();
            while (itr.hasNext()) {
                if (itr.next() == frameNo) {
                    index = i;
                    break;
                }
                i++;
            }
            if(index!=-1){
                frames.remove(index);
            }
            frames.push(frameNo);
            map.add(frameNo);
        }
    }

    @Override
    public int pickVictim(){
        int numBuffers = Utils.bfPoolsize;
        int frame;
        if ( frames.size() < numBuffers ) {
            frame = nframes++;
            return frame;
        }
        Iterator<Integer> itr = frames.iterator();
        while (itr.hasNext()) {
            int pid = itr.next();
            if(!buffMan.isPagePinned(pid)){
                map.remove(pid);
                frames.remove(pid);
                return pid;
            }
        }
        return -1;
    }
}
