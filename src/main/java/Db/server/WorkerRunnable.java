package Db.server;

import java.io.*;
import java.net.Socket;


public class WorkerRunnable implements Runnable {

    protected Socket clientSocket = null;

    public WorkerRunnable(Socket clientSocket) {
        this.clientSocket = clientSocket;
    }

    @Override
    public void run() {
        try {
            InputStream input  = clientSocket.getInputStream();
            OutputStream output = clientSocket.getOutputStream();
            long time = System.currentTimeMillis();

            String responseString = "connected to server - " + time;
            output.write(responseString.getBytes());

            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(input), 1024);

            String tempString = null;
            while((tempString = bufferedReader.readLine()) != null){
                System.out.println(tempString);
                if(clientSocket.isClosed()){
                    System.out.println("closed");
                }
            }
            System.out.println("connection closed");

        } catch (IOException e) {
            //report exception somewhere.
            e.printStackTrace();
        }

    }
}
