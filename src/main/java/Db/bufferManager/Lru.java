package Db.bufferManager;

import Db.Utils;

public class Lru extends Replacer {


    int nframes;
    int frames[];

    public Lru(Manager buffMan){
        super(buffMan);
        nframes = 0;
    }
    private void update(int frameNo) {
        int index;
        for ( index=0; index < nframes; ++index )
            if ( frames[index] == frameNo )
                break;
        while ( ++index < nframes )
            frames[index-1] = frames[index];
        frames[nframes-1] = frameNo;
    }

    @Override
    public int pickVictim(){
        int numBuffers = Utils.bfPoolsize;
        int frame;
        if ( nframes < numBuffers ) {
            frame = nframes++;
            frames[frame] = frame;
            buffMan.pinPage(frame);
            return frame;
        }
        for ( int i = 0; i < numBuffers; ++i ) {
            frame = frames[i];
            if ( !buffMan.isPagePinned(frame)) {
                buffMan.pinPage(frame);
                update(frame);
                return frame;
            }
        }
        return -1;
    }
}
