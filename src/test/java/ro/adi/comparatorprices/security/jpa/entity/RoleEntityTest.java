package ro.adi.comparatorprices.security.jpa.entity;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class RoleEntityTest {
    @Test
    void roleEntity_shouldBeEquals() {
        RoleEntity role1 = new RoleEntity();
        role1.setId(1L);
        role1.setName("ROLE_USER");
        RoleEntity role2 = new RoleEntity();
        role2.setId(1L);
        role2.setName("ROLE_USER");
        assertTrue(role1.equals(role2) && role1.equals(role1));
        assertEquals(role1.getId(), role2.getId());
        assertEquals(role1.getName(), role2.getName());
    }

    @Test
    void roleEntity_shouldNotBeEquals() {
        RoleEntity role1 = new RoleEntity();
        UserEntity role2 = new UserEntity();
        assertFalse(role1.equals(role2));
    }
}