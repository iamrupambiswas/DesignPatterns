# Builder Pattern

## Problem
You need to construct a complex object that has **many optional fields** — e.g. a `User` with name, email, age, address, phone, etc. Some are required, most are optional, and you want readable, safe object creation.

## Naive Solution
### Telescoping constructors
```java
public class User {
    private String name;
    private String email;
    private int age;
    private String address;
    private String phone;

    public User(String name, String email) { ... }
    public User(String name, String email, int age) { ... }
    public User(String name, String email, int age, String address) { ... }
    public User(String name, String email, int age, String address, String phone) { ... }
}
```
Or a giant setter-based approach:
```java
User user = new User();
user.setName("Alex");
user.setEmail("alex@mail.com");
user.setAge(30);
```

## Why It Becomes Problematic
- **Telescoping constructors**: unreadable calls like `new User("Alex", "alex@mail.com", 0, null, null)` — you can't tell which argument is which.
- **Setters**: object is mutable and can exist in a half-built, inconsistent state between `new User()` and the last setter call. Not thread-safe either.
- Adding a new field means touching every constructor overload or hoping someone remembers to call the right setter.
- No way to enforce "required fields must be set" at compile time.

## Pattern
**Builder**: separate the construction of a complex object from its representation, using a step-by-step fluent API, so the same construction process can create different configurations — and the final object is immutable.

Key ideas:
- Object's constructor is `private`.
- A nested `Builder` class collects field values via chained methods.
- `build()` validates and returns the final immutable object.

## Java Implementation
```java
public class User {
    private final String name;
    private final String email;
    private final int age;
    private final String address;
    private final String phone;

    private User(Builder builder) {
        this.name = builder.name;
        this.email = builder.email;
        this.age = builder.age;
        this.address = builder.address;
        this.phone = builder.phone;
    }

    public static class Builder {
        private final String name;   // required
        private final String email;  // required
        private int age;
        private String address;
        private String phone;

        public Builder(String name, String email) {
            this.name = name;
            this.email = email;
        }

        public Builder age(int age) {
            this.age = age;
            return this;
        }

        public Builder address(String address) {
            this.address = address;
            return this;
        }

        public Builder phone(String phone) {
            this.phone = phone;
            return this;
        }

        public User build() {
            if (name == null || email == null) {
                throw new IllegalStateException("name and email are required");
            }
            return new User(this);
        }
    }
}
```
Usage:
```java
User user = new User.Builder("Alex", "alex@mail.com")
        .age(30)
        .address("221B Baker Street")
        .build();
```
Required fields (`name`, `email`) are enforced via the `Builder` constructor; optional fields are chained fluently; the final `User` is fully immutable.

## Real Spring Example
Spring itself leans on the Builder pattern in a few core places:

```java
// UriComponentsBuilder — building URLs step by step
UriComponents uri = UriComponentsBuilder.newInstance()
        .scheme("https")
        .host("api.example.com")
        .path("/orders/{id}")
        .queryParam("status", "PAID")
        .build();
```

```java
// ResponseEntity — building HTTP responses
ResponseEntity<String> response = ResponseEntity
        .status(HttpStatus.CREATED)
        .header("X-Trace-Id", traceId)
        .body("Order created");
```

```java
// MockMvc request builders in tests
mockMvc.perform(post("/orders")
        .contentType(MediaType.APPLICATION_JSON)
        .content(jsonPayload))
        .andExpect(status().isCreated());
```
All of these use the same idea: chained, readable calls that assemble a complex, effectively immutable object at the end.

## When NOT to Use It
- When the object has **few fields** (2-3) — a normal constructor is simpler and the Builder is overkill boilerplate.
- When fields rarely change and there's no "optional vs required" complexity to manage.
- When you need mutable objects that change frequently after creation — Builder is meant to produce a finished, often immutable, result.
- When a simpler alternative fits better, e.g. **static factory methods** for a couple of fixed variants, instead of a full Builder for flexibility you don't need.