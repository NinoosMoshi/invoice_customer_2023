package com.ninos.resource;

import com.ninos.dto.UserDTO;
import com.ninos.form.LoginForm;
import com.ninos.model.HttpResponse;
import com.ninos.model.User;
import com.ninos.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.Map;

import static java.time.LocalDateTime.now;

@RestController
@RequiredArgsConstructor
@RequestMapping("/user")
public class UserResource {

    private final UserService userService;
    private final AuthenticationManager authenticationManager;


    @PostMapping(value = {"/login", "/sign-in"})
    public ResponseEntity<HttpResponse> login(@RequestBody @Valid LoginForm loginForm){

        authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(loginForm.getEmail(),loginForm.getPassword()));
        UserDTO user = userService.getUserByEmail(loginForm.getEmail());
        return user.isUsingMfa() ? sendVerificationCode(user) : sendResponse(user);

    }




    @PostMapping(value = {"/register", "/sign-up"})
    public ResponseEntity<HttpResponse> saveUser(@RequestBody @Valid User user){
        UserDTO userDTO = userService.createUser(user);
        return ResponseEntity.created(getUri())
                .body(
                        HttpResponse.builder()
                                .timeStamp(now().toString())
                                .data(Map.of("user", userDTO))
                                .message("User created")
                                .status(HttpStatus.CREATED)
                                .statusCode(HttpStatus.CREATED.value())
                                .build());

    }

    private URI getUri() {
      return URI.create(ServletUriComponentsBuilder.fromCurrentContextPath().path("/user/get/<userId>").toUriString());
    }


    private ResponseEntity<HttpResponse> sendResponse(UserDTO user) {
                return ResponseEntity.ok().body(
                        HttpResponse.builder()
                                .timeStamp(now().toString())
                                .data(Map.of("user", user))
                                .message("Login Success")
                                .status(HttpStatus.OK)
                                .statusCode(HttpStatus.OK.value())
                                .build());
    }

    private ResponseEntity<HttpResponse> sendVerificationCode(UserDTO user) {
        userService.sendVerificationCode(user);
        return ResponseEntity.ok().body(
                HttpResponse.builder()
                        .timeStamp(now().toString())
                        .data(Map.of("user", user))
                        .message("Verification Code Sent")
                        .status(HttpStatus.OK)
                        .statusCode(HttpStatus.OK.value())
                        .build());
    }





}
