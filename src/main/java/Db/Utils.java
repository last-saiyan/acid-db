package Db;

public interface Utils {

    public int pageSize = 100;


    public static byte[] stringToByte(String str, int lenght){
        // support other encoding
        // if ascii value exceeds lenght throw exception

        return null;

    }

    public static byte[] intToByte(int integer , int lenght){

        // if byte value exceeds lenght throw exception
        return null;
    }

    public static String padding(int val, int digits){
        // if digits exceeds in int exceeds value of digits
        // throw exception
        String strFormat = "%0" + String.valueOf(digits) + "d";
        return String.format(strFormat, val);
    }

}
