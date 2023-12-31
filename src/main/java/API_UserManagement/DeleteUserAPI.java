package API_UserManagement;

import static spark.Spark.*;
import com.google.gson.Gson;
import java.sql.*;

public class DeleteUserAPI {

    public static void main(String[] args) {
        // Configure database connection
        String dbUrl = "jdbc:mysql://localhost:3306/pnt29";
        String dbUser = "root";
        String dbPassword = "";

        // Gson instance for JSON conversion
        Gson gson = new Gson();

        // Define a route to delete a user
        delete("/users/:user_id", (req, res) -> {
            // Extract user ID from the request path
            int userId = Integer.parseInt(req.params(":user_id"));

            try (Connection connection = DriverManager.getConnection(dbUrl, dbUser, dbPassword)) {
                // Check if the user exists
                if (userExists(connection, userId)) {
                    // Delete the user from the database
                    String deleteQuery = "DELETE FROM users WHERE user_id = ?";
                    try (PreparedStatement preparedStatement = connection.prepareStatement(deleteQuery)) {
                        preparedStatement.setInt(1, userId);

                        int rowsAffected = preparedStatement.executeUpdate();

                        if (rowsAffected > 0) {
                            res.status(200); // OK
                            return gson.toJson(new ApiResponse("success", "User deleted successfully"));
                        } else {
                            res.status(500); // Internal Server Error
                            return gson.toJson(new ApiResponse("error", "Error deleting user"));
                        }
                    }
                } else {
                    res.status(404); // Not Found
                    return gson.toJson(new ApiResponse("error", "User not found"));
                }
            } catch (SQLException e) {
                e.printStackTrace();
                res.status(500); // Internal Server Error
                return gson.toJson(new ApiResponse("error", "Error deleting user"));
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

    // Check if a user with the given ID exists
    private static boolean userExists(Connection connection, int userId) throws SQLException {
        String checkQuery = "SELECT 1 FROM users WHERE user_id = ?";
        try (PreparedStatement preparedStatement = connection.prepareStatement(checkQuery)) {
            preparedStatement.setInt(1, userId);
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                return resultSet.next();
            }
        }
    }
}
