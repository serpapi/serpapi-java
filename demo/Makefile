.PHONY: build

ifndef API_KEY
$(error "API_KEY must be defined")
else
$(info "found API_KEY variable")
endif

all: init clean build run

init:
	gradle wrapper
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
	@./gradlew run --args="$(API_KEY)"
