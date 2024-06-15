import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.quijava.quijava.dao.UserSessionDao;
import org.quijava.quijava.models.UserSessionModel;
import org.quijava.quijava.services.SessionDBService;

import java.time.LocalDateTime;
import java.util.Optional;
import static org.junit.jupiter.api.Assertions.*;
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
        int role = 1;
        int userId = 123;

        sessionDBService.createSession(username, role, userId);

        verify(userSessionDao, times(1)).save(any());
    }

    @Test
    void testGetLastSessionId() {
        String username = "testUser";
        int sessionId = 123;

        when(userSessionDao.getLastSessionIdForUser(username)).thenReturn(sessionId);

        assertEquals(sessionId, sessionDBService.getLastSessionId(username));
    }

    @Test
    void testDeleteSession() {
        int sessionId = 123;

        when(userSessionDao.delete(sessionId)).thenReturn(true);

        assertTrue(sessionDBService.deleteSession(sessionId));
    }

    @Test
    void testIsSessionValid() {
        int sessionId = 123;

        when(userSessionDao.existsById(sessionId)).thenReturn(true);

        assertTrue(sessionDBService.isSessionValid(sessionId));
    }

    @Test
    void testGetSessionIdForUser() {
        String username = "testUser";
        int sessionId = 123;

        when(userSessionDao.getSessionIdForUser(username)).thenReturn(Optional.of(sessionId));

        assertEquals(Optional.of(sessionId), sessionDBService.getSessionIdForUser(username));
    }

    @Test
    void testGetUsername() {
        int sessionId = 123;
        String username = "testUser";

        UserSessionModel sessionModel = new UserSessionModel();
        sessionModel.setUsername(username);

        when(userSessionDao.findById(sessionId)).thenReturn(Optional.of(sessionModel));

        assertEquals(username, sessionDBService.getUsername(sessionId));
    }

    @Test
    void testGetRole() {
        int sessionId = 123;
        int role = 1;

        UserSessionModel sessionModel = new UserSessionModel();
        sessionModel.setRole(role);

        when(userSessionDao.findById(sessionId)).thenReturn(Optional.of(sessionModel));

        assertEquals(role, sessionDBService.getRole(sessionId));
    }

    @Test
    void testGetUserId() {
        int sessionId = 123;
        int userId = 456;

        UserSessionModel sessionModel = new UserSessionModel();
        sessionModel.setUserId(userId);

        when(userSessionDao.findById(sessionId)).thenReturn(Optional.of(sessionModel));

        assertEquals(userId, sessionDBService.getUserId(sessionId));
    }
}
