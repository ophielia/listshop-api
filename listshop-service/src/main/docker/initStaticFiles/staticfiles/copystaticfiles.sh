#!/bin/bash

echo 'Making directory for images'
pwd
ls
cd /opt/listshop/static
mkdir images
cd
pwd
ls
echo 'starting copy files'
echo 'Beginning to copy json files'
cp ./staticfiles/*.json /opt/listshop/static

echo 'Beginning to copy image files'
cp ./staticfiles/*.png /opt/listshop/static/images
echo 'Beginning to copy html files'
cp ./staticfiles/*.html /opt/listshop/static
echo 'Done!'