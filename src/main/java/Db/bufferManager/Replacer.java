package Db.bufferManager;

abstract public class Replacer {

    Manager buffMan;

    public Replacer(Manager buffMan){
        this.buffMan = buffMan;
    }

    public abstract int updateEntry(int key);


    public abstract int pickVictim() ;

}
