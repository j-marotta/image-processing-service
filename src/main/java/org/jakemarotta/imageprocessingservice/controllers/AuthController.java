package org.jakemarotta.imageprocessingservice.controllers;

import lombok.extern.slf4j.Slf4j;
import org.jakemarotta.imageprocessingservice.entities.JWTResponse;
import org.jakemarotta.imageprocessingservice.entities.User;
import org.jakemarotta.imageprocessingservice.entities.UserDTO;
import org.jakemarotta.imageprocessingservice.services.JWTService;
import org.jakemarotta.imageprocessingservice.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/")
@Slf4j
public class AuthController {

    @Autowired
    private UserService userService;

    @Autowired
    private UserDetailsService userDetailsService;

    @Autowired
    private JWTService jwtService;

    @PostMapping("auth/register")
    public ResponseEntity<JWTResponse> register(@RequestBody UserDTO user) {
        User newUser = userService.signup(user);

        UserDetails userDetails = userDetailsService.loadUserByUsername(user.getUsername());
        String token = jwtService.generateToken(userDetails);

        JWTResponse response = new JWTResponse();
        response.setUsername(user.getUsername());
        response.setAtToken(token);

        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @PostMapping("auth/login")
    public ResponseEntity<?> login(@RequestBody UserDTO user) {
        try {
            userService.authenticate(user);

            UserDetails userDetails = userDetailsService.loadUserByUsername(user.getUsername());
            String token = jwtService.generateToken(userDetails);

            JWTResponse response = new JWTResponse();
            response.setUsername(user.getUsername());
            response.setAtToken(token);

            return new ResponseEntity<>(response, HttpStatus.OK);

        } catch (BadCredentialsException e) {
            log.error("Bad credentials");
            return new ResponseEntity<>("Incorrect email or password",HttpStatus.NO_CONTENT);
        }
    }
}
