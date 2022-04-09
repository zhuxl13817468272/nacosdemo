package com.zxl.nacos.nettydemo.bio;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

/**
 *  同步时，线程间相互阻塞。先来线程A如果没有读写操作，会阻塞后来线程B的连接、读写操作，一直到线程A有读写操作完为止。
 *  异步时，线程自身读写阻塞。先来线程A如果没有读写操作，不会阻塞后来线程B的连接、读写操作。当一个线程调用 read() 或 write() 时，该线程被阻塞，直到有一些数据被读取，或数据完全写入，该线程在此期间不能再干任何事情了。
 *
 *  缺点：
 *      1. IO代码里read操作是阻塞操作，如果连接不做数据读写操作会导致线程阻塞，浪费资源。（线程自身读写阻塞）
 *      2. 如果线程很多，会导致服务器线程太多，压力太大，比如C10K问题。
 *
 */
public class SocketServer {
    public static void main(String[] args) throws IOException {
        ServerSocket serverSocket = new ServerSocket(9000);

        while(true){
            System.out.println("等待连接。。");
            // 阻塞方法
            Socket clientSocket  = serverSocket.accept();
            System.out.println("有客户端连接了。。");
            /**
             *  阻塞方法，场景：当有两个线程A先连接，经阻塞方法serverSocket.accept()，到阻塞方法clientSocket.getInputStream()，如果这个线程A没有读写操作，会一直阻塞后来的线程。
             *                 后来的线程B尝试连接，会阻塞无法连接，即使线程后来的线程读写操作，也会一直阻塞，知道线程A有了读写之后，才会放行线程B的连接、读写操作。
             */
//            handler(clientSocket);

            /**
             *   场景：当有两个线程A先连接，没有读写操作，会一直阻塞直到有读写操作。
             *         后来的线程B会连接成功，如果先于线程A有读写操作，会不受线程A阻塞的影响，直接响应线程B的读写操作。
             */
            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        handler(clientSocket);
                    }catch (IOException e){
                        e.printStackTrace();
                    }
                }
            }).start();

        }
    }

    private static void handler(Socket clientSocket) throws IOException {
        byte[] bytes = new byte[1024];
        System.out.println("准备read。。");
        //接收客户端的数据，阻塞方法，没有数据可读时就阻塞
        int read = clientSocket.getInputStream().read(bytes);
        System.out.println("read完毕。。");
        if(read != -1){
            System.out.println("接收到客户端的数据：" + new String(bytes, 0, read));
        }

        clientSocket.getOutputStream().write("HelloClient".getBytes());
        clientSocket.getOutputStream().flush();
    }
}
