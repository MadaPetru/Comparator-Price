package ro.adi.comparatorprices.security.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.test.context.ContextConfiguration;
import ro.adi.comparatorprices.security.SecurityMock;
import ro.adi.comparatorprices.security.jpa.entity.UserEntity;
import ro.adi.comparatorprices.security.jpa.repository.UserRepository;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@ContextConfiguration
class UserServiceTest {

    @InjectMocks
    private UserService classUnderTest;
    @Mock
    private UserRepository userRepository;

    @Test
    void loadUserByUsername_shouldRetrieve() {
        UserEntity mockUser = SecurityMock.getUserMock();
        String username = "USER";
        when(userRepository.findByUsername(anyString())).thenReturn(java.util.Optional.of(mockUser));

        UserDetails actualUserDetails = classUnderTest.loadUserByUsername(username);

        verify(userRepository).findByUsername(anyString());
        assertEquals(mockUser.getUsername(), actualUserDetails.getUsername());
        assertEquals(mockUser.getPassword(), actualUserDetails.getPassword());
    }

    @Test
    void loadUserByUsername_shouldFail() {
        String username = "USER";
        String expectedMessage = username;
        when(userRepository.findByUsername(anyString())).thenThrow(new UsernameNotFoundException(username));

        UsernameNotFoundException exception = assertThrows(UsernameNotFoundException.class, () -> {
            classUnderTest.loadUserByUsername(username);
        });

        verify(userRepository).findByUsername(anyString());
        assertEquals(exception.getMessage(), expectedMessage);
    }
}