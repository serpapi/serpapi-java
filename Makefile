.PHONY: build

all: clean init build test readme doc

# clean
clean:
	./gradlew clean

# Initialize gradle wrapper
init:
	gradle wrapper
	chmod +x ./gradlew

# Run gradle test with information
test:
	./gradlew test --info

# Run test coverage
coverage:
	./gradlew test jacocoTestReport
	@echo "Coverage report: build/reports/jacoco/test/html/index.html"

# Build the project
build: clean
	./gradlew build publishToMavenLocal -x test
	@echo "see build/lib"

# Build the demo project
oobt: build
	$(MAKE) -C demo all

# Ruby must be installed (ERB is located under $GEM_HOME/bin or under Ruby installation)
readme:
	erb -T '-' README.md.erb > README.md

# Build the javadoc
doc:
	./gradlew javadoc --info --warning-mode all

# Create a release using GitHub
release: doc build
	@echo "drag drop file"
	open build/libs/
	open build/distributions/
	open -a "Google Chrome" https://github.com/serpapi/serpapi-java/releases
