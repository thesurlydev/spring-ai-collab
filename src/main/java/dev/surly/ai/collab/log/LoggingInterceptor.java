package dev.surly.ai.collab.log;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpRequest;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.util.StreamUtils;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

@Slf4j
public class LoggingInterceptor implements ClientHttpRequestInterceptor {
    @Override
    public ClientHttpResponse intercept(HttpRequest request, byte[] body, ClientHttpRequestExecution execution) throws IOException {

        if (!log.isDebugEnabled()) {
            return execution.execute(request, body);
        }

        String rawRequestBody = new String(body, StandardCharsets.UTF_8);
        try {
            ObjectMapper mapper = new ObjectMapper();
            Object json = mapper.readValue(rawRequestBody, Object.class);
            String prettyJson = mapper
                    .enable(SerializationFeature.INDENT_OUTPUT)
                    .writeValueAsString(json);
            logRequestBody(prettyJson);
        } catch (Exception e) {
            logRequestBody(rawRequestBody);
        }

        ClientHttpResponse response = execution.execute(request, body);

        byte[] responseBody = logResponseBody(response);

        return new BufferedClientHttpResponse(response, responseBody);
    }

    private void logRequestBody(String prettyJson) {
        log.debug("REQUEST:{}{}", System.lineSeparator(), prettyJson);
    }

    private byte[] logResponseBody(ClientHttpResponse response) throws IOException {
        byte[] responseBody = StreamUtils.copyToByteArray(response.getBody());
        String bodyAsString = new String(responseBody, StandardCharsets.UTF_8);
        log.debug("RESPONSE:{}{}", System.lineSeparator(), bodyAsString);
        return responseBody;
    }

    static class BufferedClientHttpResponse implements ClientHttpResponse {
        private final ClientHttpResponse originalResponse;
        private final byte[] body;

        public BufferedClientHttpResponse(ClientHttpResponse originalResponse, byte[] body) {
            this.originalResponse = originalResponse;
            this.body = body;
        }

        @Override
        public @NotNull HttpStatusCode getStatusCode() throws IOException {
            return originalResponse.getStatusCode();
        }

        @Override
        public @NotNull String getStatusText() throws IOException {
            return originalResponse.getStatusText();
        }

        @Override
        public void close() {
            originalResponse.close();
        }

        @Override
        public @NotNull InputStream getBody() throws IOException {
            return new ByteArrayInputStream(body);
        }

        @Override
        public @NotNull HttpHeaders getHeaders() {
            return originalResponse.getHeaders();
        }
    }
}
