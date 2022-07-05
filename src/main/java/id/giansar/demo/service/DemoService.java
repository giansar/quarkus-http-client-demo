package id.giansar.demo.service;

import id.giansar.demo.dao.DemoDao;
import id.giansar.demo.dto.DemoRequestDto;
import id.giansar.demo.dto.DemoResponseDto;
import id.giansar.demo.dto.ErrorDto;
import id.giansar.demo.entity.HostServer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.ws.rs.core.Response;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpHeaders;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.CompletableFuture;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@ApplicationScoped
public class DemoService {
    private static final Logger LOGGER = LoggerFactory.getLogger(DemoService.class);

    @Inject
    DemoDao demoDao;

    public Response getServer(DemoRequestDto requestDto) {
        DemoResponseDto responseDto = new DemoResponseDto();

        //validate input
        if (!isUrlValid(requestDto.url)) {
            responseDto.error = new ErrorDto("E1001", "URL is not valid");
            return Response.status(Response.Status.BAD_REQUEST).entity(requestDto).build();
        }

        //prepare a request
        Properties properties = System.getProperties();
        properties.setProperty("jdk.internal.httpclient.disableHostnameVerification", Boolean.TRUE.toString());
        
        HttpRequest httpRequest = HttpRequest.newBuilder()
                .GET()
                .uri(URI.create(requestDto.url))
                .headers("Accept", "*/*", "Accept-Encoding",
                        "gzip, deflate, br", "User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64; rv:102.0) Gecko/20100101 Firefox/102.0")
                .build();
        HttpClient httpClient = HttpClient.newBuilder().followRedirects(HttpClient.Redirect.ALWAYS).build();

        //send request and process the response
        try {
            CompletableFuture<HttpResponse<String>> completableFuture = httpClient.sendAsync(httpRequest, HttpResponse.BodyHandlers.ofString());
            HttpResponse<String> httpResponse = completableFuture.get();
            String host = httpResponse.uri().getHost().toLowerCase();
            responseDto.host = host;
            responseDto.statusCode = httpResponse.statusCode();
            responseDto.httpVersion = httpResponse.version().toString();

            if (httpResponse.statusCode() != 200) {
                return Response.ok(responseDto).build();
            }
            HttpHeaders httpHeaders = httpResponse.headers();
            Map<String, Object> headers = httpHeadersToMap(httpHeaders);

            String server = headers.get("server") == null ? "" : ((List) headers.get("server")).get(0).toString();
            String date = headers.get("date") == null ? "" : ((List) headers.get("date")).get(0).toString();
            ZonedDateTime dateTime = date.isEmpty() ? null : ZonedDateTime.parse(((List) headers.get("date")).get(0).toString(), DateTimeFormatter.RFC_1123_DATE_TIME);

            HostServer hostServer = new HostServer();
            hostServer.host = host;
            hostServer.server = server;
            hostServer.dateInquiry = dateTime;
            demoDao.saveOrUpdateHostServer(hostServer);

            responseDto.date = date;
            responseDto.server = server;
            return Response.ok(responseDto).build();
        } catch (Exception e) {
            LOGGER.error("General Error", e);
            responseDto.error = new ErrorDto("E0001", "General Error");
            responseDto.host = httpRequest.uri().getHost();
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(responseDto).build();
        }
    }

    private Map<String, Object> httpHeadersToMap(HttpHeaders httpHeaders) {
        Map<String, Object> headerMap = new HashMap<>();
        httpHeaders.map().forEach((key, value) -> {
            headerMap.put(key, value);
            LOGGER.info("{} = {}", key, value);
        });
        return headerMap;
    }

    private boolean isUrlValid(String url) {
        final String REGEX = "^((((https?)://))(%[0-9A-Fa-f]{2}|[-()_.!~*';/?:@&=+$,A-Za-z0-9])+)([).!';/?:,][[:blank:]])?$";

        if (url == null || url.isEmpty()) return false;

        Pattern pattern = Pattern.compile(REGEX);
        Matcher matcher = pattern.matcher(url);
        return matcher.matches();
    }
}
