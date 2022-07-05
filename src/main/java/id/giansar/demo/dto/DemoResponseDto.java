package id.giansar.demo.dto;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class DemoResponseDto {
    public String host;
    public ErrorDto error;
    public Integer statusCode;
    public String httpVersion;
    public String date;
    public String server;
}
