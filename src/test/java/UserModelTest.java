
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.quijava.quijava.models.UserModel;

import java.time.Instant;

public class UserModelTest {
    @Test
    void testDateTimeNow(){
        UserModel newUser = new UserModel();

        Instant createdAt = Instant.now();
        Instant updatedAt = Instant.now();

        newUser.setCreatedAt(createdAt);
        newUser.setUpdatedAt(updatedAt);

        Assertions.assertEquals(createdAt, newUser.getCreatedAt());
        Assertions.assertEquals(updatedAt, newUser.getUpdatedAt());
    }

    @Test
    void testA_addId_addUser(){
        UserModel newUser = new UserModel();

        newUser.setId(1);
        newUser.setUsername("Teste");
        newUser.setPassword("TesteSenha");

        Assertions.assertEquals(1, newUser.getId());
        Assertions.assertEquals("Teste", newUser.getUsername());
        Assertions.assertEquals("TesteSenha", newUser.getPassword());


    }

}
