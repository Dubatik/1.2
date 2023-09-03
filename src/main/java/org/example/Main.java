package org.example;

import org.example.server.Server;
import org.example.utils.*;

import java.nio.file.Files;
import java.nio.file.Path;

public class Main {
    public static void main(String[] args) {

        Server server = new Server();

        server.addHandler("GET", "/index.html",
                (request, out) -> {
                    Path filePath = Path.of(".", "public", request.getPath());
                    byte[] content = Files.readAllBytes(filePath);
                    ResponseUtils.sendOkResponse(filePath, content, out);
                });
        server.addHandler("POST", "/index.html",
                (request, out) -> {
                    Path filePath = Path.of(".", "public", request.getPath());
                    byte[] content = Files.readAllBytes(filePath);
                    ResponseUtils.sendOkResponse(filePath, content, out);
                });
        server.addHandler("GET", "/classic.html",
                (request, out) -> {
                    Path filePath = Path.of(".", "public", request.getPath());
                    String content = Files.readString(filePath);
                    byte[] newContent = content
                            .replace("{time}", TimeUtils.getCurrentTime())
                            .getBytes();
                    ResponseUtils.sendOkResponse(filePath, newContent, out);
                });
        server.addHandler("GET", "/events.html",
                (request, out) -> {
                    Path filePath = Path.of(".", "public", request.getPath());
                    byte[] content = Files.readAllBytes(filePath);
                    ResponseUtils.sendOkResponse(filePath, content, out);
                });
        server.addHandler("GET", "/events.js",
                (request, out) -> {
                    Path filePath = Path.of(".", "public", request.getPath());
                    byte[] content = Files.readAllBytes(filePath);
                    ResponseUtils.sendOkResponse(filePath, content, out);
                });
        server.addHandler("GET", "/resources.html",
                (request, out) -> {
                    Path filePath = Path.of(".", "public", request.getPath());
                    byte[] content = Files.readAllBytes(filePath);
                    ResponseUtils.sendOkResponse(filePath, content, out);
                });
        server.addHandler("GET", "/spring.png",
                (request, out) -> {
                    Path filePath = Path.of(".", "public", request.getPath());
                    byte[] content = Files.readAllBytes(filePath);
                    ResponseUtils.sendOkResponse(filePath, content, out);
                });
        server.addHandler("GET", "/spring.svg",
                (request, out) -> {
                    Path filePath = Path.of(".", "public", request.getPath());
                    byte[] content = Files.readAllBytes(filePath);
                    ResponseUtils.sendOkResponse(filePath, content, out);
                });
        server.addHandler("GET", "/styles.css",
                (request, out) -> {
                    Path filePath = Path.of(".", "public", request.getPath());
                    byte[] content = Files.readAllBytes(filePath);
                    ResponseUtils.sendOkResponse(filePath, content, out);
                });
        server.addHandler("GET", "/links.html",
                (request, out) -> {
                    Path filePath = Path.of(".", "public", request.getPath());
                    byte[] content = Files.readAllBytes(filePath);
                    ResponseUtils.sendOkResponse(filePath, content, out);
                });
        server.addHandler("GET", "/forms.html",
                (request, out) -> {
                    Path filePath = Path.of(".", "public", request.getPath());
                    byte[] content = Files.readAllBytes(filePath);
                    ResponseUtils.sendOkResponse(filePath, content, out);
                });
        server.addHandler("GET", "/app.js",
                (request, out) -> {
                    Path filePath = Path.of(".", "public", request.getPath());
                    byte[] content = Files.readAllBytes(filePath);
                    ResponseUtils.sendOkResponse(filePath, content, out);
                });

        server.listen();
    }
}