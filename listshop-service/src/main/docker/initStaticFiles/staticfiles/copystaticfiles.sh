#!/bin/bash


ls /opt/listshop
echo 'starting copy files'
ls staticfiles
echo 'starting copy files'
cd /opt/listshop
mkdir glowroot
mkdir static
cd ../..
echo 'made directory, beginning to copy glowroot'
cp  -rf ./staticfiles/glowroot/* /opt/listshop/glowroot
echo 'copied glowroot, beginning to copy json files'
cp ./staticfiles/*.json /opt/listshop/static
echo 'copied json files, beginning to copy png files'
cp ./staticfiles/*.png /opt/listshop/static
echo 'all done - lets do a ls'
ls

cd /opt/listshop
ls
ls static
ls glowroot
