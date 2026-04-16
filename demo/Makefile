.PHONY: build

ifndef SERPAPI_KEY
$(error "SERPAPI_KEY must be defined")
else
$(info "found SERPAPI_KEY variable")
endif

ifeq ($(strip $(SERPAPI_KEY)),)
$(error "SERPAPI_KEY must be non-empty")
endif

all: init clean build run

# Ensure wrapper script is executable (wrapper files should be committed under demo/)
init:
	chmod +x ./gradlew

clean:
	./gradlew clean
	
# optional step if building from local source instead of downloading jar file form jitpack.io
dep:
	$(MAKE) -C ../ build
	rm -rf libs
	mkdir -p libs
	cp ../build/libs/* libs

build: clean
	@./gradlew build

run:
	@env SERPAPI_KEY="$(SERPAPI_KEY)" ./gradlew run
