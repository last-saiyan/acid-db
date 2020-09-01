package Db.server;

import java.io.*;
import java.net.Socket;

public class ServerIO {

    private Socket clientSocket;

    public ServerIO(Socket clientSocket) {
        this.clientSocket = clientSocket;
    }

    public void write(String message) throws IOException {
        OutputStream output = clientSocket.getOutputStream();
        output.write((message + "\n").getBytes());
    }

    protected String read() throws IOException {
        InputStream input  = clientSocket.getInputStream();
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(input), 1024);
        return bufferedReader.readLine();
    }

}
