package Tx;

import Db.Tx.LockTable;
import Db.Tx.Permission;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class LockTableTest {

    @Test
    public void testGrantLock(){
        LockTable locks = new LockTable();
        boolean b;

        b = locks.grantLock(1, 1, Permission.SHARED);
        Assertions.assertEquals(b, true);

        b = locks.grantLock(1, 1, Permission.EXCLUSIVE);
        Assertions.assertEquals(b, true);

        b = locks.grantLock(0, 0, Permission.EXCLUSIVE);
        Assertions.assertEquals(b, true);

        b = locks.grantLock(0, 0, Permission.EXCLUSIVE);
        Assertions.assertEquals(b, true);

        b = locks.grantLock(0, 0, Permission.SHARED);
        Assertions.assertEquals(b, true);

        b = locks.grantLock(0, 1, Permission.EXCLUSIVE);
        Assertions.assertEquals(b, false);

        b = locks.grantLock(0, 1, Permission.SHARED);
        Assertions.assertEquals(b, false);

    }

    @Test
    public void testCanLockPage(){
        LockTable locks = new LockTable();
        boolean b;

        b = locks.canLockPage(1, 1, Permission.SHARED);
        Assertions.assertEquals(b, true);

        b = locks.canLockPage(1, 1, Permission.EXCLUSIVE);
        Assertions.assertEquals(b, true);


        b = locks.canLockPage(0, 0, Permission.EXCLUSIVE);
        Assertions.assertEquals(b, true);

        b = locks.canLockPage(0, 0, Permission.EXCLUSIVE);
        Assertions.assertEquals(b, true);

        b = locks.canLockPage(0, 1, Permission.EXCLUSIVE);
        Assertions.assertEquals(b, false);


    }


    @Test
    public void detectDeadLock(){

        LockTable locks = new LockTable();

        locks.grantLock(1, 0, Permission.EXCLUSIVE);
        locks.grantLock(2, 1, Permission.EXCLUSIVE);
        locks.grantLock(1, 1, Permission.EXCLUSIVE);
        Assertions.assertEquals(locks.detectDeadLock(1), false);
        locks.grantLock(2, 0, Permission.EXCLUSIVE);
        Assertions.assertEquals(locks.detectDeadLock(1), true);

        locks = new LockTable();

        locks.grantLock(1, 1, Permission.SHARED);
        locks.grantLock(2, 2, Permission.SHARED);
        locks.grantLock(1, 2, Permission.EXCLUSIVE);
        Assertions.assertEquals(locks.detectDeadLock(1), false);
        locks.grantLock(2, 1, Permission.EXCLUSIVE);
        Assertions.assertEquals(locks.detectDeadLock(1), true);


    }
}
