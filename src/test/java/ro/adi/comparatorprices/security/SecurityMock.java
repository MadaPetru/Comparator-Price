package ro.adi.comparatorprices.security;

import lombok.experimental.UtilityClass;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import ro.adi.comparatorprices.security.dto.request.AuthenticationRequestDto;
import ro.adi.comparatorprices.security.jpa.entity.RoleEntity;
import ro.adi.comparatorprices.security.jpa.entity.UserEntity;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

@UtilityClass
public class SecurityMock {
    public UserEntity getUserMock() {
        UserEntity mock = new UserEntity();
        Set<RoleEntity> roles = getRolesMock();
        mock.setPassword("adi");
        mock.setUsername("USER");
        mock.setId(1L);
        mock.setRoles(roles);
        return mock;
    }

    public HashSet<RoleEntity> getRolesMock() {
        HashSet<RoleEntity> mock = new HashSet<>();
        mock.add(getRoleMock());
        return new HashSet<>();
    }

    public RoleEntity getRoleMock() {
        RoleEntity roleMock = new RoleEntity();
        roleMock.setName("ROLE_USER");
        roleMock.setId(1L);
        return roleMock;
    }

    public AuthenticationRequestDto getRequestDtoMock() {
        String username = "USER";
        String password = "adi";
        return AuthenticationRequestDto.builder().password(password).username(username).build();
    }

    public UserDetails getUserDetailsMock() {
        UserEntity userEntityMock = getUserMock();
        String username = userEntityMock.getUsername();
        String password = userEntityMock.getPassword();
        Collection<? extends GrantedAuthority> authorities = getRolesMock().stream().map(RoleEntity::getName)
                .map(SimpleGrantedAuthority::new).collect(Collectors.toSet());
        User userMock = new User(username, password, authorities);
        return userMock;
    }
}
