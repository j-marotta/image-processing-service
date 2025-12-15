package org.jakemarotta.imageprocessingservice.services;

import org.jakemarotta.imageprocessingservice.entities.User;
import org.jakemarotta.imageprocessingservice.entities.UserDTO;
import org.jakemarotta.imageprocessingservice.repos.UserRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    @Autowired
    private UserRepo userRepo;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private AuthenticationManager authenticationManager;

    public User signup(UserDTO user) {
        User newUser = new User();
        newUser.setUsername(user.getUsername());
        newUser.setPassword(passwordEncoder.encode(user.getPassword()));

        userRepo.save(newUser);

        return newUser;
    }

    public void authenticate(UserDTO user) {
        try {
            UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(user.getUsername(), user.getPassword());
            authenticationManager.authenticate(authenticationToken);
        } catch (BadCredentialsException exception) {
            throw new BadCredentialsException("Username or Password is Invalid");
        }
    }
}
