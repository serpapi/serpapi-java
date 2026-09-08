# Contributing

## Key Goals

- Brand centric instead of search engine based
    -  No hard-coded logic per search engine
- Simple HTTP client (lightweight, reduced dependency)
    - No magic default values
    - Thread safe
- Easy extension
- Defensive code style (raise a custom exception)
- TDD - Test driven development
- Best API coding practice per platform
- KISS principles

## Code Quality Expectations


We use JUnit, GitHub Actions (see workflow), and Gradle.

Run the full test suite locally (integration tests call the live API when a key is present):

```bash
export SERPAPI_KEY='your_key'   # optional: without it, many tests skip; some tests require the key and will fail if unset
./gradlew test
```

Regenerate README.md from the template after editing examples:

```bash
make readme   # requires Ruby `erb`
```

How to build from source
Clone the repository:

```bash
git clone https://github.com/serpapi/serpapi-java.git
cd serpapi-java
```

Build (use the wrapper):

```bash
./gradlew build
```

The main library JAR is under build/libs/ (for example serpapi-1.1.0.jar, name follows version in build.gradle). Copy it into your project’s lib/ directory if you are not using Maven/Gradle dependency resolution.