#!/bin/bash


echo 'starting copy files'
echo 'Beginning to copy json files'
cp ./staticfiles/*.json /opt/listshop/static
echo 'Making directory for images'
cd /opt/listshop
mkdir images
cd ../..
echo 'Beginning to copy image files'
cp ./staticfiles/*.png /opt/listshop/static/images
echo 'Beginning to copy html files'
cp ./staticfiles/*.html /opt/listshop/static
echo 'Done!'