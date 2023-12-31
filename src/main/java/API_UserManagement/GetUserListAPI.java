package API_UserManagement;

import static spark.Spark.*;
import com.google.gson.Gson;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class GetUserListAPI {

    public static void main(String[] args) {
        // Configure database connection
        String dbUrl = "jdbc:mysql://localhost:3306/pnt29";
        String dbUser = "root";
        String dbPassword = "";

        // Gson instance for JSON conversion
        Gson gson = new Gson();

        // Define a route to get the list of users as JSON
        get("/users", (req, res) -> {
            try (Connection connection = DriverManager.getConnection(dbUrl, dbUser, dbPassword)) {
                // Execute the query
                Statement statement = connection.createStatement();
                ResultSet resultSet = statement.executeQuery("SELECT * FROM users");

                // Process the results and convert to a list of User objects
                List<User> userList = new ArrayList<>();
                while (resultSet.next()) {
                    String username = resultSet.getString("username");
                    String email = resultSet.getString("email");
                    String password = resultSet.getString("password");
                    String address = resultSet.getString("address");

                    userList.add(new User(username, email, password, address));
                }

                // Convert the list to JSON
                String json = gson.toJson(userList);

                res.type("application/json"); // Set the response type to JSON
                return json;
            } catch (SQLException e) {
                e.printStackTrace();
                res.status(500); // Internal Server Error
                return "Error fetching users";
            }
        });

        // Stop the Spark server when the application exits
        awaitInitialization();
    }

    // User class for representing a user
    private static class User {
        private final String username;
        private final String email;
        private final String password;
        private final String address;

        public User(String username, String email, String password, String address) {
            this.username = username;
            this.email = email;
            this.password = password;
            this.address = address;
        }
    }
}
