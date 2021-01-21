# qupath bundled with stardist and tensor flow

Sample data in data/sample_data has been downloaded from http://openslide.cs.cmu.edu/download/openslide-testdata/.

More test data from various vendors can be found on this site.


## Build qupath image.
    
```
$ make build
$ docker images

REPOSITORY               TAG                           IMAGE ID            CREATED              SIZE
qupath/latest            latest                        ead3bb08477d        About a minute ago   2.4GB
adoptopenjdk/openjdk14   x86_64-debian-jdk-14.0.2_12   9350dbb3ad77        4 days ago           516MB
```
   
## Run qupath script in container. 
    
The command for executing the container has been designed to use the 'data', 'scripts' and 'models' directories to map these files to the container file system. These directories and files must be specified as relative paths.

If the script uses an external api, the url and IP of the api must be provided to the continer using the host argument. The host IP can be obtained from the URL using the nslookup linux command. Two or more hosts may be specified by specifying multiple host arguments. If the script does not use any external api, the host argument must be specified at least once with an empty string since it is a required argument. 

```
make host="--add-host=<api_host_url:api_host_ip>" \
script=scripts/sample_scripts/import_annot_from_api.groovy \
image=data/HobI20-934829783117.svs run


make host="" \
script=scripts/sample_scripts/stardist_example.groovy \
models=models/he_heavy_augment \
image=data/sample_data/CMU-1-Small-Region_2.svs run
```

## Cleanup docker.
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
