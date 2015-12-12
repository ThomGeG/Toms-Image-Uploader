import requests
import os, sys
from base64 import b64encode

clientID = "aab9761289e2b38"
clientSecret = "e17ea350d4eff1892239f243f0164f2b163e0a58"

url = "https://api.imgur.com/3/image"
headers = {"Authorization" : "Client-ID " + clientID}

def uploadImage(imagePath):
    """
    Uploads the image to imgur and returns the URL.
    """
        
    data = {
            "key"   : clientSecret,
            "image" : b64encode(open(imagePath, "rb").read()),
            "type"  : "base64",
            "name"  : imagePath.split("/")[-1],
            "title" : getFilename(imagePath)
    }
    
    response = requests.post(url, headers=headers, data=data).json()
    
    return response["data"]["link"]
        
def sendToClipboard(string):
    string = str(string)
    os.system("echo " + string.strip() + " | clip")
    print("\"" + string +"\" sent to clip-board")

def getFilename(fileDir):
    """
    Input: Directory of a file (String)
	Output: Name of the file w/o extension (String)
	"""
    return fileDir.split("/")[-1].split(".")[0]
    
if __name__ == "__main__":

    if len(sys.argv) > 1:
        sendToClipboard(uploadImage(sys.argv[1]))
        
