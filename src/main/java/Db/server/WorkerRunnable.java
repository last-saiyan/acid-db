package Db.server;

import Db.Acid;
import Db.query.CreateInit;
import Db.query.Executor;
import Db.query.Planner;
import Db.query.Query;

import java.io.*;
import java.net.Socket;
import java.nio.file.FileAlreadyExistsException;


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

            String queryString = "{\"type\": \"init\",\"database\": \"dbname\"}";
            String tempString ;
            while((tempString = bufferedReader.readLine()) != null){
                tempString = tempString.trim();
                queryString = queryString + tempString;

                if( !tempString.equals("") && tempString.charAt(tempString.length()-1) == ';'){
                    System.out.println("new query");
                    queryString = queryString.substring(0, queryString.length()-1);
                    Acid db = Acid.getDatabase();

                    Query query = new Query(queryString, db.tupleDesc);
                    queryString = "";

                    try {
                        if(query.getQuery().type.equals("init")){
                            CreateInit init = new CreateInit(db,query.getQuery());
                            init.handleInit();
                            output.write("initiliazed database".getBytes());
                        }
                        if(query.getQuery().type.equals("create")){
                            CreateInit create = new CreateInit(db,query.getQuery());
                            create.handleCreate();
                            output.write("created database".getBytes());
                        }
                    }catch (FileAlreadyExistsException e){
                        output.write("database already present".getBytes());
                        e.printStackTrace();
                    }catch (FileNotFoundException e){
                        output.write("database not present".getBytes());
                        e.printStackTrace();
                    }

                    if(!(query.getQuery().type.equals("init") || query.getQuery().type.equals("create"))) {
                        Planner planner = new Planner(query.getQuery(), query.getPredicate());

                        Executor executor = new Executor(planner.getplan(), output);

                        executor.run();
                    }
                }

            }
            System.out.println("connection closed");

        } catch (IOException | ClassNotFoundException e) {
            //report exception somewhere.
            e.printStackTrace();
        }

    }
}
