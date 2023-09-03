package org.example.server;

import org.apache.commons.fileupload.RequestContext;

import org.apache.http.NameValuePair;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class Request implements RequestContext {

    private String method;
    private String url;
    private String path;
    private List<NameValuePair> queryParams;
    private String version;
    private List<String> headers;
    private List<NameValuePair> postParams;
    private String body;
    private String boundary;

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public String getBoundary() {
        return boundary;
    }

    public void setBoundary(String boundary) {
        this.boundary = boundary;
    }

    public List<NameValuePair> getQueryParams() {
        return queryParams;
    }

    public void setQueryParams(List<NameValuePair> queryParams) {
        this.queryParams = queryParams;
    }

    public List<NameValuePair> getPostParams() {
        return postParams;
    }

    public void setPostParams(List<NameValuePair> postParams) {
        this.postParams = postParams;
    }

    public String getMethod() {
        return method;
    }

    public void setMethod(String method) {
        this.method = method;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public void setHeaders(List<String> headers) {
        this.headers = headers;
    }

    public List<String> getHeaders() {
        return headers;
    }

    public List<NameValuePair> getQueryParam(String name) {
        List<NameValuePair> queryParamByName = new ArrayList<>();
        for (NameValuePair queryParam : getQueryParams()) {
            if (name.equals(queryParam.getName())) {
                queryParamByName.add(queryParam);
            }
        }
        if (!queryParamByName.isEmpty()) {
            return queryParamByName;
        }
        System.out.println("Запрашиваемое поле отсутствует");
        return null;
    }

    public List<NameValuePair> getPostParam(String name) {
        List<NameValuePair> postParamByName = new ArrayList<>();
        for (NameValuePair postParam : getPostParams()) {
            if (name.equals(postParam.getName())) {
                postParamByName.add(postParam);
            }
        }
        if (!postParamByName.isEmpty()) {
            return postParamByName;
        }
        System.out.println("Запрашиваемое поле отсутствует");
        return null;
    }

    @Override
    public String getCharacterEncoding() {
        return "UTF-8";
    }

    @Override
    public String getContentType() {
        Optional<String> optContType = headers.stream()
                .filter(o -> o.startsWith("Content-Type"))
                .map(o -> o.substring(o.indexOf(" ")))
                .map(String::trim)
                .findFirst();
        return optContType.orElse(null);
    }

    public int getContentLength() {
        Optional<String> optLength = headers.stream()
                .filter(o -> o.startsWith("Content-Length"))
                .map(o -> o.substring(o.indexOf(" ")))
                .map(String::trim)
                .findFirst();
        return optLength.map(Integer::parseInt).orElse(0);
    }

    @Override
    public InputStream getInputStream() {
        return new ByteArrayInputStream(body.getBytes());
    }

    @Override
    public String toString() throws NullPointerException {
        if (method.equals("GET") && queryParams != null) {
            return "Request {\n" +
                    "\tМетод запроса: " + method + "\n" +
                    "\tРесурс: " + url + "\n" +
                    "\tQuery: " + queryParams + "\n" +
                    "\tПротокол: " + version + "\n" +
                    "\tЗаголовки: " + headers.toString() + "\n" +
                    "}";
        } else if (method.equals("POST") && boundary != null && postParams != null) {
            return "Request {\n" +
                    "\tМетод запроса: " + method + "\n" +
                    "\tРесурс: " + url + "\n" +
                    "\tПротокол: " + version + "\n" +
                    "\tЗаголовки: " + headers.toString() + "\n" +
                    "\tBoundary: " + boundary + "\n" +
                    "\tТело запроса: " + body + "\n" +
                    "\tpostParams: " + postParams.toString() + "\n" +
                    "}";
        } else if (method.equals("POST") && postParams != null) {
            return "Request {\n" +
                    "\tМетод запроса: " + method + "\n" +
                    "\tРесурс: " + url + "\n" +
                    "\tПротокол: " + version + "\n" +
                    "\tЗаголовки: " + headers.toString() + "\n" +
                    "\tТело запроса: " + body + "\n" +
                    "\tpostParams: " + postParams.toString() + "\n" +
                    "}";
        } else {
            return "Request {\n" +
                    "\tМетод запроса: " + method + "\n" +
                    "\tРесурс: " + url + "\n" +
                    "\tПротокол: " + version + "\n" +
                    "\tЗаголовки: " + headers.toString() + "\n" +
                    "}";
        }
    }
}