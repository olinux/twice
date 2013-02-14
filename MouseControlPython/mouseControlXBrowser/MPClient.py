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
class MPClient(): 
    
    def __init__(self, uuid, width, height, request): 
        self.uuid = uuid
        self.width = width
        self.height = height
        self.request = request
        self.clientColor = {}
    
    def getColorForUUID(self, uuid):
        if uuid!=None and self.clientColor.has_key(uuid):
            return self.clientColor[uuid]
        return "#null"
        