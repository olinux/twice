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
Created on 26.05.2012

@author: oli
'''
from mouseControlXBrowser.MPClient import MPClient
import re
class Controller(): 
    
    
    def __init__(self): 
        self.clients = {}
        
    def cleanClosedConnections(self):
        toRemove = []
        for client in self.clients.values():
            if client!=None and (client.request==None or client.request.client_terminated == True):
                toRemove.append(client)
        for r in toRemove:
            print "Close connection to client "+r.uuid
            self.clients.pop(r.uuid)
    
    def interpretMessage(self, message, request):
        self.cleanClosedConnections()
        values = re.split("@", message)    
        if len(values)>0:
            uuid = values[0];
            if len(values)>1:
                action = values[1]
                if len(values)>2:
                    if len(values)>3:
                        if action == 's':
                            width = values[2]
                            height = values[3]
                            self.clients[uuid] = MPClient(uuid, width, height, request)
                            print "Client "+uuid+" (dimensions: "+width+"x"+height+") connected!"
                        elif action == 'r':
                            width = values[2]
                            height = values[3]
                            if self.clients.has_key(uuid):
                                client = self.clients[uuid]
                                if client!=None:
                                    print "Client "+uuid+" resized! New dimensions: "+width+"x"+height+"!"
                                    client.width = width
                                    client.height = height
                        elif action == 'c':
                            clientUUID = values[2]
                            color = values[3]
                            if self.clients.has_key(uuid):
                                client = self.clients[uuid]
                                if client!=None:
                                    print "Cursor assigned! Color for "+clientUUID+" @ "+uuid+" is "+color
                                    client.clientColor[clientUUID]=color
