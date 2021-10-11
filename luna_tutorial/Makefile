IMAGE=$(USER)-luna-tutorial:0.0.1
CONTAINER=$(USER)-luna-tutorial
PWD=$(shell pwd)

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
	docker system prune --volumes -f

clean-image:  ## remove luna tutorial image
	docker rmi -f $(IMAGE)

build:      ## build luna tutorial docker image
	DOCKER_BUILDKIT=0 time docker build -t $(IMAGE) .

run:       ## run the luna-tutorial container
	mkdir -p data
	docker run -it --name $(CONTAINER) -v $(PWD)/data:/home/laluna/data -p 127.0.0.1:8888:8888/tcp $(IMAGE)
	docker rm $(CONTAINER)

exec:      ## enter the luna-tutorial container
	docker exec -it $(CONTAINER) /bin/bash

backup:  ## backup notebook changes from luna-tutorial container to the host notebooks dir
	mv -f notebooks/dataset-prep.ipynb notebooks/dataset-prep.ipynb.bk
	mv -f notebooks/dsa-tools.ipynb notebooks/dsa-tools.ipynb.bk
	mv -f notebooks/end-to-end-pipeline.ipynb notebooks/end-to-end-pipeline.ipynb.bk
	mv -f notebooks/inference-and-visualization.ipynb notebooks/inference-and-visualization.ipynb.bk
	mv -f notebooks/model-training.ipynb notebooks/model-training.ipynb.bk
	mv -f notebooks/tiling.ipynb notebooks/tiling.ipynb.bk
	docker cp luna-tutorial:/home/laluna/dataset-prep.ipynb notebooks/dataset-prep.ipynb
	docker cp luna-tutorial:/home/laluna/dsa-tools.ipynb notebooks/dsa-tools.ipynb
	docker cp luna-tutorial:/home/laluna/end-to-end-pipeline.ipynb notebooks/end-to-end-pipeline.ipynb
	docker cp luna-tutorial:/home/laluna/inference-and-visualization.ipynb notebooks/inference-and-visualization.ipynb
	docker cp luna-tutorial:/home/laluna/model-training.ipynb notebooks/model-training.ipynb
	docker cp luna-tutorial:/home/laluna/tiling.ipynb notebooks/tiling.ipynb
