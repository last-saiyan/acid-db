package Db.bufferManager;

abstract public class Replacer {

    Manager buffMan;

    public Replacer(Manager buffMan){
        this.buffMan = buffMan;
    }

    public abstract int pickVictim();

    public abstract void update(int frameNo);

}
