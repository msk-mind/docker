USER=$(shell id -u)
GID=$(shell getent group docker | cut -d: -f3)
CURRENT_UID=$(USER):$(GID)

build:
	docker-compose build \
	--build-arg user=$(USER) \
	--no-cache girder worker

clean:
	docker-compose rm -sf || true

run: clean build
	CURRENT_UID=$(CURRENT_UID) user=$(USER) docker-compose up -d	
