FROM ubuntu

RUN apt-get update

# copy files into image
COPY /staticfiles/celebrations.json /staticfiles/check.png /staticfilt ./staticfiles/
COPY  /staticfiles/index.html ./staticfiles/
COPY /staticfiles/features.json /staticfiles/lock.png /staticfiles/smallorangelogo.png ./staticfiles/

# copy script into image
COPY /staticfiles/copystaticfiles.sh ./

ENTRYPOINT ["sh","/copystaticfiles.sh"]
