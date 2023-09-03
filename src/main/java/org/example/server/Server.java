package org.example.server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Server {

    private final List<String> allowedMethods = List.of("GET", "POST");
    private final Map<String, Map<String, Handler>> handlers = new ConcurrentHashMap<>(
            Map.of(allowedMethods.get(0), new ConcurrentHashMap<>(),
                    allowedMethods.get(1), new ConcurrentHashMap<>()));

    public void listen() {
        ExecutorService executor = Executors.newFixedThreadPool(64);
        int port = 9999;
        try (ServerSocket serverSocket = new ServerSocket(port)) {
            while (true) {
                Socket socket = serverSocket.accept();
                executor.submit(new Processor(socket, handlers));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        executor.shutdown();
    }

    public void addHandler(String method, String path, Handler handler) {
        if (!allowedMethods.contains(method)) {
            System.out.println("Не разрешенный метод!");
        }
        handlers.get(method).put(path, handler);
        System.out.printf("Обработчик для '%s %s' добавлен!\n", method, path);
    }
}