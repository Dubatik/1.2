package org.example.server;

import org.apache.commons.fileupload.*;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.example.utils.*;

import java.io.*;

import java.net.Socket;
import java.net.URISyntaxException;
import java.util.*;

public class Processor implements Runnable {
    private static final String URLENCODED = "Content-Type: application/x-www-form-urlencoded";
    private static final String MULTIPART = "Content-Type: multipart/form-data";
    private final Socket socket;
    private final Map<String, Map<String, Handler>> handlers;
    private final HttpRequestParser parser;

    public Processor(Socket socket, Map<String, Map<String, Handler>> handlers) {
        this.socket = socket;
        this.handlers = handlers;
        this.parser = new HttpRequestParser();
    }

    @Override
    public void run() {
        try (var in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
             var out = new BufferedOutputStream(socket.getOutputStream())) {

            processRequest(in, out);

        } catch (IOException | URISyntaxException e) {
            e.printStackTrace();
        } finally {
            try {
                socket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void processRequest(BufferedReader in, BufferedOutputStream out)
            throws IOException, URISyntaxException {

        StringBuilder requestLineBuilder = new StringBuilder();
        String line;
        while (!(line = in.readLine()).isBlank()) {
            requestLineBuilder.append(line).append("\r\n");
        }
        String requestLine = requestLineBuilder.toString();

        Request request = parser.parseRequest(requestLine);

        if (!handlers.containsKey(request.getMethod())) {
            ResponseUtils.sendMethodNotAllowed(out);
        }
        if (!request.getPath().startsWith("/")) {
            ResponseUtils.sendBadRequest(out);
        }

        // Тело POST запросов в кодировке text/plain не обрабатываем отдельно
        if (request.getMethod().equals("POST")) {
            if (request.getHeaders().toString().contains(URLENCODED)) {
                readAndParseUrlEncodedBody(request, in);
            } else if (request.getHeaders().toString().contains(MULTIPART)) {
                readAndParseMultipartBody(request, in);
            }
        }

        Handler handler = handlers.get(request.getMethod()).get(request.getPath());
        if (handler == null) {
            ResponseUtils.sendNotFound(out);
            System.out.println(request + " обработан с ошибкой 404!");
        } else {
            handler.handle(request, out);
            System.out.println(request + " обработан!");
        }
    }

    private void readAndParseUrlEncodedBody(Request request, BufferedReader in) throws IOException {
        // Читаем тело по-символьно
        StringBuilder bodyLineBuilder = new StringBuilder();
        int contentLength = request.getContentLength();
        for (int i = 0; i < contentLength; i++) {
            int intBody = in.read();
            bodyLineBuilder.append((char) intBody);
        }
        String bodyLine = bodyLineBuilder.toString();
        request.setBody(bodyLine);

        List<NameValuePair> postParams = new ArrayList<>();
        String[] params = request.getBody().split("&");
        for (String param : params) {
            String[] prm = param.split("=");
            // Складываем POST-параметры в список пар NameValuePair
            postParams.add(new BasicNameValuePair(prm[0], prm[1]));
        }
        request.setPostParams(postParams);
    }

    private void readAndParseMultipartBody(Request request, BufferedReader in) throws IOException {
        // Читаем тело по-строчно
        StringBuilder bodyLineBuilder = new StringBuilder();
        String body;
        while ((body = in.readLine()) != null) {
            bodyLineBuilder.append(body).append("\r\n");
            if (body.endsWith("--")) break;
        }
        String bodyLine = bodyLineBuilder.toString();
        request.setBody(bodyLine);
        String boundary = bodyLine.substring(2, bodyLine.indexOf('\n')).trim();
        request.setBoundary(boundary);
        List<NameValuePair> postParams = new ArrayList<>();

        try {
            FileItemFactory factory = new DiskFileItemFactory();
            FileUpload upload = new FileUpload(factory);
            List<FileItem> fileItems = upload.parseRequest(request);
            for (FileItem fileItem : fileItems) {
                if (fileItem.isFormField()) {
                    postParams.add(new BasicNameValuePair(fileItem.getFieldName(), fileItem.getString()));
                } else {
                    // Если файл, то сохраняем в корневую папку проекта
                    String fileName = fileItem.getName();
                    try {
                        fileItem.write(new File(fileName));
                        postParams.add(new BasicNameValuePair(fileItem.getFieldName(), fileName));
                    } catch (Exception e) {
                        System.out.println("Не могу сохранить файл " + fileName + "! Наверное он уже имеется!");
                    }
                }
            }
            request.setPostParams(postParams);
        } catch (FileUploadException e) {
            System.out.println("Что-то пошло не так, не получается распарсить!");
        }
    }
}