## Tom's Image Uploader

Tom's Image Uploader is a personal project of mine to create an application to allow me to quickly share my images/screenshots with others.

### What it looks like
![alt tag](http://i.imgur.com/ratM0z7.png)

### What it does

The core functionality of this application is to monitor directories for new images and upload them to an associated album on Imgur in real time. A link to the image will then be copied into the users clipboard ready to share the image with another.

## How it works

The application watches any number of directories (Stored in `auto-update.txt` by default) for new images through [watchdog](https://github.com/gorakhargosh/watchdog), a file system monitor that gives access to Windows events, namely the file creation event.

```observer.schedule(event_handler=Handler('*', albumID), path=location, recursive=True)```

The event handler uploads any images created while running to Imgur through their REST API via [requests](https://github.com/kennethreitz/requests) and adds them to the appropriorite album.

Over the top of this sits a HTML webserver (not so) cleverly disguised as a GUI, delivered via [bottle](https://github.com/bottlepy/bottle). 
