package Db;

public interface Utils {

    public int pageSize = 16000;



    public static String padding(int val, int digits){
        // if digits exceeds in int exceeds value of digits
        // throw exception
        String strFormat = "%0" + String.valueOf(digits) + "d";
        return String.format(strFormat, val);
    }

}
