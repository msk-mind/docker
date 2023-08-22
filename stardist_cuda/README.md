stardist_cuda
=============

This folder is used to build an image for running QuPath with CUDA support.
We currently use this image in `luna` for `stardist_simple` jobs.

The version number should be the QuPath version number, plus a sequence
number or some other label to distinguish different versions of our code
(the groovy scripts, etc.)  The version that will be used in luna should
*additionally* be tagged `current`.

Build
-----
    NAME="qupath_stardist"
    VERSION=0.4.3-1

    docker build -t ${NAME}:${VERSION} .

Installation
------------
Add the newly built image to DockerHub with the command

    docker push mskmind/${NAME}:${VERSION}

When you're ready to make this the version that's used by luna, apply
the `current` tag to this image with

    docker tag ${NAME}:${VERSION} ${NAME}:current
    docker push mskmind/${NAME}:current


