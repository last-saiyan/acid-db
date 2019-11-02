package Db.Pages;

import Db.Tuples.Tuple;
import Db.Utils;

public interface Page extends Utils {


    public boolean addTuple(Tuple tp);

    public boolean canAddTuple(Tuple tp);

    public int nextIndex = 0;






}
