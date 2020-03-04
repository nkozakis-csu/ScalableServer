package cs455.scaling.tasks;

import cs455.scaling.threading.Task;
import cs455.scaling.threading.TaskInterface;

import java.io.IOException;
import java.nio.channels.*;

public class RegisterTask extends Task implements TaskInterface {

    Selector selector;
    ServerSocketChannel ssc;

    public RegisterTask(Selector sel, ServerSocketChannel ssc){
        super();
        selector = sel;
        this.ssc = ssc;
    }

    public void run() throws IOException {
        SocketChannel sc = ssc.accept();
        if (sc != null) {
            sc.configureBlocking(false);
            sc.register(selector, SelectionKey.OP_READ);
            System.out.println("registered client: " + sc.getRemoteAddress());
        }
    }

}
