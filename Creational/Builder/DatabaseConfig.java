package Creational.Builder;

public class DatabaseConfig {
    private final String host;
    private final int port;
    private final String userName;
    private final String password;
    private final String database;

    private DatabaseConfig(Builder builder) {
        this.host = builder.host;
        this.port = builder.port;
        this.userName = builder.userName;
        this.password = builder.password;
        this.database = builder.database;
    }

    public static class Builder {
        private final String host;
        private final int port;
        private String userName = "root";
        private String password = "root";
        private String database = "";

        public Builder(String host, int port) {
            this.host = host;
            this.port = port;
        }

        public Builder setUserName(String userName) {
            this.userName = userName;
            return this;
        }

        public Builder setPassword(String password) {
            this.password = password;
            return this;
        }

        public Builder setDatabase(String database) {
            this.database = database;
            return this;
        }

        public DatabaseConfig build() {
            return new DatabaseConfig(this);
        }
    }
}
