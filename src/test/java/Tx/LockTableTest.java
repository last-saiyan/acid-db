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
}
