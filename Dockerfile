FROM ubuntu:latest
LABEL authors="Gagnon"

ENTRYPOINT ["top", "-b"]