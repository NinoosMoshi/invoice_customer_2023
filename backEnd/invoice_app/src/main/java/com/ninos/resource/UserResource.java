package com.ninos.resource;

import com.ninos.dto.UserDTO;
import com.ninos.dtomapper.UserDTOMapper;
import com.ninos.form.LoginForm;
import com.ninos.model.HttpResponse;
import com.ninos.model.User;
import com.ninos.model.UserPrinciple;
import com.ninos.provider.TokenProvider;
import com.ninos.service.RoleService;
import com.ninos.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.bind.annotation.*;
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
    private final TokenProvider tokenProvider;
    private final RoleService roleService;


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


    @GetMapping("/verify/code/{email}/{code}")
    public ResponseEntity<HttpResponse> verifyCode(@PathVariable("email") String email, @PathVariable("code") String code){
        UserDTO user = userService.verifyCode(email, code);
        return ResponseEntity.ok().body(
                HttpResponse.builder()
                        .timeStamp(now().toString())
                        .data(Map.of("user", user,
                                "access_token", tokenProvider.createAccessToken(getUserPrincipal(user)),
                                "refresh_token",tokenProvider.createRefreshToken(getUserPrincipal(user))
                        ))
                        .message("Login Success")
                        .status(HttpStatus.OK)
                        .statusCode(HttpStatus.OK.value())
                        .build());

    }

    private URI getUri() {
      return URI.create(ServletUriComponentsBuilder.fromCurrentContextPath().path("/user/get/<userId>").toUriString());
    }


    private ResponseEntity<HttpResponse> sendResponse(UserDTO user) {
                return ResponseEntity.ok().body(
                        HttpResponse.builder()
                                .timeStamp(now().toString())
                                .data(Map.of("user", user,
                                             "access_token", tokenProvider.createAccessToken(getUserPrincipal(user)),
                                             "refresh_token",tokenProvider.createRefreshToken(getUserPrincipal(user))
                                        ))
                                .message("Login Success")
                                .status(HttpStatus.OK)
                                .statusCode(HttpStatus.OK.value())
                                .build());
    }

    private UserPrinciple getUserPrincipal(UserDTO user) {
        // get user from user principle and all user permissions
        return new UserPrinciple(UserDTOMapper.toUser(userService.getUserByEmail(user.getEmail())), roleService.getRoleByUserId(user.getId()).getPermission());
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
