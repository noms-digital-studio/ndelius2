#!/usr/bin/env bash

sbt assembly
docker build -t ndelius2 .

# To run within Docker:
# docker run -d -p 9000:9000 --name ndelius2 -e APPLICATION_SECRET=abcdef ndelius2

