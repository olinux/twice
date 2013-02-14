#!/bin/bash

SCRIPT=`readlink -f $0`
SCRIPTPATH=`dirname $SCRIPT`
cleanup(){
	echo ""
}
trap cleanup INT TERM EXIT
cleanup
#gksudo -m "Start multipointer support" 'echo successful\ authentication'
echo "***************************************"
echo "NOW STARTING THE MULTICURSOR SUPPORT. IF YOU WANT TO STOP IT, CLOSE THIS WINDOW OR PRESS CTRL-C"
echo "***************************************"
echo "Port: "${1}

python $SCRIPTPATH/standalone.py -p ${1} -w $SCRIPTPATH/modules
cleanup
exit 0



