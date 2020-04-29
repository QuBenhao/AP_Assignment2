import model.database.User;
import org.junit.After;
import org.junit.Before;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.jupiter.api.Assertions.*;

class UserTest {
    private Connection con = null;

    @BeforeEach
    void setUp() {
        try {
            Class.forName("org.hsqldb.jdbc.JDBCDriver");
            con = DriverManager.getConnection("jdbc:hsqldb:file:database/UniLink", "SA", "");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }

    @AfterEach
    void tearDown() {
        try {
            con.close();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }

    @Test
    void login() {
        assertFalse(User.Login(con,"test","1234"));
        assertTrue(User.Login(con,"s3773865",""));
    }

    @Test
    void register() {
        String input[] = new String[3];
        input[0] = "test";
        input[1] = "test";
        input[2] = "1234";
        assertFalse(User.Register(con,input));

        input[0] = "s3773865";
        input[1] = "test";
        input[2] = "1234";
        assertFalse(User.Register(con,input));

        input[0] = "s3795577";
        input[1] = "Humiki";
        input[2] = "";
        assertFalse(User.Register(con,input));

    }
}