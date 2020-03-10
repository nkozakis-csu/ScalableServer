package cs455.scaling.tasks;

import cs455.scaling.server.Server;
import cs455.scaling.threading.Task;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.*;

public class RegisterTask extends Task {
    Server server;
    Selector selector;
    SocketChannel sc;

    public RegisterTask(Server s, Selector sel, SocketChannel sc){
        super();
        server = s;
        selector = sel;
        this.sc = sc;
    }

    public void run() throws IOException {
        sc.configureBlocking(false);
        int id = server.activeConnections.getAndIncrement();
        sc.register(selector, SelectionKey.OP_READ, id);
        System.out.println("registered client: " + sc.getRemoteAddress());
    }

}
