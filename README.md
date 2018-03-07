# Toms-Image-Uploader
A small application for automatically syncing image folders with imgur.<br>

Currently this application is little more than a minimal viable product so the features are small and the bugs are likely many. 

## How to use this applicaiton

To have the applicaiton attempt to sync a directory with imgur you must have two things: 
- An imgur album (made with your account) and
- A directory/folder on disk containing images

Simply create a new file in the `albums/` directory (by default) using the album ID as the filename, such as `h2sn8.txt` or `h2sn8.album`.
The contents of this file should be the path of the target directory/folder from which the images will be pulled, such as `C:\Users\Tom\Desktop\Photos` or `C:\Users\Tom\Pictures`.
Upon execution, the application will now cross reference the images found in the specified directory with those in the corresponding album and upload any that were missing.

## Getting Started 

In order to run and use this application you require both an Imgur account and a set of application keys.<br>

If you do not have an imgur account you can sign up for one at: [imgur.com/register](https://imgur.com/register)<br>

### Registering your application

If you do not have a registered application you must sign up for one at: [api.imgur.com/oauth2/addclient](https://api.imgur.com/oauth2/addclient)<br>

Simply fill out the form with the required information and ignore fields related to Authorization type and callback as they are unneccessary to the execution of this program.<br>

After successfully registering your application, Imgur will respond via the email address provided. Contained within this email should be two critical pieces of information, your `client-id` and `client secret`.
Save these for later.

### Authorizing your account

This application does not have an automatic authorization process in place, instead you must do so manually.<br>

To do so, modify the following link with the client ID you received after registering your application and follow it:
```
https://api.imgur.com/oauth2/authorize?response_type=token&client_id=YOUR_CLIENT_ID_HERE
```
You may be asked to log into your Imgur account if not already and authorize the application. Upon doing so you will be redirected to a non-existent page.

![alt](https://i.imgur.com/chKLdbY.png)

Contained within the parameters of this redirect are some of the keys/tokens neccessary for operation of this application. 
Extract the refresh token, as seen above, and have it on hand.

This lengthy process has now given you the three pieces of information you require to properly configure this application.<br>
Open `src/main/resources/application.properties` and replace the null values with your keys so it resembles something like:
```
CLIENT_ID=1a2b3c4d
CLIENT_SECRET=onetwothreefourfivesixseveneight
REFRESH_TOKEN=notarealkey
```
From here you should be able to simply run or package the application like you would any other Maven project.

## Built With

* [Spring Boot](https://projects.spring.io/spring-boot/) - For requesting and parsing JSON into POJO's 
* [Maven](https://maven.apache.org/) - For dependency/build management
* [Java](https://java.com/en/download/) - ...
* [Eclipse](http://www.eclipse.org/downloads/) - Blessed be the IDE, even if it does cause hair loss.

## See also
 - [github.com/ThomGeG/Toms-Image-Uploader/tree/Python](https://github.com/ThomGeG/Toms-Image-Uploader/tree/Python) - A deprecated Python version of this program.
 - [api.imgur.com](https://api.imgur.com/) - The official API documentation that this application utilizes.
