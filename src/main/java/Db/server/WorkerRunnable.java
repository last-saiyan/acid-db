package Db.server;

import Db.Acid;
import Db.Tx.Transaction;
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
            Transaction tx = null;
            String responseString = "\nconnected to server - " + time + "\n";
            output.write(responseString.getBytes());

            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(input), 1024);

            String queryString = "";
            String tempString ;
            while((tempString = bufferedReader.readLine()) != null){
                tempString = tempString.trim();
                queryString = queryString + tempString;

                if( !tempString.equals("") && tempString.charAt(tempString.length()-1) == ';'){
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
                        if(query.getQuery().type.equals("transaction")){
                            if(query.getQuery().transaction.equals("begin")){
                                if(tx == null){
                                    tx = new Transaction(true);
                                }else {
                                    output.write("transaction is already running".getBytes());
                                }
                            }else if(query.getQuery().transaction.equals("commit")){
                                if(tx == null){
                                    output.write("no transaction is running".getBytes());
                                }else {
                                    tx.commit();
                                    tx = null;
                                }
                            }
                            else if(query.getQuery().transaction.equals("abort")){

                                if(tx == null){
                                    output.write("no transaction is running".getBytes());
                                }else {
                                    tx.abort();
                                    tx = null;
                                }
                            }
                        }
                    }catch (FileAlreadyExistsException e){
                        output.write("database already present".getBytes());
                        e.printStackTrace();
                    }catch (FileNotFoundException e){
                        output.write("database not present".getBytes());
                        e.printStackTrace();
                    }

                    if(!( query.getQuery().type.equals("init") || query.getQuery().type.equals("transaction") || query.getQuery().type.equals("create"))) {
                        if(tx == null){
                            tx = new Transaction(false);
                        }

                        Planner planner = new Planner(query.getQuery(), query.getPredicate(), tx);

//                        individual query has to be treated as a transaction

                        Executor executor = new Executor(planner.getplan(), output);
                        executor.run();

                        if(!tx.isExplicit()){
                            tx.commit();
                            tx = null;
                        }

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
