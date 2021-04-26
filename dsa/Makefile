USER=$(shell id -u)
# get docker group id to avoid permission issues when installing slicer cli web
GID=$(shell getent group docker | cut -d: -f3)
CURRENT_UID=$(USER):$(GID)

build:
	CURRENT_UID=$(CURRENT_UID) docker-compose build \
	--build-arg user=$(USER) \
	--no-cache girder worker

clean:
	docker-compose rm -sf || true

# scale girder webapp
run: clean build
	CURRENT_UID=$(CURRENT_UID) user=$(USER) docker-compose up -d --scale girder=2	
