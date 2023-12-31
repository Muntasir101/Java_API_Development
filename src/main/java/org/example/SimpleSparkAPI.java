package org.example;

import static spark.Spark.*;

public class SimpleSparkAPI {

    public static void main(String[] args) {
        // Define a route
        get("/hello", (req, res) -> "Hello, Spark!");

        // Stop the Spark server when the application exits
        awaitInitialization();
    }
}
