public class Logger {
    private Logger() {}

    private static Logger instance = new Logger();

    public static Logger getInstance() {
        return instance;
    }

    public void log(String message) {
        System.out.println(message);
    }
}