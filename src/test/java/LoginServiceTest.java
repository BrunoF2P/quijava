import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.quijava.quijava.dao.UserDao;
import org.quijava.quijava.models.UserModel;
import org.quijava.quijava.services.LoginService;
import org.quijava.quijava.services.SessionDBService;
import org.quijava.quijava.services.SessionPreferencesService;
import org.quijava.quijava.utils.PasswordEncoder;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class LoginServiceTest {

    @Mock
    private SessionDBService sessionDBService;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private SessionPreferencesService sessionPreferencesService;

    @Mock
    private UserDao userDao;

    @InjectMocks
    private LoginService loginService;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testValidateLoginSuccess() {
        String username = "testUser";
        String password = "testPassword";
        UserModel user = new UserModel();
        user.setUsername(username);
        user.setPassword(password);

        when(userDao.findByUsername(username)).thenReturn(user);
        when(passwordEncoder.matches(password, user.getPassword())).thenReturn(true);

        boolean result = loginService.validateLogin(username, password);

        assertTrue(result);
        verify(userDao).findByUsername(username);
        verify(passwordEncoder).matches(password, user.getPassword());
    }

    @Test
    public void testValidateLoginFailure() {
        String username = "testUser";
        String password = "testPassword";

        when(userDao.findByUsername(username)).thenReturn(null);

        boolean result = loginService.validateLogin(username, password);

        assertFalse(result);
        verify(userDao).findByUsername(username);
        verify(passwordEncoder, never()).matches(anyString(), anyString());
    }

    @Test
    public void testValidateLoginEmptyUsername() {
        String username = "";
        String password = "testPassword";

        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            loginService.validateLogin(username, password);
        });

        assertEquals("Preencha todos os campos.", exception.getMessage());
    }

    @Test
    public void testValidateLoginEmptyPassword() {
        String username = "testUser";
        String password = "";

        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            loginService.validateLogin(username, password);
        });

        assertEquals("Preencha todos os campos.", exception.getMessage());
    }

}