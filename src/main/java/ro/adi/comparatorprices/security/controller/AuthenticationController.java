package ro.adi.comparatorprices.security.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import ro.adi.comparatorprices.security.config.JwtUtils;
import ro.adi.comparatorprices.security.dto.request.AuthenticationRequestDto;
import ro.adi.comparatorprices.security.service.UserService;

@RestController
@RequiredArgsConstructor
public class AuthenticationController {
    private final AuthenticationManager authenticationManager;
    private final UserService userService;
    private final JwtUtils jwtUtils;

    @PostMapping("/login")
    public String authenticate(@RequestBody AuthenticationRequestDto request) {
        UsernamePasswordAuthenticationToken authenticationUser = new UsernamePasswordAuthenticationToken(
                request.getUsername(), request.getPassword());
        authenticationManager.authenticate(authenticationUser);
        final UserDetails userDetails = userService.loadUserByUsername(request.getUsername());
        return jwtUtils.generateToken(userDetails);
    }
}
