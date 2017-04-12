import webview
from   bottle             import *
from   watchdog.observers import Observer

import os
import signal
import threading
import webbrowser
from   http.client import HTTPConnection

import config
import ImgurAPI
from   Logger       import Logger
from   ImageHandler import Handler

gui = Bottle()

config.DEFAULT_QUICK_SHARE_ALBUM = "No Album." #Remove magic string. See updateSettings() for more information!

@gui.route('/')
def home():

    #Get the albums to feature on the main 'menu'.
    albums = []
    for (directory, albumID) in config.albumPairs:
        album = ImgurAPI.getAlbum(albumID)
        album["directory"] = directory

        #Make the cover image link more accessible (It's kept with the images themselves).
        for image in album["images"]:
            if image["id"] == album["cover"]:
                album["cover_link"] = image["link"]
                break

        albums.append(album)

    templateData = {"albums" : albums}
    return template(config.TEMPLATE_DIR + 'home.html', templateData)

@gui.route('/settings')
def settings():

    #Variables in config.py that may be changed.
    templateData = {"setting_value_pairs" : [
            ("IMGUR_CLIENT_ID",     config.IMGUR_CLIENT_ID),
            ("IMGUR_CLIENT_SECRET", config.IMGUR_CLIENT_SECRET),
            ("IMGUR_ACCESS_TOKEN",  config.IMGUR_ACCESS_TOKEN),
            ("IMGUR_REFRESH_TOKEN", config.IMGUR_REFRESH_TOKEN),
            ("QUICK_SHARE_ALBUM",   config.QUICK_SHARE_ALBUM)
        ]
    }

    return template(config.TEMPLATE_DIR + 'settings.html', templateData)

@gui.route('/settings', method="POST")
def updateSettings():

    updated_config = None

    with open("config.py") as f:
        updated_config = f.read()
        updated_config = updated_config.replace(config.IMGUR_CLIENT_ID,     request.forms.get("IMGUR_CLIENT_ID"))
        updated_config = updated_config.replace(config.IMGUR_CLIENT_SECRET, request.forms.get("IMGUR_CLIENT_SECRET"))
        updated_config = updated_config.replace(config.IMGUR_ACCESS_TOKEN,  request.forms.get("IMGUR_ACCESS_TOKEN"))
        updated_config = updated_config.replace(config.IMGUR_REFRESH_TOKEN, request.forms.get("IMGUR_REFRESH_TOKEN"))

        #Update the QUICK_SHARE_ALBUM.
        #A None value (or "") is converted into config.DEFAULT_QUICK_SHARE_ALBUM, otherwise the next update will corrupt the file,
        #replacing all characters with that new value. ("SOME TEXT".replace("", "NEW_VALUE")... Not good.)
        updated_config = updated_config.replace(config.QUICK_SHARE_ALBUM, request.forms.get("QUICK_SHARE_ALBUM") if request.forms.get("QUICK_SHARE_ALBUM") is not None else config.DEFAULT_QUICK_SHARE_ALBUM)

    with open("config.py", "w") as f:
        f.write(updated_config) #Commit changes to file.

    #Commit changes in memory.
    config.IMGUR_CLIENT_ID     = request.forms.get("IMGUR_CLIENT_ID")
    config.IMGUR_CLIENT_SECRET = request.forms.get("IMGUR_CLIENT_SECRET")
    config.IMGUR_ACCESS_TOKEN  = request.forms.get("IMGUR_ACCESS_TOKEN")
    config.IMGUR_REFRESH_TOKEN = request.forms.get("IMGUR_REFRESH_TOKEN")
    config.QUICK_SHARE_ALBUM   = request.forms.get("QUICK_SHARE_ALBUM") if request.forms.get("QUICK_SHARE_ALBUM") is not None else config.DEFAULT_QUICK_SHARE_ALBUM

    return seeOther("/")

@gui.route('/register')
def register():
    return template(config.TEMPLATE_DIR + 'register.html', {})

@gui.route('/register', method="POST")
def processClient():

    #Update internal variables.
    config.IMGUR_CLIENT_ID     = request.forms.get("IMGUR_CLIENT_ID")
    config.IMGUR_CLIENT_SECRET = request.forms.get("IMGUR_CLIENT_SECRET")

    #Append new variables to config file. (Yay self-modifying code!)
    with open("config.py", "a") as f:
        f.write("\nIMGUR_CLIENT_ID = \"" + config.IMGUR_CLIENT_ID + "\"\nIMGUR_CLIENT_SECRET = \"" + config.IMGUR_CLIENT_SECRET + "\"\n")

    return seeOther("/PIN")

@gui.route('/PIN')
def requestPIN():

    webbrowser.open_new_tab("https://api.imgur.com/oauth2/authorize?client_id=" + config.IMGUR_CLIENT_ID + "&response_type=pin")

    return template(config.TEMPLATE_DIR + 'PIN.html', {})

@gui.route('/PIN', method="POST")
def processPIN():

    PIN = request.forms.get("PIN")

    #Send imgur the PIN and receive the response.
    response = requests.post("https://api.imgur.com/oauth2/token",
                              headers = {"Authorization" : "Client-ID" + config.IMGUR_CLIENT_ID},
                              data = {
                                "client_id"       : config.IMGUR_CLIENT_ID,
                                "client_secret"   : config.IMGUR_CLIENT_SECRET,
                                "grant_type"      : "pin",
                                "pin"             : PIN
                              }
                            ).json()

    #Request successful, response contains keys.
    if "access_token" in response:

        #Update internal variables.
        config.IMGUR_ACCESS_TOKEN = response["access_token"]
        config.IMGUR_REFRESH_TOKEN = response["refresh_token"]

        #Commit changes to config file. (Yay self-modifying code!)
        with open("config.py", "a") as f:
            f.write("\nIMGUR_ACCESS_TOKEN = \"" + config.IMGUR_ACCESS_TOKEN + "\"\nIMGUR_REFRESH_TOKEN = \"" + config.IMGUR_REFRESH_TOKEN + "\"\n")

        return seeOther("/")
    else:
        return seeOther("/PIN")

