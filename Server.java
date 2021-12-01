import java.io.*;
import java.net.*;
import java.nio.channels.IllegalBlockingModeException;
import java.nio.channels.*;
import java.nio.ByteBuffer;
import java.util.concurrent.*;


public class Server {
    protected final int BUF_SIZE = 160;
    protected final String HOST = "";
    protected int port;

    public Server(int port) {
        this.port = port;
    }

    void delegate(AsynchronousSocketChannel channel) {
        try {
            ByteBuffer readBuffer = ByteBuffer.allocate(BUF_SIZE);
            CompletionHandler<Integer,Void> handler =
            new CompletionHandler<Integer,Void>() {
                public void completed(Integer result, Void attachment) {
                    if (result <= 0) {
                        try {
                            channel.close();
                        } catch (Exception e) {
                            System.err.println(e);
                        }
                        return;
                    }
                    readBuffer.flip();
                    String msg = Thread.currentThread().getName() + " " + new String(readBuffer.array());
                    channel.write(ByteBuffer.wrap(msg.getBytes()));
                    channel.read(readBuffer, null, this);
                }
                public void failed(Throwable e, Void att) {
                    System.err.println(e);
                }
            };
            channel.read(readBuffer, null, handler);
        } catch (Exception e) {
            System.err.println(e);
            System.exit(-1);
        }
    }
    public static void main(String[] args) {
        Server s = new Server(24680);
    
    }
    
}

