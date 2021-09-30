=============================
Minimal Digital Slide Archive
=============================

This directory contains a docker-compose set up for a minimal installation of the Digital Slide Archive without the ability to run algorithms or other worker-based jobs.
Sample data and annotations are made available on the platform.

Prerequsities:
--------------

Before using this, you need both Docker and docker-compose.  See the `official installation instructions <https://docs.docker.com/compose/install>`_.

The docker-compose file assumes certain file paths.  This has been tested on Ubuntu 18.04.  It will probably work on other Linux variants.


Start
-----

To start the minimal Digital Slide Archive::

    docker-compose up -d

Stop
----

To stop the Digital Slide Archive::

    docker-compose down

Notes
-----

All data is stored in Docker volumes.

