FROM adoptopenjdk/openjdk16:x86_64-debian-jdk-16.0.1_9

RUN apt-get update && apt-get install -y sudo tree wget git binutils libgl1-mesa-glx libxml2-dev openslide-tools xz-utils

# Download the main qupath library
RUN wget https://github.com/qupath/qupath/releases/download/v0.3.2/QuPath-0.3.2-Linux.tar.xz \
    && tar -xf QuPath-0.3.2-Linux.tar.xz \
    && chmod u+x /QuPath/bin/QuPath

ENV PATH=/QuPath/bin/:$PATH

# Download the stardist extention
RUN wget https://github.com/qupath/qupath-extension-stardist/releases/download/v0.3.0/qupath-extension-stardist-0.3.0.jar \
    && mv qupath-extension-stardist-0.3.0.jar /QuPath/lib/app/
    
# Update Qupath.cfg
RUN sed -i '/Application/ a app.classpath=$APPDIR/qupath-extension-stardist-0.3.0.jar' /QuPath/lib/app/QuPath.cfg &&  \
    sed -i '/JavaOptions/ a java-options=-Djava.awt.headless=true' /QuPath/lib/app/QuPath.cfg

#RUN cat /QuPath/lib/app/QuPath.cfg

# Checkout models
RUN git clone https://github.com/qupath/models

# Copy scripts
COPY scripts /scripts/
