package Db.server;

import Db.Query.Executor;
import Db.Query.Planner;
import Db.Query.Query;
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
        try {
            InputStream input  = clientSocket.getInputStream();
            OutputStream output = clientSocket.getOutputStream();
            long time = System.currentTimeMillis();

            String responseString = "\nconnected to server - " + time + "\n";
            output.write(responseString.getBytes());

            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(input), 1024);

            String queryString = "";
            String tempString ;
            while((tempString = bufferedReader.readLine()) != null){
                tempString = tempString.trim();

                queryString = queryString + tempString;
                if(tempString.charAt(tempString.length()-1) == ';'){
                    Query query = new Query(queryString);

                    Planner planner = new Planner(query.getQuery(), query.getPredicate());

                    Executor executor = new Executor(planner.getplan(), output);

                    executor.run();

                }

            }
            System.out.println("connection closed");

        } catch (IOException e) {
            //report exception somewhere.
            e.printStackTrace();
        }

    }
}
