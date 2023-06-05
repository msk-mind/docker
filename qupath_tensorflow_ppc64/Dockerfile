FROM ppc64le/adoptopenjdk:14.0.2_12-jdk-openj9-0.21.0
MAINTAINER MSK-MIND
LABEL "app"="qupath"
LABEL "version"="0.2.3"
LABEL "description"="qupath bundled with stardist and tensorflow for CPUs. Run QuPath-gpu for GPU support, and QuPath-cpu for CPU only"

RUN apt-get update && \
    apt-get install -y sudo tree wget git vim  && \
    apt-get install -y gnupg2 && \
    apt-get install -y binutils  && \
    apt-get install -y libxml2-dev libtiff-dev libglib2.0-0 libxcb-shm0-dev libxrender-dev libxcb-render0-dev && \
    apt-get install -y libgl1-mesa-glx && \
    apt-get install -y software-properties-common && \
    apt-get install -y unzip && \
    apt-get install -y openslide-tools && \


# download this RPM from https://www.oracle.com/java/technologies/javase/jdk14-archive-downloads.html

RUN git clone --branch v0.2.3 https://github.com/qupath/qupath.git /qupath-cpu && \
    cd /qupath-cpu &&  \
    ./gradlew clean build createPackage -x test -Ptensorflow-cpu=true

RUN ln -s /qupath-cpu/build/dist/QuPath-0.2.3/bin/QuPath-0.2.3 /usr/local/bin/QuPath-cpu

#RUN git clone --branch v0.2.3 https://github.com/qupath/qupath.git /qupath-gpu && \
#    cd /qupath-gpu &&  \
#    ./gradlew clean build createPackage -x test -Ptensorflow-gpu=true
#
#RUN ln -s /qupath-gpu/build/dist/QuPath-0.2.3/bin/QuPath-0.2.3 /usr/local/bin/QuPath-gpu

#
## download tensorflow models
RUN git clone https://github.com/stardist/stardist-imagej
RUN mkdir -p /tensorflow-models/he_heavy_augment && unzip stardist-imagej/src/main/resources/models/2D/he_heavy_augment.zip -d /tensorflow-models/he_heavy_augment

#
## Copy scripts
COPY scripts /scripts/

#
## Copy classifier models
COPY classifier-models /classifier-models/


CMD QuPath-cpu # QuPath-gpu for GPU
