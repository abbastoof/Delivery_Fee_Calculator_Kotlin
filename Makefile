ifeq ($(OS), Windows_NT)
    GRADLE_CMD = gradlew
else
    GRADLE_CMD = ./gradlew
endif


all: build run

.PHONY: all build run down clean test
build:
	chmod +x gradlew
	$(GRADLE_CMD) build
	docker compose up -d --build
test:
	$(GRADLE_CMD) test
down:
	docker compose down
clean:
	@containers=$$(docker ps -a -q --filter ancestor=delivery_fee); \
	if [ -n "$$containers" ]; then \
		echo "Stopping and removing containers using delivery_fee image..."; \
		docker rm -f $$containers; \
	else \
		echo "No containers using delivery_fee image."; \
	fi
	@if docker image inspect delivery_fee > /dev/null 2>&1; then \
    	docker image rm delivery_fee; \
		echo "Removed delivery_fee image."; \
	else \
    	echo "Image delivery_fee not found."; \
	fi
