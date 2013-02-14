#!/bin/bash
SCRIPT=`readlink -f $0`
SCRIPTPATH=`dirname $SCRIPT`
echo "***********************"
echo "Install Mod_pywebsocket"
echo "***********************"
sudo python $SCRIPTPATH/setup_mod_pywebsocket.py install