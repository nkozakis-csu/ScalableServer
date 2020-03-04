package cs455.scaling.threading;

import cs455.scaling.server.Server;

import java.io.IOException;
import java.nio.channels.*;

public class RegisterTask extends Task{

    Selector selector;
    ServerSocketChannel ssc;

    public RegisterTask(Selector sel, ServerSocketChannel ssc){
        super();
        selector = sel;
        this.ssc = ssc;
    }

    public void run(){
        try {
            SocketChannel sc = ssc.accept();
            if (sc != null) {
                sc.configureBlocking(false);
                sc.register(selector, SelectionKey.OP_READ);
                System.out.println("registered client: " + sc.getRemoteAddress());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
