FROM ubuntu

RUN apt-get update

# copy files into image
RUN mkdir glowroot
COPY /staticfiles/glowroot/* ./glowroot/
COPY /staticfiles/celebrations.json /staticfiles/check.png  ./
COPY /staticfiles/features.json /staticfiles/lock.png /staticfiles/smallorangelogo.png ./

# copy script into image
COPY /staticfiles/copystaticfiles.sh ./

ENTRYPOINT ["sh","/copystaticfiles.sh"]