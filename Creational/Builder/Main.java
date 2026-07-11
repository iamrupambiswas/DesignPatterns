package Creational.Builder;

public class Main {
    public static void main(String args[]) {
        // User user = new User.Builder()
        // .setName("Rupam Biswas")
        // .build();
        // user.display();

        // HttpRequest request = new HttpRequest.Builder("https:/www.example.com", "POST")
        //     .setBody("{ \"id\": 1 }")
        //     .setTimeout(2000)
        //     .build();
        // System.out.print(request);

        DatabaseConfig db = new DatabaseConfig.Builder("localhost", 3306)
            .setDatabase("Employee")
            .build();
        System.out.print(db);
    }
}
