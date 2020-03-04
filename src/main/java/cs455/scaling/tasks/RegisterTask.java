package cs455.scaling.tasks;

import cs455.scaling.server.Server;
import cs455.scaling.threading.Task;
import cs455.scaling.threading.TaskInterface;

import java.io.IOException;
import java.nio.channels.*;

public class RegisterTask extends Task implements TaskInterface {
    
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
        selector.wakeup();
        sc.register(selector, SelectionKey.OP_READ);
        System.out.println("registered client: " + sc.getRemoteAddress());
        server.activeConnections.getAndIncrement();
    }

}
