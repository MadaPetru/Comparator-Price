package ro.adi.comparatorprices.security.config;

import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import ro.adi.comparatorprices.security.service.UserService;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

import static org.springframework.http.HttpHeaders.AUTHORIZATION;

@Component
@RequiredArgsConstructor
public class JwtAuthFilter extends OncePerRequestFilter {

    private final UserService userService;
    private final JwtUtils jwtUtils;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws IOException, ServletException {
        final String authHeader = request.getHeader(AUTHORIZATION);
        final String username;
        final String jwtToken;
        if (isTryToLoginWithUsernameAndPassword(authHeader)) {
            filterChain.doFilter(request, response);
            return;
        }
        jwtToken = authHeader.substring(7);
        username = jwtUtils.extractUsername(jwtToken);
        if (isCorrectUsernameAndIsNotAuthenticate(username)) {
            UserDetails userDetails = userService.loadUserByUsername(username);
            final boolean isTokenValid = jwtUtils.validateToken(jwtToken, userDetails);
            if (isTokenValid) {
                updateSecurityContextAuthentication(request, userDetails);
            }
        }
        filterChain.doFilter(request, response);
    }

    private void updateSecurityContextAuthentication(HttpServletRequest request, UserDetails userDetails) {
        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(userDetails,
                null, userDetails.getAuthorities());
        authenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
        SecurityContextHolder.getContext().setAuthentication(authenticationToken);
    }

    private boolean isCorrectUsernameAndIsNotAuthenticate(String username) {
        return username != null && SecurityContextHolder.getContext().getAuthentication() == null;
    }

    private boolean isTryToLoginWithUsernameAndPassword(String authHeader) {
        return authHeader == null || !authHeader.startsWith("Bearer ");
    }
}
