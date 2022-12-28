package ro.adi.comparatorprices.security.service;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import ro.adi.comparatorprices.security.jpa.entity.RoleEntity;
import ro.adi.comparatorprices.security.jpa.entity.UserEntity;
import ro.adi.comparatorprices.security.jpa.repository.UserRepository;

import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserService implements UserDetailsService {
    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        UserEntity user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException(username));
        User userDetail = getUserDetail(user);
        return userDetail;
    }

    private User getUserDetail(UserEntity user) {
        String usernameEntity = user.getUsername();
        String passwordEntity = user.getPassword();
        Set<SimpleGrantedAuthority> roles = user.getRoles().stream().map(RoleEntity::getName)
                .map(SimpleGrantedAuthority::new).collect(Collectors.toSet());
        User userDetail = new User(usernameEntity, passwordEntity, roles);
        return userDetail;
    }
}
