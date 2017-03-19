"""
Created on 19 Nov 2016
@author: Tom

    A little module to replace the need for the offical Imgur Python library
    with that lovely amatuer homemade smell.

"""

import requests

from   base64 import b64encode

import config

headers = {}

def uploadImage(image, album="", title="", description=""):

    #Retrieve image data
    image_body = None
    with open(image, "rb") as file:
        image_body = b64encode(file.read())

    data = {
            "key"           : config.IMGUR_CLIENT_SECRET,
            "image"         : image_body,
            "type"          : "base64",
            "name"          : image.split("\\")[-1]
    }

    if album is not "":
        data["album"] = album
    if title is not "":
        data["title"] = title
    if description is not "":
        data["description"] = description

    return transmit(requests.post, "https://api.imgur.com/3/image", data)

def getImage(imageID):
    return transmit(requests.get, "https://api.imgur.com/3/image/" + imageID, None)

def deleteImage(imageID):
    return transmit(requests.delete, "https://api.imgur.com/3/image/" + imageID, None)

def createAlbum(ids="", title="", description="", privacy="", cover=""):

    data = {}

    #Start setting the data for transfer.
    if ids is not "":
        data["ids[]"] = ','.join(map(str, ids)) if isinstance(ids, list) else ids #ids may come in list form. If so convert them to a single string delimited with commas.
    if title is not "":
        data["title"] = title
    if description is not "":
        data["description"] = description
    if privacy is not "":
        data["privacy"] = privacy
    if cover is not "":
        data["cover"] = cover

    return transmit(requests.post, "https://api.imgur.com/3/album", data)

def getAlbum(albumID):
    return transmit(requests.get, "https://api.imgur.com/3/album/" + albumID, None)

def getAlbumImages(albumID):
    return transmit(requests.get, "https://api.imgur.com/3/album/" + albumID + "/images", None)

def emptyAlbum(albumID):
    return transmit(requests.delete, "https://api.imgur.com/3/album/" + albumID + "/remove_images?ids=" + ','.join(map(str, [image["id"] for image in ImgurAPI.getAlbumImages(albumID)["data"]])), None)

def deleteAlbum(albumID, recursive=False):

    if recursive:
        for image in getAlbumImages(albumID):
            deleteImage(image["id"])

    return transmit(requests.delete, "https://api.imgur.com/3/album/" + albumID, None)

def handshake():

    #Validate the access token (They expire after ~1 month)
    headers["Authorization"] = "Bearer " + config.IMGUR_ACCESS_TOKEN

    #If the token has expired...
    if not requests.get("https://api.imgur.com/3/account/me", headers=headers).json()['success']:
        renewAccessToken() #request a new token!

def renewAccessToken():
    """Renews config.IMGUR_ACCESS_TOKEN using various other config variables."""
    print("Renewing access token!")

    expiredToken = config.IMGUR_ACCESS_TOKEN
    headers["Authorization"] = "Client-ID " + config.IMGUR_CLIENT_ID

    #Request new token...
    config.IMGUR_ACCESS_TOKEN = requests.post(
        "https://api.imgur.com/oauth2/token",
        headers=headers,
        data={
            "refresh_token" : config.IMGUR_REFRESH_TOKEN,
            "client_id"     : config.IMGUR_CLIENT_ID,
            "client_secret" : config.IMGUR_CLIENT_SECRET,
            "grant_type"    : "refresh_token"
        }
    ).json()["access_token"]

    headers["Authorization"] = "Bearer " + config.IMGUR_ACCESS_TOKEN

    #Update config file...
    with open("config.py") as f:
        updatedVars = f.read().replace(expiredToken, config.IMGUR_ACCESS_TOKEN)
    with open("config.py", "w") as f:
        f.write(updatedVars)

def transmit(func, url, data):

    response = func(url, headers=headers, data=data).json()

    if 'status' in response and response['status'] in ("401", "403"):
        renewAccessToken()
        response = func(url, headers=headers, data=data).json()

    if 'success' in response and response["success"] is False:
        from config import logger
        logger.log("Unexpected response from imgur.")
        logger.log("Request: " + str(func) + " to " + url + " with payload " + str(data))
        logger.log("Response: " + str(response))

    return response
