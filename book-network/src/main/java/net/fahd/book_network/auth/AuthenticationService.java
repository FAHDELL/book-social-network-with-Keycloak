package net.fahd.book_network.auth;

import jakarta.mail.MessagingException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import net.fahd.book_network.email.EmailService;
import net.fahd.book_network.email.EmailTemplateName;
import net.fahd.book_network.role.RoleRepository;
import net.fahd.book_network.security.JwtService;
import net.fahd.book_network.user.Token;
import net.fahd.book_network.user.TokenRepository;
import net.fahd.book_network.user.User;
import net.fahd.book_network.user.UserRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;

//@Service
@RequiredArgsConstructor
public class AuthenticationService {

   /* private final RoleRepository roleRepository;

    private final PasswordEncoder passwordEncoder;

    private final UserRepository userRepository;

    private final TokenRepository tokenRepository;

    private final EmailService emailService;

    private final AuthenticationManager authenticationManager;

    private final JwtService  jwtService;

    @Value("${application.mailing.frontend.activation-url}")
    private String activationUrl;

    public void register(RegistrationRequest request) throws MessagingException {

        var userRole = roleRepository.findByName("USER")
                .orElseThrow(() -> new IllegalStateException("ROLE User not initialize"));
        var user = User.builder()
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .accountLocked(false)
                .enabled(false)
                .roles(List.of(userRole))
                .build();
        userRepository.save(user);
        sendValidationEmail(user);
    }

    private void sendValidationEmail(User user) throws MessagingException {
        var newToken = generatesAndSaveActivationToken(user);
        //send email

        emailService.sendEmail(
                user.getEmail(),
                user.fullName(),
                EmailTemplateName.ACTIVATE_ACCOUNT,
                activationUrl,
                newToken,
                "Account activation"
        );
    }

    private String generatesAndSaveActivationToken(User user) {
        //generate a token
        String generatesToken = generateActivationCode(6);

        var token = Token.builder()
                .token(generatesToken)
                .createdAt(LocalDateTime.now())
                .expiryAt(LocalDateTime.now().plusMinutes(15))
                .user(user)
                .build();
        tokenRepository.save(token);
        return  generatesToken;
    }

    private String generateActivationCode(int length) {

        String characters = "123456789";
        StringBuilder codeBuilder = new StringBuilder();
        SecureRandom secureRandom = new SecureRandom();
        for (int i = 0; i < length; i++) {
            int randomIndex = secureRandom.nextInt(characters.length()); //0...9
            codeBuilder.append(characters.charAt(randomIndex));

        }

        return codeBuilder.toString();
    }

    public AuthenticationResponse authenticate(AuthenticationRequest request) {
        var auth = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getEmail(),
                        request.getPassword()
                )
        );
        var claims = new HashMap<String, Object>();
        var user = ((User)auth.getPrincipal());
        claims.put("fullName", user.fullName());
        var jwtToken = jwtService.generateToken(claims , user);
        return AuthenticationResponse.builder().token(jwtToken).build();
    }

    //@Transactional
    public void activateAccount(String token) throws MessagingException {
        Token saveToken = tokenRepository.findByToken(token).orElseThrow(() -> new RuntimeException("Invalid token"));

        if (LocalDateTime.now().isAfter(saveToken.getExpiryAt())){
            sendValidationEmail(saveToken.getUser());
            throw  new RuntimeException("Activation Token expired. A new token has been sent");

        }
         var user = userRepository.findById(saveToken.getUser().getId()).orElseThrow(() -> new RuntimeException("User not found"));
        user.setEnabled(true);
        userRepository.save(user);
        saveToken.setValidateAt(LocalDateTime.now());
        tokenRepository.save(saveToken);
    }*/
}
