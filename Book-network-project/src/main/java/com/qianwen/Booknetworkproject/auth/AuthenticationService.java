package com.qianwen.Booknetworkproject.auth;

import com.qianwen.Booknetworkproject.auth.authRequest.LoginRequest;
import com.qianwen.Booknetworkproject.auth.authRequest.RegistrationRequest;
import com.qianwen.Booknetworkproject.auth.authResponse.LoginResponse;
import com.qianwen.Booknetworkproject.email.EmailService;
import com.qianwen.Booknetworkproject.email.EmailTemplateName;
import com.qianwen.Booknetworkproject.entities.activationCode.Token;
import com.qianwen.Booknetworkproject.entities.activationCode.TokenRepository;
import com.qianwen.Booknetworkproject.entities.role.Role;
import com.qianwen.Booknetworkproject.entities.role.RoleRepository;
import com.qianwen.Booknetworkproject.entities.user.User;
import com.qianwen.Booknetworkproject.entities.user.UserRepository;
import com.qianwen.Booknetworkproject.security.jwt.JwtService;
import jakarta.mail.MessagingException;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class AuthenticationService {
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtService jwtService;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private EmailService emailService;

    @Autowired
    private TokenRepository tokenRepository;

    @Value("${application.mailing.frontend.activation-url}")
    private String activationUrl;

    public void register(RegistrationRequest request) throws MessagingException {
        //get USER role from rolerepo
        Role userRole = roleRepository.findByName("USER")
                .orElseThrow(() -> new IllegalStateException("ROLE USER was not initiated"));
        //assign user with role
        User user = new User();
        user.setFirstname(request.getFirstname());
        user.setLastname(request.getLastname());
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setAccountLocked(false);
        user.setEnabled(false);
        user.setRoles(List.of(userRole));
        userRepository.save(user);
        sendValidationEmail(user);
    }

    private void sendValidationEmail(User user) throws MessagingException {
        //get activation code
        String activationCode = generateActivationCode(6, user);
        //send activation email to user
        emailService.sendEmail(user.getEmail(),
                user.getFullName(),
                EmailTemplateName.ACTIVATE_ACCOUNT,
                activationUrl,
                activationCode,
                "Account activation"
        );
    }

    private String generateActivationCode(int length, User user) {
        String characters = "0123456789";
        StringBuilder codeBuilder = new StringBuilder();
        SecureRandom secureRandom = new SecureRandom();

        for (int i = 0; i < length; i++) {
            int randomIndex = secureRandom.nextInt(characters.length());
            codeBuilder.append(characters.charAt(randomIndex));
        }
        String activationCode = codeBuilder.toString();

        Token generatedActivationToken = new Token();
        generatedActivationToken.setActivationCode(activationCode);
        generatedActivationToken.setCreatedAt(LocalDateTime.now());
        generatedActivationToken.setExpiresAt(LocalDateTime.now().plusMinutes(15));
        generatedActivationToken.setUser(user);
        tokenRepository.save(generatedActivationToken);

        return activationCode;
    }

    @Transactional
    public void activateAccount(String code) throws MessagingException {
        //find activation token from repo
        Token savedToken = tokenRepository.findByActivationCode(code)
                .orElseThrow(() -> new RuntimeException("Invalid token"));
        //validate if token is expired or not
        if (LocalDateTime.now().isAfter(savedToken.getExpiresAt())) {
            sendValidationEmail(savedToken.getUser());
            throw new RuntimeException("Activation token has expired. A new token has been send to the same email address");
        }
        //find user from token info
        User user = userRepository.findById(savedToken.getUser().getId())
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
        //make sure users account is activated
        user.setEnabled(true);
        userRepository.save(user);
        //record validation time
        savedToken.setValidatedAt(LocalDateTime.now());
        tokenRepository.save(savedToken);
    }


    public LoginResponse authenticate(LoginRequest request) {
        Authentication auth = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getEmail(),
                        request.getPassword()
                )
        );
        //get user from authentication token
        User user = ((User) auth.getPrincipal());
        // add extra claim to jwt token
        Map<String, Object> claims = new HashMap<>();
        claims.put("fullName", user.getFullName());
        //generate jwt token
        String jwtToken = jwtService.generateToken((User) auth.getPrincipal(), claims);
        //return user token
        LoginResponse loginResponse = new LoginResponse();
        loginResponse.setToken(jwtToken);
          return loginResponse;
    }


}