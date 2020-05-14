package Db;
import java.lang.reflect.Array;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;

public interface Utils {

    public int pageSize = 100;

    public int bfPoolsize = 100;

    public int port = 8081;

    public String dbFolderPath = "./dbFile";

    public static byte[] stringToByte(String str, int bytes){
        // does this support other encoding?
        byte [] value = str.getBytes(StandardCharsets.US_ASCII);
        if(value.length < bytes){
            byte [] padding = new byte[(bytes - value.length)];
            byte[] paddedValue = new byte[padding.length + value.length];
            System.arraycopy(padding, 0, paddedValue, 0, padding.length);
            System.arraycopy(value, 0, paddedValue, padding.length, value.length);
            return paddedValue;
        }if(value.length == bytes){
            return value;
        }else{
            System.out.println("array overflow");
            throw new ArrayStoreException("string exceeds length");
        }
    }

    public static byte[] mergeByteArr(byte[] arr1, byte[] arr2){

        byte[] result = new byte[arr1.length+arr2.length];


//        System.arraycopy(b,zeroInd, value, 0,value.length);
        return null;
    }

    public static String byteToString(byte [] b){
        byte zero = 0; int zeroInd = 0;
        for(zeroInd = 0 ; zeroInd < b.length ; zeroInd++){
            if(b[zeroInd] != zero)
                break;
        }
        byte[] value = new byte[(b.length-zeroInd)];
        System.arraycopy(b,zeroInd, value, 0,value.length);
        return new String(value);
    }

    public static int byteToInt(byte[] b){
        return ByteBuffer.wrap(b).getInt();
    }
    public static byte[] intToByte(int value){
        return ByteBuffer.allocate(4).putInt(value).array();
    }


    public static byte[] floatToByte(float value){
        return ByteBuffer.allocate(8).putFloat(value).array();
    }
    public static float byteToFloat(byte[] b){
        return ByteBuffer.wrap(b).getFloat();
    }

}