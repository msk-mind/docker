ORG=mskmind
NAME=qupath-stardist
QPVER=0.4.3
IMAGE=${NAME}:${QPVER}-1

.PHONY: help build build-intel build-apple upload deploy
.DEFAULT_GOAL := help

# See https://dwmkerr.com/makefile-help-command/
help: # Show help for each of the Makefile targets
	@grep -E '^[a-zA-Z0-9 -]+:.*#'  Makefile | sort | while read -r l; do printf "\033[1;32m$$(echo $$l | cut -f 1 -d':')\033[00m:$$(echo $$l | cut -f 2- -d'#')\n"; done


build: build-intel # Build the docker image for intel

build-apple: # Build the docker image for linux/arm64
	docker buildx build --platform linux/arm64 -t $(IMAGE) .

build-intel: # Build the docker image for linux/amd64
	docker buildx build --platform linux/amd64 -t $(IMAGE) .

upload: # Upload image to DockerHub
	docker tag $(IMAGE) $(ORG)/$(IMAGE)
	docker push $(ORG)/$(IMAGE)

deploy: # Upload image to DockerHub
	docker tag $(IMAGE) $(ORG)/$(NAME):current
	docker push $(ORG)/$(NAME):current

