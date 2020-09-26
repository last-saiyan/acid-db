package Db.server;

import Db.Acid;
import Db.Tx.Transaction;
import Db.query.*;

import java.io.*;
import java.net.Socket;
import java.nio.file.FileAlreadyExistsException;
import java.util.logging.Level;
import java.util.logging.Logger;


public class WorkerRunnable implements Runnable {

    private static final Logger logger =
            Logger.getLogger(WorkerRunnable.class.getName());
    private Socket clientSocket;
    private Transaction tx;

    WorkerRunnable(Socket clientSocket) {
        this.clientSocket = clientSocket;
    }

    @Override
    public void run() {
        try {
            ServerIO io = new ServerIO(clientSocket);

            tx = null;

            io.write("\nconnected to server - " + System.currentTimeMillis());
            InputStream input  = clientSocket.getInputStream();
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(input), 1024);

            StringBuilder queryString = new StringBuilder();
            String tempString ;
            while((tempString = bufferedReader.readLine()) != null){

                tempString = tempString.trim();
                queryString.append(tempString);

                if( !tempString.equals("") && tempString.charAt(tempString.length()-1) == ';'){
                    queryString.deleteCharAt(queryString.length() -1);
                    Acid db = Acid.getDatabase();
                    Query query = new Query(queryString.toString(), db.tupleDesc);
                    queryString.delete(0, queryString.length());

                    logger.log(Level.INFO, "new request , {0}" , query.getQuery().type);

                    try {
                        handleQueries(query, db, io);
                    }catch (FileAlreadyExistsException e){
                        io.write("database already present");
                        logger.log(Level.INFO, e.toString());
                    }catch (FileNotFoundException e){
                        io.write("database not present");
                        logger.log(Level.INFO, e.toString());
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }

            }
            logger.log(Level.INFO, "connection closed");

        } catch (IOException | ClassNotFoundException e) {
            //report exception somewhere.
            logger.log(Level.INFO, e.toString());
        }

    }



    private void handleQueries(Query query, Acid db, ServerIO io) throws InterruptedException, IOException, ClassNotFoundException {

        if(query.getQuery().type.equals("init") || query.getQuery().type.equals("create")){
            CreateInit init = new CreateInit(db, query.getQuery());
            init.execute();
            io.write( query.getQuery().type + " database " + query.getQuery().database);
            return;
        }

        if(query.getQuery().type.equals("transaction")){
            if(query.getQuery().transaction.equals("begin")){
                if(tx == null){
                    tx = new Transaction(true);
                }else {
                    io.write("transaction is already running");
                }
            }

            if(tx == null){
                io.write("no transaction is running");
                return;
            }

            if(query.getQuery().transaction.equals("commit")){
                tx.commit();
                tx = null;
            }
            else if(query.getQuery().transaction.equals("rollback")){
                tx.abort();
                tx = null;
            }
            return;
        }

        if(tx == null){
            tx = new Transaction(false);
        }

        Planner planner = new Planner(query.getQuery(), query.getPredicate(), tx);
        Executor executor = new Executor(planner.getplan(), io);
        executor.run();

        if(!tx.isExplicit()){
            tx.commit();
            tx = null;
        }
    }


}
