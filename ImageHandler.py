from watchdog.events    import (PatternMatchingEventHandler, FileCreatedEvent)

import time, os, sys

import ImgurAPI
from   config import VALID_FILE_TYPES

#An event handler to go with our observer.
class Handler(PatternMatchingEventHandler):

    def __init__(self, directory, albumID):
        super(Handler, self).__init__()
        self.albumID = albumID          #Give each Handler object the albumID for the album it's watching.

    def on_created(self, event: FileCreatedEvent):
        from config import logger

        #Applications will create an empty file and then write into it.
        #This call back commonly fires before said data can be written,
        #so we need to wait a moment otherwise there's no data to be read.
        time.sleep(1)

        if(event.src_path.split('.')[1].lower() in VALID_FILE_TYPES):                                                       #If the file created was a valid type...
            os.system("echo " + str(ImgurAPI.uploadImage(event.src_path, album=self.albumID)["data"]["link"]).strip() + " | clip")      #Upload it.
            logger.log("Detected and uploaded: " + event.src_path)
