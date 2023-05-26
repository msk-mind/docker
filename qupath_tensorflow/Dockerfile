FROM nvidia/cuda:12.1.1-cudnn8-devel-centos7
MAINTAINER MSK-MIND
LABEL "app"="qupath"
LABEL "version"="0.2.3"
LABEL "description"="qupath bundled with stardist and tensorflow for CPUs. Run QuPath-gpu for GPU support, and QuPath-cpu for CPU only"

RUN yum install -y sudo tree wget git vim  && \
    yum install -y binutils  && \
    yum install -y libxml2-devel libtiff-devel glib2-devel libxcb-devel libXrender-devel libxcb-devel && \
    yum install -y mesa-libGL && \
    yum install -y epel-release && \
    yum install -y openslide && \
    yum install -y xz && \
    yum install -y unzip

# download this RPM from https://www.oracle.com/java/technologies/javase/jdk14-archive-downloads.html
COPY jdk-14.0.2_linux-x64_bin.rpm .

RUN yum localinstall -y jdk-14.0.2_linux-x64_bin.rpm

RUN git clone --branch v0.2.3 https://github.com/qupath/qupath.git /qupath-cpu && \
    cd /qupath-cpu &&  \
    ./gradlew clean build createPackage -x test -Ptensorflow-cpu=true

RUN ln -s /qupath-cpu/build/dist/QuPath-0.2.3/bin/QuPath-0.2.3 /usr/local/bin/QuPath-cpu

RUN git clone --branch v0.2.3 https://github.com/qupath/qupath.git /qupath-gpu && \
    cd /qupath-gpu &&  \
    ./gradlew clean build createPackage -x test -Ptensorflow-gpu=true

RUN ln -s /qupath-gpu/build/dist/QuPath-0.2.3/bin/QuPath-0.2.3 /usr/local/bin/QuPath-gpu

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
