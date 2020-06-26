current_branch := $(shell git rev-parse --abbrev-ref HEAD)
build:
	docker build -t msk-mind/hive .

clean:
	docker stop hive || true
	docker rm hive || true

run: clean build
	docker run --name hive \
	-p 8020:8020 \
	-p 10000:10000 \
	-p 50111:50111 \
	msk-mind/hive:latest