@gui.route('/new_album')
def albumWizard():
    return template(config.TEMPLATE_DIR + "album_creator.html", {})

@gui.route('/new_album', method="POST")
def addAlbum():
    """ Add a new directory for the application to watch. """

    #Collect meta-data.
    title       = request.forms.get("TITLE")
    location    = request.forms.get("LOCATION")
    description = request.forms.get("DESCRIPTION")
    visibility  = request.forms.get("VISIBILITY")

    #Create the new album.
    albumID = ImgurAPI.createAlbum(title=title, description=description, privacy=visibility)["id"]

    #Add to observer.
    config.observer.schedule(event_handler=Handler('*', albumID), path=location, recursive=True)

    #Commit to memory (The auto-update-file).
    with open(config.AUTO_UPDATE_FILE, 'a') as f:
        f.write(location + " -> " + albumID + "\n")

    return seeOther("/")

@gui.route('/sync')
def sync():
    threading.Thread(target=syncAlbums, daemon=True).start() #Spin off the real function on a new thread.
    return seeOther("/")

def syncAlbums():
    """ Perform a pass of images in local storage and ensure each is uploaded to Imgur. """

    from config import observer, albumPairs

    #Retrieve the images currently in local storage.
    albumImages = [] #[(albumID, [images, ...]), ...]
    for (directory, albumID) in albumPairs:
        albumImages.append((albumID, [directory + "\\" + f for f in os.listdir(directory) if os.path.isfile(directory + "\\" + f) and f.split(".")[-1].lower() in config.VALID_FILE_TYPES])) #Long list comprehension that gets us all the images in the directory.

    uploaded_images = [] #Remember what images we uploaded for the quick share feature.

    for (albumID, imageList) in albumImages:

        existingImages = [image["name"] for image in ImgurAPI.getAlbumImages(albumID)] #Fetch the pre-existing images online..

        for image in imageList:
            if image.split("\\")[-1] not in existingImages:
                #It's not online, upload it!
                uploaded_images.append(ImgurAPI.uploadImage(image, album=albumID))
                config.logger.log("Uploaded: (" + albumID + ") <- " + image)

    if config.QUICK_SHARE_ALBUM is not config.DEFAULT_QUICK_SHARE_ALBUM and len(uploaded_images) is not 0:
        ImgurAPI.emptyAlbum(config.QUICK_SHARE_ALBUM)                                                                       #Empty the quick share album of previous images
        ImgurAPI.addImageToAlbum(config.QUICK_SHARE_ALBUM, ','.join(map(str, [image["id"] for image in uploaded_images])))  #Add the recent images to the quick share album
        os.system("echo " + str(ImgurAPI.getAlbum(config.QUICK_SHARE_ALBUM)["link"] + " | clip"))                           #Hand out the link to the quick share album

    config.logger.log("Synced " + str(len(uploaded_images)) + " image(s) on request.")

@gui.route('/static/<path:path>')
def getResource(path):
    """ Static content delivery (CSS, JS, etc.) """
    return(static_file(path, root="resources"))

def seeOther(url):
    """
        bottle.redirect currently not working.
        Use this as alternative, homebrew version of the same thing.
        Constructs a '303 See Other' response and returns it to the user.
    """
    res = response.copy(cls=HTTPResponse)
    res.body = ""
    res.set_header('Location', urljoin(request.url, url))
    res.status = 303 if request.get('SERVER_PROTOCOL') == "HTTP/1.1" else 302

    return res

if __name__ == '__main__':

    os.chdir(sys.argv[0] + "/..") #Change our working directory to where this executed file is located.

    redirect = "" #Should we need to redirect the user somewhere on launch we can.
    config.logger = Logger(config.LOG_LOCATION)

    #Start the HTTP server that servers our "GUI"
    threading.Thread(target=(lambda: run(gui, host="localhost", port=8080, debug=True, server="paste"))).start()
    config.logger.log("Server started!")

    #Retrieve albums to sync.
    config.albumPairs = [] #[(directory, albumID), ...]
    with open(config.AUTO_UPDATE_FILE, 'r') as f:
        for albumPair in f.readlines():
            config.albumPairs.append([x.strip() for x in albumPair.split("->")])

    config.logger.log("Using albums: " + str(config.albumPairs))

    #Schedule an observer for each of our albums
    config.observer = Observer()
    for (directory, album) in config.albumPairs:
        config.observer.schedule(event_handler=Handler('*', album), path=directory, recursive=True)
    config.observer.daemon = False

    #Start said listener
    config.observer.start()
    config.logger.log("Listening on said directories.")

    try: #Attempt handshake with Imgur.
        ImgurAPI.handshake() #Ensure connection with imgur.
        config.logger.log("Shook hands with Imgur.")
    except NameError: #If the neccessary keys are missing..
        redirect = "/register" #Redirect the user to the registration page.
        config.logger.log("Keys not found, redirecting to first time setup.")

    sync() #Sync any images created whilst application wasn't open.

    #Initiate GUI
    webview.create_window("Tom's Image Uploader", "http://localhost:8080/" + redirect, width=800, height=600, resizable=False, fullscreen=False)

    config.logger.log("Killing application.")
    os.kill(os.getpid(), signal.SIGINT) #Send the same signal Ctrl-C sends to stop the Bottle server
