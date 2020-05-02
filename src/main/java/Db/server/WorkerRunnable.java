package Db.server;

import com.google.gson.Gson;

import java.io.*;
import java.net.Socket;


public class WorkerRunnable implements Runnable {

    protected Socket clientSocket = null;

    public WorkerRunnable(Socket clientSocket) {
        this.clientSocket = clientSocket;
    }

    @Override
    public void run() {
        Gson gson = new Gson();
        try {
            InputStream input  = clientSocket.getInputStream();
            OutputStream output = clientSocket.getOutputStream();
            long time = System.currentTimeMillis();

            String responseString = "\nconnected to server - " + time + "\n";
            output.write(responseString.getBytes());

            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(input), 1024);

            String queryString = "";
            String tempString = null;
            while((tempString = bufferedReader.readLine()) != null){
                queryString = queryString + tempString;


//                    if(Query.isValid(queryString)){
//                        Query query = gson.fromJson(queryString, Query.class);
//
//                        System.out.println(query.columns);
//                        System.out.println(query.columns);
//                    }else{
//                        System.out.println("type");
//                    }



                if(tempString.equals('\n')){
                    System.out.println("enter");
                }

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
