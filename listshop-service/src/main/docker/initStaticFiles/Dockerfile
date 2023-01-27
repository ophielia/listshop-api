FROM ubuntu

RUN apt-get update

# copy files into image
ADD /staticfiles/glowroot staticfiles/glowroot
COPY /staticfiles/celebrations.json /staticfiles/check.png  ./staticfiles/
COPY /staticfiles/features.json /staticfiles/lock.png /staticfiles/smallorangelogo.png ./staticfiles/

# copy script into image
COPY /staticfiles/copystaticfiles.sh ./

ENTRYPOINT ["sh","/copystaticfiles.sh"]
