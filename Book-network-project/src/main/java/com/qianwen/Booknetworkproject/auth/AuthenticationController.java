package com.qianwen.Booknetworkproject.auth;

import com.qianwen.Booknetworkproject.auth.authRequest.LoginRequest;
import com.qianwen.Booknetworkproject.auth.authRequest.RegistrationRequest;
import com.qianwen.Booknetworkproject.auth.authResponse.LoginResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.mail.MessagingException;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("auth")
@Tag(name = "Authentication")
public class AuthenticationController {
    @Autowired
    private AuthenticationService service;

    @PostMapping("/register")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public ResponseEntity<?> register(@RequestBody @Valid RegistrationRequest request) throws MessagingException {
        service.register(request);
        return ResponseEntity.accepted().build();
    }

    @GetMapping("/activate-account")
    public void confirmEmail(@RequestParam String code) throws MessagingException {
        service.activateAccount(code);
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> userLogin(@RequestBody LoginRequest request) {
        return ResponseEntity.ok(service.authenticate(request));
    }





}