package API_UserManagement;

import static spark.Spark.*;
import com.google.gson.Gson;
import java.sql.*;

public class UpdateUserAPI {

    public static void main(String[] args) {
        // Configure database connection
        String dbUrl = "jdbc:mysql://localhost:3306/pnt29";
        String dbUser = "root";
        String dbPassword = "";

        // Gson instance for JSON conversion
        Gson gson = new Gson();

        // Define a route to update a user
        put("/users/:user_id", (req, res) -> {
            // Extract user ID from the request path
            int userId = Integer.parseInt(req.params(":user_id"));

            try (Connection connection = DriverManager.getConnection(dbUrl, dbUser, dbPassword)) {
                // Parse JSON request body into a User object
                User updatedUser = gson.fromJson(req.body(), User.class);

                // Update the user in the database
                String updateQuery = "UPDATE users SET username = ?, email = ?, password = ?, address = ? WHERE user_id = ?";
                try (PreparedStatement preparedStatement = connection.prepareStatement(updateQuery)) {
                    preparedStatement.setString(1, updatedUser.getUsername());
                    preparedStatement.setString(2, updatedUser.getEmail());
                    preparedStatement.setString(3, updatedUser.getPassword());
                    preparedStatement.setString(4, updatedUser.getAddress());
                    preparedStatement.setInt(5, userId);

                    int rowsAffected = preparedStatement.executeUpdate();

                    if (rowsAffected > 0) {
                        res.status(200); // OK
                        return gson.toJson(new ApiResponse("success", "User updated successfully"));
                    } else {
                        res.status(404); // Not Found
                        return gson.toJson(new ApiResponse("error", "User not found or error updating user"));
                    }
                }
            } catch (SQLException e) {
                e.printStackTrace();
                res.status(500); // Internal Server Error
                return gson.toJson(new ApiResponse("error", "Error updating user"));
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
