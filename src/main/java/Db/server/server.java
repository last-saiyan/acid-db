package Db.server;

import java.net.ServerSocket;
import java.net.Socket;
import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Server implements Runnable{


    protected int          serverPort   = 8080;
    protected ServerSocket serverSocket = null;
    protected boolean      isStopped    = false;
    protected Thread       runningThread= null;
    protected ExecutorService threadPool = Executors.newFixedThreadPool(10);

    public Server(int port){
        this.serverPort = port;
    }

    public void run(){
//        synchronized(this){
//            this.runningThread = Thread.currentThread();
//        }
        openServerSocket();
        while(true){
            if(isStopped){
                break;
            }
            Socket clientSocket = null;
            try {
                clientSocket = this.serverSocket.accept();
            } catch (IOException e) {
//                if(isStopped()) {
//                    System.out.println("Server Stopped.") ;
//                    break;
//                }
                throw new RuntimeException("Error accepting client connection", e);
            }
            this.threadPool.execute(new WorkerRunnable(clientSocket));
        }
        this.threadPool.shutdown();
        System.out.println("Server Stopped.") ;
    }


//    private synchronized boolean isStopped() {
//        return this.isStopped;
//    }

    public synchronized void stop(){
        this.isStopped = true;
        try {
            this.serverSocket.close();
        } catch (IOException e) {
            throw new RuntimeException("Error closing server", e);
        }
    }

    private void openServerSocket() {
        try {
            this.serverSocket = new ServerSocket(this.serverPort);
        } catch (IOException e) {
            throw new RuntimeException("Cannot open port 8080", e);
        }
    }
}