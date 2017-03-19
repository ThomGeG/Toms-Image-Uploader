import webview
from   bottle             import *
from   watchdog.observers import Observer

import os
import signal
import threading
from   webbrowser  import open_new_tab
from   http.client import HTTPConnection

import config
import ImgurAPI
from   Logger       import Logger
from   ImageHandler import Handler

gui = Bottle()

@gui.route('/')
def home():

    albums = []
    for (directory, albumID) in config.albumPairs:
        album = ImgurAPI.getAlbum(albumID)["data"]
        album["directory"] = directory

        for image in album["images"]:
            if image["id"] == album["cover"]:
                album["cover_link"] = image["link"]
                break

        albums.append(album)

    templateData = {"albums" : albums}
    return template(config.TEMPLATE_DIR + 'home.html', templateData)

@gui.route('/settings')
def settings():

    templateData = {"setting_value_pairs" : [
            ("IMGUR_CLIENT_ID",     config.IMGUR_CLIENT_ID),
            ("IMGUR_CLIENT_SECRET", config.IMGUR_CLIENT_SECRET),
            ("IMGUR_ACCESS_TOKEN",  config.IMGUR_ACCESS_TOKEN),
            ("IMGUR_REFRESH_TOKEN", config.IMGUR_REFRESH_TOKEN )
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

    with open("config.py", "w") as f:
        f.write(updated_config) #Commit changes to file.

    #Commit changes in memory.
    config.IMGUR_CLIENT_ID     = request.forms.get("IMGUR_CLIENT_ID")
    config.IMGUR_CLIENT_SECRET = request.forms.get("IMGUR_CLIENT_SECRET")
    config.IMGUR_ACCESS_TOKEN  = request.forms.get("IMGUR_ACCESS_TOKEN")
    config.IMGUR_REFRESH_TOKEN = request.forms.get("IMGUR_REFRESH_TOKEN")

    return seeOther("/")

@gui.route('/register')
def register():

    templateData = {}
    return template(config.TEMPLATE_DIR + 'register.html', templateData)

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

    import webbrowser
    webbrowser.open_new_tab("https://api.imgur.com/oauth2/authorize?client_id=" + config.IMGUR_CLIENT_ID + "&response_type=pin")

    templateData = {}
    return template(config.TEMPLATE_DIR + 'PIN.html', templateData)

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
    if 'access_token' in response:

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

    templateData = {}
    return template(config.TEMPLATE_DIR + "album_creator.html", templateData)

@gui.route('/new_album', method="POST")
def addAlbum():

    title       = request.forms.get("TITLE")
    location    = request.forms.get("LOCATION")
    description = request.forms.get("DESCRIPTION")
    visibility  = request.forms.get("VISIBILITY")

    albumID = ImgurAPI.createAlbum(title=title, description=description, privacy=visibility)["data"]["id"]

    #Add to observer.
    config.observer.schedule(event_handler=Handler('*', albumID), path=location, recursive=True)

    #Add to auto-update-file
    with open(config.AUTO_UPDATE_FILE, 'a') as f:
        f.write(location + " -> " + albumID + "\n")

    return seeOther("/")

@gui.route('/sync')
def sync():

    threading.Thread(target=syncAlbums, daemon=True).start() #Spin off the real function on a new thread.

    return seeOther("/")

def syncAlbums():
    """ Perform a pass of images on disk and ensure each is uploaded to Imgur. """

    from config import observer, albumPairs

    #Retrieve/reference the images currently on disk.
    albumImages = [] #[(albumID, [images, ...]), ...]
    for (directory, albumID) in albumPairs:
        albumImages.append((albumID, [directory + "\\" + f for f in os.listdir(directory) if os.path.isfile(directory + "\\" + f) and f.split(".")[-1].lower() in config.VALID_FILE_TYPES])) #Long list comprehension that gets us all the images in the directory.

    uploaded_images = 0
    #For each album..
    for (albumID, imageList) in albumImages:
        #Fetch the pre-existing images online..
        existingImages = [image["name"] for image in ImgurAPI.getAlbumImages(albumID)["data"]]

        for image in imageList:
            if image.split("\\")[-1] not in existingImages: #and if there's ones not present.. do the thing.
                ImgurAPI.uploadImage(image, album=albumID)
                config.logger.log("Uploaded: (" + albumID + ") <- " + image)
                uploaded_images += 1

    config.logger.log("Synced " + str(uploaded_images) + " image(s) on request.")

@gui.route('/static/<path:path>')
def getResource(path):
    """ Static content delivery (CSS, JS, etc.) """
    return(static_file(path, root="resources"))

def seeOther(url):
    """
        bottle.redirect currently not working.
        Use this as alternative, homebrew version of the same thing.
    """
    res = response.copy(cls=HTTPResponse)
    res.body = ""
    res.set_header('Location', urljoin(request.url, url))
    res.status = 303 if request.get('SERVER_PROTOCOL') == "HTTP/1.1" else 302

    return res

if __name__ == '__main__':

    redirect = ""
    config.logger = Logger(config.LOG_LOCATION)

    #Start the HTTP server that servers our "GUI"
    threading.Thread(target=(lambda: run(gui, host="localhost", port=8080, debug=True, server="paste"))).start()
    config.logger.log("Server started!")

    #Retrieve albums to sync.
    config.albumPairs = [] #Variable to store [(directory, albumID), ...]

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

    #Initiate GUI
    webview.create_window("Tom's Image Uploader", "http://localhost:8080/" + redirect, width=800, height=600, resizable=False, fullscreen=False)

    config.logger.log("Killing application.")
    os.kill(os.getpid(), signal.SIGINT) #Send the same signal Ctrl-C sends to stop the Bottle server
