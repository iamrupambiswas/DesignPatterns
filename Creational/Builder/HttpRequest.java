package Creational.Builder;

public class HttpRequest {
    private final String url;
    private final String method;
    private final String body;
    private final int timeout;

    private HttpRequest(Builder builder) {
        this.url = builder.url;
        this.method = builder.method;
        this.body = builder.body;
        this.timeout = builder.timeout;
    }

    public static class Builder {
        private final String url;
        private final String method;
        private String body = "";
        private int timeout = 5000;

        public Builder(String url, String method) {
            this.url = url;
            this.method = method;
        }

        public Builder setBody(String body) {
            this.body = body;
            return this;
        }

        public Builder setTimeout(int timeout) {
            this.timeout = timeout;
            return this;
        }

        public HttpRequest build() {
            return new HttpRequest(this);
        }

    }
}
