package Db.Pages;

import Db.Tuples.Tuple;
import Db.Utils;

public interface Page extends Utils {




    public boolean canAddTuple(Tuple tp);

    public int nextIndex = 0;

    public boolean isDirty = false;



    public boolean addTuple(Tuple tp);

    public boolean deleteTuple(int id);

    public boolean updateTuple(int id, Tuple tp);


}
