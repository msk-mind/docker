IMAGE=luna-tutorial:0.0.1
CONTAINER=luna-tutorial

.PHONY: all help build clean test run
.DEFAULT_GOAL := help

# help menu
define BROWSER_PYSCRIPT
import os, webbrowser, sys

try:
	from urllib import pathname2url
except:
	from urllib.request import pathname2url

webbrowser.open("file://" + pathname2url(os.path.abspath(sys.argv[1])))
endef
export BROWSER_PYSCRIPT

define PRINT_HELP_PYSCRIPT
import re, sys

for line in sys.stdin:
	match = re.match(r'^([a-zA-Z_-]+):.*?## (.*)$$', line)
	if match:
		target, help = match.groups()
		print("%-20s %s" % (target, help))
endef
export PRINT_HELP_PYSCRIPT


BROWSER := python -c "$$BROWSER_PYSCRIPT"

help:
	@python -c "$$PRINT_HELP_PYSCRIPT" < $(MAKEFILE_LIST)


clean:      ## cleanup all images volumes and containers
	docker system prune -a -f

clean-image:  ## remove luna tutorial image
	docker rmi -f $(IMAGE)

build:      ## build luna tutorial docker image
	DOCKER_BUILDKIT=0 docker build -t $(IMAGE) .

run:       ## run the luna-tutorial container
	docker run -it --name $(CONTAINER) -v images:/home/laluna/images -p 127.0.0.1:8888:8888/tcp $(IMAGE)

rm:      ## remove the stopped luna-tutorial container
	docker rm $(CONTAINER)

exec:    ## enter the luna-tutorial container
	docker exec -it $(CONTAINER) /bin/bash