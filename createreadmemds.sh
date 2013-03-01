#!/bin/bash

#This script updates the README.md files for all projects depending on the pom.xml descriptor and adds the 
#Google Analytics inclusion

for dir in $(find . -maxdepth 1 -type d );
 do for pom in $(find $dir -maxdepth 1 -name "pom.xml");
  do 
   sed -n -e 's/.*<artifactId[ ]*>\(.*\)<\/artifactId[ ]*>.*/\1/p' $pom | head -1 >$dir/README.md;
   echo "===========================================================" >>$dir/README.md;
   sed -n -e 's/.*<description[ ]*>\(.*\)<\/description[ ]*>.*/\1/p' $pom >>$dir/README.md;
   echo "

[![githalytics.com alpha](https://cruel-carlota.pagodabox.com/21fc96ecdd5b75775df8dfeea272aa3a \"githalytics.com\")](http://githalytics.com/olinux/twice)" >> $dir/README.md;	

 done;
done;

