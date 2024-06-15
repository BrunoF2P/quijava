import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.quijava.quijava.dao.UserDao;
import org.quijava.quijava.services.RegisterService;
import org.quijava.quijava.services.SessionDBService;
import org.quijava.quijava.services.SessionPreferencesService;
import org.quijava.quijava.utils.PasswordEncoder;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class RegisterServiceTest {

    @Mock
    private UserDao userDao;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private SessionDBService sessionDBService;

    @Mock
    private SessionPreferencesService sessionPreferencesService;

    @InjectMocks
    private RegisterService registerService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testValidateUsername() {
        assertTrue(registerService.validateUsername("username123"));
        assertFalse(registerService.validateUsername("user$name"));
    }

    @Test
    void testValidatePassword() {
        assertTrue(registerService.validatePassword("password123"));
        assertFalse(registerService.validatePassword("pass"));
    }

    @Test
    void testCheckPasswordsMatch() {
        assertTrue(registerService.checkPasswordsMatch("password123", "password123"));
        assertFalse(registerService.checkPasswordsMatch("password123", "password456"));
    }

    @Test
    void testUserExists() {
        when(userDao.existsByUsername("existingUser")).thenReturn(true);
        assertTrue(registerService.userExists("existingUser"));
        assertFalse(registerService.userExists("nonExistingUser"));
    }
}