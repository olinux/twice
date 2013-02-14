set path=%path%;c:\Python27
@echo "***************************************"
@echo "NOW STARTING THE MULTICURSOR SUPPORT. IF YOU WANT TO STOP IT, CLOSE THIS WINDOW OR PRESS CTRL-C"
@echo "***************************************"
@echo "Port: 8081"

python %~dp0standalone.py -p 8081 -w %~dp0modules
exit 0



