#!/bin/bash

print("starting copy files")
mkdir glowroot
print("made directory, beginning to copy glowroot")
cp  ./staticfiles/glowroot/* /opt/listshop/glowroot
print("copied glowroot, beginning to copy json files")
cp ./staticfiles/*.json /opt/listshop/static
print("copied json files, beginning to copy png files")
cp ./staticfiles/*.png /opt/listshop/static
print("all done - lets do a ls")
ls

