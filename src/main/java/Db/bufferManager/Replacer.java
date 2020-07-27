package Db.bufferManager;

abstract public class Replacer {

    Manager buffMan;

    public Replacer(Manager buffMan){
        this.buffMan = buffMan;
    }

    public abstract int updateEntry(int key);


//    todo check last committed lsn before picking victim
    public abstract int pickVictim() ;

}
