package org.example.server;

import org.apache.http.NameValuePair;
import org.apache.http.client.utils.URLEncodedUtils;

import java.net.URI;
import java.net.URISyntaxException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

public class HttpRequestParser {
    private final Request resultRequest;

    public HttpRequestParser() {
        this.resultRequest = new Request();
    }

    public Request parseRequest(String request) throws URISyntaxException {

        String[] requestsLines = request.split("\r\n");
        String[] requestLine = requestsLines[0].split(" ");
        resultRequest.setMethod(requestLine[0]);

        String url = requestLine[1];
        resultRequest.setUrl(url);
        if (url.contains("?")) {
            resultRequest.setPath(url.substring(0, url.indexOf('?')));
            List<NameValuePair> queryParams = URLEncodedUtils.parse(new URI(url), Charset.defaultCharset());
            resultRequest.setQueryParams(queryParams);
        } else {
            resultRequest.setPath(url);
        }

        resultRequest.setVersion(requestLine[2]);

        List<String> headers = new ArrayList<>();
        for (int h = 1; h < requestsLines.length; h++) {
            String header = requestsLines[h];
            headers.add(header);
        }
        resultRequest.setHeaders(headers);

        return resultRequest;
    }
}