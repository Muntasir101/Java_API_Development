package API_UserManagement;

import static spark.Spark.*;
import com.google.gson.Gson;
import java.sql.*;

public class CreateUserAPI {

    public static void main(String[] args) {
        // Configure database connection
        String dbUrl = "jdbc:mysql://localhost:3306/pnt29";
        String dbUser = "root";
        String dbPassword = "";

        // Gson instance for JSON conversion
        Gson gson = new Gson();

        // Define a route to create a new user
        post("/users", (req, res) -> {
            try (Connection connection = DriverManager.getConnection(dbUrl, dbUser, dbPassword)) {
                // Parse JSON request body into a User object
                User newUser = gson.fromJson(req.body(), User.class);

                // Insert the new user into the database
                String insertQuery = "INSERT INTO users (username, email, password, address) VALUES (?, ?, ?, ?)";
                try (PreparedStatement preparedStatement = connection.prepareStatement(insertQuery)) {
                    preparedStatement.setString(1, newUser.getUsername());
                    preparedStatement.setString(2, newUser.getEmail());
                    preparedStatement.setString(3, newUser.getPassword());
                    preparedStatement.setString(4, newUser.getAddress());

                    int rowsAffected = preparedStatement.executeUpdate();

                    if (rowsAffected > 0) {
                        res.status(201); // Created
                        return gson.toJson(new ApiResponse("success", "User created successfully"));
                    } else {
                        res.status(500); // Internal Server Error
                        return gson.toJson(new ApiResponse("error", "Error creating user"));
                    }
                }
            } catch (SQLException e) {
                e.printStackTrace();
                res.status(500); // Internal Server Error
                return gson.toJson(new ApiResponse("error", "Error creating user"));
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

        // Constructor
        public User(String username, String email, String password, String address) {
            this.username = username;
            this.email = email;
            this.password = password;
            this.address = address;
        }

        // Getter methods
        public String getUsername() {
            return username;
        }

        public String getEmail() {
            return email;
        }

        public String getPassword() {
            return password;
        }

        public String getAddress() {
            return address;
        }
    }

    // ApiResponse class for representing the response in JSON format
    private static class ApiResponse {
        private final String status;
        private final String message;

        // Constructor
        public ApiResponse(String status, String message) {
            this.status = status;
            this.message = message;
        }

        // Getter methods
        public String getStatus() {
            return status;
        }

        public String getMessage() {
            return message;
        }
    }
}
