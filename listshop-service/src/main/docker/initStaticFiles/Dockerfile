FROM ubuntu

RUN apt-get update

# copy files into image
RUN mkdir glowroot
RUN mkdir staticfiles
ADD /staticfiles/glowroot ./staticfiles
COPY /staticfiles/celebrations.json /staticfiles/check.png  ./staticfiles/
COPY /staticfiles/features.json /staticfiles/lock.png /staticfiles/smallorangelogo.png ./staticfiles/

# copy script into image
COPY /staticfiles/copystaticfiles.sh ./

ENTRYPOINT ["sh","/copystaticfiles.sh"]