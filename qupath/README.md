# QuPath bundled with StarDist and Tensorflow

Sample data in data/sample_data has been downloaded from http://openslide.cs.cmu.edu/download/openslide-testdata/.
More test data from various vendors can be found on this site.

This repo contains a containerized version of QuPath+StarDist with GPU support. These containers can be built and run using Docker (Section 2) or Singularity (Section 1).


# Section 1: Building and Running Image with Singularity    
This image has been prebuild on Dockerhub to run via singularity.


## Section 1: Part 1 -- Pull singularity image from Dockerhub

```
make build-singularity
```

## Section 1: Part 2 -- Run singularity image
Run the image using singularity specifying script and image arguments. Like the docker image, the command for executing the container has been designed to use the 'data', 'scripts' and 'models' directories to map these files to the container file system. These directories and files must be specified as relative paths. Any data that needs to be referenced outside of data/ scripts/ and models/ should be mounted using the -B command. To do this, append the new mount (comma separated) to the -B argument in the makefile under run-singularity-cpu and/or run-singularity-gpu as follows: /path/on/host:/bind/path/on/container.


To run with CPUs: use `run-singularity-cpu`, and use GPUs use `run-singularity-gspu`.

Note: adding hosts is not currently supported with the singularity build. 

Examples:

If successful, `stardist_example.groovy` will output a geojson of cell objects to data/test.geojson 
```
make
script=scripts/sample_scripts/stardist_example.groovy \
image=data/sample_data/CMU-1-Small-Region_2.svs run-singularity-cpu
```

```
make \
script=scripts/sample_scripts/stardist_example.groovy \
image=data/sample_data/CMU-1-Small-Region_2.svs run-singularity-gpu
```




# Section 2: Building and Running Image with Docker (WIP)
## Section 2: Part 1 -- Build image using Dockerfile

For building with Docker, there is a small setup step that needs to be done. Using the following links, download these .deb files and copy them to this directory `docker/qupath/`. You will have to create a developer nvidia account in order to do this.

1) libcudnn7_7.6.5.32-1%2Bcuda10.2_amd64.deb: 
https://developer.nvidia.com/compute/machine-learning/cudnn/secure/7.6.5.32/Production/10.2_20191118/Ubuntu16_04-x64/libcudnn7_7.6.5.32-1%2Bcuda10.2_amd64.deb

2) libcudnn7-dev_7.6.5.32-1%2Bcuda10.2_amd64.deb
https://developer.nvidia.com/compute/machine-learning/cudnn/secure/7.6.5.32/Production/10.2_20191118/Ubuntu16_04-x64/libcudnn7-dev_7.6.5.32-1%2Bcuda10.2_amd64.deb

3) libcudnn7-doc_7.6.5.32-1%2Bcuda10.2_amd64.deb
https://developer.nvidia.com/compute/machine-learning/cudnn/secure/7.6.5.32/Production/10.2_20191118/Ubuntu16_04-x64/libcudnn7-doc_7.6.5.32-1%2Bcuda10.2_amd64.deb


Once you've downloaded these files, you can proceed with the build:
```
$ make build
$ docker images

REPOSITORY               TAG                           IMAGE ID            CREATED              SIZE
qupath/latest            latest                        ead3bb08477d        About a minute ago   2.4GB
adoptopenjdk/openjdk14   x86_64-debian-jdk-14.0.2_12   9350dbb3ad77        4 days ago           516MB
```
   
## Section 2: Part 2 -- Run QuPath groovy script using built Docker container

    
The command for executing the container has been designed to use the 'data', 'scripts' and 'models' directories to map these files to the container file system. These directories and files must be specified as relative paths.

If the script uses an external api, the url and IP of the api must be provided to the continer using the host argument. The host IP can be obtained from the URL using the nslookup linux command. Two or more hosts may be specified by specifying multiple host arguments. If the script does not use any external api, the host argument must be specified at least once with an empty string since it is a required argument. 

To run with CPUs: use `run-cpu`, and use GPUs use `run-gpu`.

Examples:

This script can be used to import annotationss from the getPathologyAnnotations API
```
make host="--add-host=<api_host_url:api_host_ip>" \
script=scripts/sample_scripts/import_annot_from_api.groovy \
image=data/HobI20-934829783117.svs run-cpu
```

If successful, `stardist_example.groovy` will output a geojson of cell objects to data/test.geojson 
```
make host="" \
script=scripts/sample_scripts/stardist_example.groovy \
image=data/sample_data/CMU-1-Small-Region_2.svs run-gpu
```



## Section 2: Part 3 -- Cleanup Docker container
Cleans stopped/exited containers, unused networks, dangling images, dangling build caches

```
$ make clean
```


## Logs
- started with adoptopenjdk:openjdk14

- ImageWriterIJTest > testTiffAndZip() FAILED
    java.awt.HeadlessException at ImageWriterIJTest.java:46
    4 tests completed, 1 failed
  
  so, excluded tests with `gradle ... -x test` (see dockerfile)

- Error: java.io.IOException: Cannot run program "objcopy": error=2, No such file or directory

  so, installed binutils (see dockerfile)

- 17:18:31.139 [main] [ERROR] q.l.i.s.o.OpenslideServerBuilder - Could not load OpenSlide native libraries
java.lang.UnsatisfiedLinkError: /qupath/build/dist/QuPath-0.2.3/lib/app/libopenslide-jni.so: libxml2.so.2: cannot open shared object file: No such file or directory

  so, installed native libs (see dockerfile)
  
- cleanup stopped/exited conntainers, networks, dangling images, dangling build cache
docker system prune





`                                                                               `
