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


build:      ## build docker image
	docker build -t qupath/latest .


clean:      ## cleanup
	docker system prune -f


clean-image:  ## remove qupath image
	docker rmi -f qupath/latest



run:    ## run a script within the container. This runs using cpus.
	docker run -it \
	--mount source=$(PWD)/data,destination=/data,type=bind \
	--mount source=$(PWD)/models,destination=/models,type=bind \
	--mount source=$(PWD)/scripts,destination=/scripts,type=bind \
	$(host) \
	qupath/latest \
    java -Djava.awt.headless=true -Djava.library.path=/qupath/build/dist/QuPath-0.2.3/lib/app \
    -jar /qupath-cpu/build/dist/QuPath-0.2.3/lib/app/qupath-0.2.3.jar \
    script --image /$(image) /$(script)


build-singularity-gpu:
	singularity pull docker://druvpatel/qupath-stardist:latest
run-singularity-gpu:
	singularity run --env TF_FORCE_GPU_ALLOW_GROWTH=true,PER_PROCESS_GPU_MEMORY_FRACTION=0.8  -B $(PWD)/data:/data,$(PWD)/models:/models,$(PWD)/scripts:/scripts  --nv $(PWD)/qupath-stardist_latest.sif java -Djava.awt.headless=true \
	-Djava.library.path=/qupath-gpu/build/dist/QuPath-0.2.3/lib/app  \
	-jar /qupath-gpu/build/dist/QuPath-0.2.3/lib/app/qupath-0.2.3.jar  \
	script --image /$(image) /$(script)