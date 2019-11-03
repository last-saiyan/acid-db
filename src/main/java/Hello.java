import java.io.*;

import java.nio.*;
import java.nio.channels.FileChannel;;


public class Hello {

    public static void main(String[] args) throws Exception {

        String s = new String();
        File file = new File("./abc.txt");


        FileChannel fc = new FileInputStream(file).getChannel();

        ByteBuffer buf = ByteBuffer.allocate(48);

        fc = fc.position(2);

        int bytesRead = fc.read(buf);

        System.out.println(buf.limit());

        buf.flip();

        while(buf.hasRemaining()){
            System.out.println((char) buf.get());
        }

        System.out.println( " - "+buf.position());




//        FileWriter fw = new FileWriter(file);
//        BufferedWriter bw = new BufferedWriter(fw);
//
//        bw.write("b hb hbj j");
//        bw.close();

        System.out.println("\nasdfasdf "+ bytesRead);
    }
}
