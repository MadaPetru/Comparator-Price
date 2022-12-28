package ro.adi.comparatorprices.security.jpa.entity;

import org.junit.jupiter.api.Test;

import java.util.Collections;

import static org.junit.jupiter.api.Assertions.*;

class UserEntityTest {
    @Test
    void userEntity_shouldBeEquals() {
        UserEntity user1 = new UserEntity();
        user1.setId(1L);
        user1.setUsername("USER");
        user1.setPassword("");
        user1.setRoles(Collections.emptySet());
        UserEntity user2 = new UserEntity();
        user2.setId(1L);
        user2.setUsername("USER");
        user2.setPassword("");
        user2.setRoles(Collections.emptySet());
        assertTrue(user1.equals(user2) && user1.equals(user1));
        assertEquals(user1.getId(), user2.getId());
        assertEquals(user1.getUsername(), user2.getUsername());
        assertEquals(user1.getPassword(), user2.getPassword());
        assertEquals(user1.getRoles(), user2.getRoles());
    }

    @Test
    void userEntity_shouldNotBeEquals() {
        UserEntity user1 = new UserEntity();
        RoleEntity user2 = new RoleEntity();
        assertFalse(user1.equals(user2));
    }

}