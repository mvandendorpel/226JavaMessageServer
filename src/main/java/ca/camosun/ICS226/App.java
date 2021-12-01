package ca.camosun.ICS226;
import java.nio.ByteBuffer;
import java.io.*;
import java.net.*;
import java.nio.channels.*;
import java.util.*;

public class App 
{
    protected final int BUF_SIZE = 160;
    protected final String HOST = "";
    protected int port;

    public App(int port) {
        this.port = port;
    }

    void acceptConnection(SelectionKey key) {
        try {
            ServerSocketChannel channel = (ServerSocketChannel) key.channel();
            SocketChannel socketChannel = (SocketChannel) channel.accept();
            socketChannel.configureBlocking(false);
            socketChannel.register(key.selector(), SelectionKey.OP_READ);
        }
        catch (Exception e) {
            System.err.println(e);
        }

    }

    void readConnection(SelectionKey key) {
        try {
            SocketChannel channel = (SocketChannel) key.channel();
            ByteBuffer readBuffer = ByteBuffer.allocate(BUF_SIZE);
            int bytesCount = channel.read(readBuffer); // Does not reset buf index
            if (bytesCount > 0) {
                readBuffer.flip(); //reset buf index to 0
                String msg = Thread.currentThread().getName() + " " + new String(readBuffer.array());
                channel.write(ByteBuffer.wrap(msg.getBytes()));
            }
        }
        catch (Exception e) {
            System.err.println(e);
        }
    }

    public void serve() {
        try{
            ServerSocketChannel channel = ServerSocketChannel.open();
            channel.configureBlocking(false);
            ServerSocket serverSocket = channel.socket();
            serverSocket.bind(new InetSocketAddress(this.HOST, this.port));
            Selector selector = Selector.open();
            channel.register(selector, SelectionKey.OP_ACCEPT);
            while (true) {
                selector.select();
                Set keys = selector.selectedKeys();
                Iterator i = keys.iterator();
                while (i.hasNext()) {
                    SelectionKey key = (SelectionKey) i.next();
                    if (key.isAcceptable()) {
                        acceptConnection(key);
                    }
                    else if (key.isReadable()) {
                        readConnection(key);
                    }
                }

            }
        }
        catch (Exception e) {
            System.err.println(e);
            System.exit(-3);
        }

    }
    public static void main( String[] args )
    {
        
    }
}
