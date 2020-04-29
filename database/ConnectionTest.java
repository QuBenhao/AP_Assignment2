
import java.sql.*;

public class ConnectionTest {

    public static void main(String[] args) {

        final String DB_NAME = "UniLink";

        //use try-with-resources Statement
        try (Connection con = getConnection(DB_NAME)) {

            System.out.println("Connection to database "
                    + DB_NAME + " created successfully");
            Statement query = con.createStatement();
            ResultSet rs = query.executeQuery("select * from USER");
            if(rs.next()){
               System.out.println(rs.getString(1));
            }
            con.close();

        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    public static Connection getConnection(String dbName)
            throws SQLException, ClassNotFoundException {
        //Registering the HSQLDB JDBC driver
        Class.forName("org.hsqldb.jdbc.JDBCDriver");

        /* Database files will be created in the "database"
         * folder in the project. If no username or password is
         * specified, the default SA user and an empty password are used */
        Connection con = DriverManager.getConnection
                ("jdbc:hsqldb:file:database/" + dbName, "SA", "");
        return con;
    }
}

