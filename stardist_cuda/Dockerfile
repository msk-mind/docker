FROM eclipse-temurin:11

RUN apt-get update && apt-get install -y sudo tree wget git binutils libgl1-mesa-glx libxml2-dev openslide-tools xz-utils unzip

# Download the main qupath library
RUN wget https://github.com/qupath/qupath/releases/download/v0.4.3/QuPath-0.4.3-Linux.tar.xz \
    && tar -xf QuPath-0.4.3-Linux.tar.xz \
    && chmod u+x /QuPath/bin/QuPath

ENV PATH=/QuPath/bin/:$PATH

# Download the stardist extention
RUN wget https://github.com/qupath/qupath-extension-stardist/releases/download/v0.4.0/qupath-extension-stardist-0.4.0.jar \
    && cp qupath-extension-stardist-0.4.0.jar /QuPath/lib/app/

# Qupath GPU
RUN git clone --branch v0.4.3 https://github.com/qupath/qupath.git /qupath-gpu && \
    cd /qupath-gpu &&  \
    ./gradlew clean jpackage -Pcuda-redist

RUN cp qupath-extension-stardist-0.4.0.jar /qupath-gpu/build/dist/QuPath/lib/app/

RUN ln -s /qupath-gpu/build/dist/QuPath/bin/QuPath /usr/local/bin/QuPath-gpu
RUN ln -s /QuPath/bin/QuPath /QuPath/bin/QuPath-cpu

RUN chmod ugo+rx /qupath-gpu/build/dist/QuPath/bin/QuPath /QuPath/bin/QuPath

# Update Qupath.cfg
RUN sed -i '/Application/ a app.classpath=$APPDIR/qupath-extension-stardist-0.4.0.jar' /QuPath/lib/app/QuPath.cfg /qupath-gpu/build/dist/QuPath/lib/app/QuPath.cfg &&  \
    sed -i '/JavaOptions/ a java-options=-Djava.awt.headless=true' /QuPath/lib/app/QuPath.cfg /qupath-gpu/build/dist/QuPath/lib/app/QuPath.cfg && \
    sed -i 's/-XX.*/-Xmx64G/' /qupath-gpu/build/dist/QuPath/lib/app/QuPath.cfg /QuPath/lib/app/QuPath.cfg

# Checkout models
RUN git clone https://github.com/qupath/models

COPY scripts /scripts/

# Copy classifier models
COPY classifier-models /classifier-models/

CMD QuPath # QuPath-gpu for GPU
