class main {
public static void main(String[] args) {

        // Will serve all static file are under "/public" in classpath if the route isn't consumed by others routes.
        staticFileLocation("/public");

        get("/hello", (request, response) -> {
            return "Hello World!";
        });
    }
}
