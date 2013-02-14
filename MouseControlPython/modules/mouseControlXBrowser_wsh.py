#
# Copyright 2013 Oliver Schmid
# Licensed under the Apache License, Version 2.0 (the "License");
# you may not use this file except in compliance with the License.
# You may obtain a copy of the License at
#
# http://www.apache.org/licenses/LICENSE-2.0
# 
# Unless required by applicable law or agreed to in writing, software
# distributed under the License is distributed on an "AS IS" BASIS,
# WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
# See the License for the specific language governing permissions and
# limitations under the License.
# 

from mouseControlXBrowser.Controller import Controller
from mouseControlXBrowser.UDPIntf import UDPIntf

controller = Controller()
controlSocket = UDPIntf(8182, controller)

def web_socket_do_extra_handshake(request):
    pass


def web_socket_transfer_data(request):
    while True:
        line = request.ws_stream.receive_message()
        if line is None:
            return
        if isinstance(line, unicode): 
            controller.interpretMessage(line, request);

controlSocket.setDaemon(True)
controlSocket.start()
print "Websocket server for mouse cursor xbrowser started"
        
