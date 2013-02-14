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
'''
Created on 27.11.2011

@author: oli
'''
import re
import socket
import threading

class UDPIntf(threading.Thread): 
    def __init__(self, port, controller): 
        threading.Thread.__init__(self) 
        self.sock = socket.socket(socket.AF_INET, socket.SOCK_DGRAM)
        self.sock.bind(("", int(port)))  
        self.controller = controller
    
    def run(self):         
        print "Started udp server on port 8182"
        while True:
            data, address = self.sock.recvfrom(256) 
            if len(data)>0:
                responded = False;
                value = re.split("@", data)
                self.controller.cleanClosedConnections()
                if len(value)>0:
                    action = value[0]
                    if action == 'g':
                        response = ''
                        for client in self.controller.clients.values():
                            response = response+client.uuid+"\n"
                        self.sock.sendto(response, address)
                        responded = True
                    elif action == 'm':
                        if len(value)>1:
                            uuid = value[1]
                            if self.controller.clients.has_key(uuid):
                                client = self.controller.clients[uuid]
                                message = ''  
                                i = 0
                                clientUUID = None
                                if len(value)>2:
                                    clientUUID = value[2]    
                                for partMessage in value:
                                    if i>1:
                                        message = message+partMessage
                                        if i!=len(value)-1:
                                            message = message +'@'
                                    i = i+1
                                if client!=None and client.request!=None and client.request.client_terminated == False:
                                    client.request.ws_stream.send_message(message.decode('utf-8'), binary=False)
                                self.sock.sendto(client.getColorForUUID(clientUUID)+"@"+client.width+"x"+client.height, address)
                                responded = True
                if responded == False:
                    self.sock.sendto("", address)