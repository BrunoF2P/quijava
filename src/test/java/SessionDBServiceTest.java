import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.quijava.quijava.dao.UserSessionDao;
import org.quijava.quijava.models.UserSessionModel;
import org.quijava.quijava.services.SessionDBService;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class SessionDBServiceTest {

    @Mock
    private UserSessionDao userSessionDao;

    @InjectMocks
    private SessionDBService sessionDBService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testCreateSession() {
        String username = "testUser";
        Integer role = 1;
        Integer userId = 123;

        sessionDBService.createSession(username, role, userId);

        verify(userSessionDao, times(1)).save(any(UserSessionModel.class));
    }

    @Test
    void testGetLastSessionId() {
        String username = "testUser";
        Integer expectedSessionId = 123;

        when(userSessionDao.getLastSessionIdForUser(username)).thenReturn(expectedSessionId);

        Integer actualSessionId = sessionDBService.getLastSessionId(username);

        assertEquals(expectedSessionId, actualSessionId);
    }

    @Test
    void testDeleteSession() {
        Integer sessionId = 123;

        sessionDBService.deleteSession(sessionId);

        verify(userSessionDao, times(1)).deleteById(sessionId);
    }

    @Test
    void testIsSessionValid() {
        Integer sessionId = 123;

        when(userSessionDao.existsById(sessionId)).thenReturn(true);

        assertTrue(sessionDBService.isSessionValid(sessionId));
    }

    @Test
    void testIsSessionValid_WhenNotExists() {
        Integer sessionId = 999;

        when(userSessionDao.existsById(sessionId)).thenReturn(false);

        assertFalse(sessionDBService.isSessionValid(sessionId));
    }

    @Test
    void testGetSessionIdForUser() {
        String username = "testUser";
        Integer sessionId = 123;

        UserSessionModel sessionModel = new UserSessionModel();
        sessionModel.setId(sessionId);
        sessionModel.setUsername(username);

        when(userSessionDao.findByUsername(username)).thenReturn(Optional.of(sessionModel));

        Optional<Integer> result = sessionDBService.getSessionIdForUser(username);

        assertTrue(result.isPresent());
        assertEquals(sessionId, result.get());
    }

    @Test
    void testGetSessionIdForUser_WhenNotFound() {
        String username = "unknownUser";

        when(userSessionDao.findByUsername(username)).thenReturn(Optional.empty());

        Optional<Integer> result = sessionDBService.getSessionIdForUser(username);

        assertFalse(result.isPresent());
    }

    @Test
    void testGetUsername() {
        Integer sessionId = 123;
        String expectedUsername = "testUser";

        UserSessionModel sessionModel = new UserSessionModel();
        sessionModel.setUsername(expectedUsername);

        when(userSessionDao.findById(sessionId)).thenReturn(Optional.of(sessionModel));

        Optional<String> result = sessionDBService.getUsername(sessionId);

        assertTrue(result.isPresent());
        assertEquals(expectedUsername, result.get());
    }

    @Test
    void testGetUsername_WhenNotFound() {
        Integer sessionId = 999;

        when(userSessionDao.findById(sessionId)).thenReturn(Optional.empty());

        Optional<String> result = sessionDBService.getUsername(sessionId);

        assertFalse(result.isPresent());
    }

    @Test
    void testGetRole() {
        Integer sessionId = 123;
        Integer expectedRole = 1;

        UserSessionModel sessionModel = new UserSessionModel();
        sessionModel.setRole(expectedRole);

        when(userSessionDao.findById(sessionId)).thenReturn(Optional.of(sessionModel));

        Optional<Integer> result = sessionDBService.getRole(sessionId);

        assertTrue(result.isPresent());
        assertEquals(expectedRole, result.get());
    }

    @Test
    void testGetRole_WhenNotFound() {
        Integer sessionId = 999;

        when(userSessionDao.findById(sessionId)).thenReturn(Optional.empty());

        Optional<Integer> result = sessionDBService.getRole(sessionId);

        assertFalse(result.isPresent());
    }

    @Test
    void testGetUserId() {
        Integer sessionId = 123;
        Integer expectedUserId = 456;

        UserSessionModel sessionModel = new UserSessionModel();
        sessionModel.setUserId(expectedUserId);

        when(userSessionDao.findById(sessionId)).thenReturn(Optional.of(sessionModel));

        Optional<Integer> result = sessionDBService.getUserId(sessionId);

        assertTrue(result.isPresent());
        assertEquals(expectedUserId, result.get());
    }

    @Test
    void testGetUserId_WhenNotFound() {
        Integer sessionId = 999;

        when(userSessionDao.findById(sessionId)).thenReturn(Optional.empty());

        Optional<Integer> result = sessionDBService.getUserId(sessionId);

        assertFalse(result.isPresent());
    }
}
