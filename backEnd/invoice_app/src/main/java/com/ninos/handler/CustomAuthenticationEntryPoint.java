package com.ninos.handler;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ninos.model.HttpResponse;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.OutputStream;

import static java.time.LocalDateTime.now;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;


@Component
public class CustomAuthenticationEntryPoint implements AuthenticationEntryPoint {

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException) throws IOException, ServletException {
        HttpResponse httpResponse = HttpResponse.builder()
        .timeStamp(now().toString())
        .reason("You need to log in to access this resource")
        .status(HttpStatus.UNAUTHORIZED)
        .statusCode(HttpStatus.UNAUTHORIZED.value())
        .build();
        response.setContentType(APPLICATION_JSON_VALUE);
        response.setStatus(HttpStatus.UNAUTHORIZED.value());

        OutputStream out = response.getOutputStream();  // take a response from the stream
        ObjectMapper mapper = new ObjectMapper();       // convert a stream to json
        mapper.writeValue(out, httpResponse);           // write a response as a json
        out.flush();
    }

}
