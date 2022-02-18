
#  WebTerminal  🚀

<img src="images/webterminal_logo_large.png?raw=true" width="500" value="primary_logo" />

<!--- ## ```Delightfully capable ⚡```  ```Surprisingly simple ⭐ ``` --->
 
## What is webterminal ?

Webterminal 🚀 is a **self-hosted web app** that boasts powerful features like 

&nbsp;&nbsp;&nbsp;&nbsp;  :heavy_check_mark: Remote command execution 

&nbsp;&nbsp;&nbsp;&nbsp;  :heavy_check_mark: Browse file system, download-upload files

&nbsp;&nbsp;&nbsp;&nbsp;  :heavy_check_mark: Monitor status of processes 

&nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp;  &nbsp; &nbsp; &nbsp; &nbsp; ...and much more, everything from your **BROWSER** 🌐 !! 

Web terminal is an attempt to take the most advantage of the intutive medium - ```WEB```, with the powerful tool - ```TERMINAL```

```Simple ⭐ yet Capable ⚡``` 

# Setup 

Just run the app on the machine you want to control and access the web app at port **9000** from your BROWSER🌏. 

##  🛠️ Running - The *easy* way 
[recommended for remote machines - EC2]

To run the app, execute the following **fat commands**. It would download/update and start the app. (under ```webterminal-app``` folder in current user directory)

#### Linux 

```cd ~ && wget DIRECT_BINARY_HYPERLINK -O webterminalinstaller-LATEST.jar && java -jar webterminalinstaller-LATEST.jar && cd webterminal-app && chmod 777 start.sh && nohup ./start.sh &```

#### Windows (using cmd) 

```powershell -nop -c "Invoke-WebRequest 'DIRECT_BINARY_HYPERLINK' -OutFile %homepath%\webterminalinstaller-LATEST.jar" && cd %homepath% && java -jar webterminalinstaller-LATEST.jar && cd webterminal-app && start.bat```

⚙️ JPS output shows the "webterminal.jar" process, to make sure that the server is running. 

##  ⚒️ Running - The *normal* way (alternative)
[recommended for your local machine]

Its just a *2 step process !!* 

>  **STEP 1:** ```Clone/Download this repository and extract.```

>  **STEP 2:** ```Run start.bat/sh```

The **The *easy* way** does the same, in a single command !! 

⚙️ JPS output shows the "webterminal.jar" process, to make sure that the server is running. 

# Usage

After opening the web page 🌏, you will presented with ```Apps view```. 

Other views - ```Files view``` 📁 and ```Monitor view```🖥️ can be accessed using the icons on the left.

## ``` Apps view:```

```Action``` in webterminal, simply speaking, is a pre-configured command and an directory (optional) to run it.

Each action would have ```Run now``` button on the webpage, which fires the command in the server. 

By default, the application comes bundled with default apps.

##### Usecases 

The idea of ```action``` in apps view is to have a handy bag of utility commands pre-configured, like ```jps```, ```process kill```, even ```tailing a file```...

Anything that can be executed on terminal (cmd or ssh) can be triggered from here, using ```any``` app ( the last app in the list ).

## ``` Files view:```

Opening ```Files view``` icon (📁 -> from left of the page), would allow us to browse across all directories in the host. 

You can navigate across directories, choose to ```download ``` and ``` upload```  a file. You can favourite 💗 frequently visited directories as bookmarks.

```For file downloads,``` it would be zipped by default. In case of log files, it would be advantageous.

```Uploading a file``` is as easy as navigating to desired directory and choosing upload. This upload is proven to be ```faster ⚡``` than Windows RPD or Linux SFTP transfer, and has ```no size limit``` !!

##### Peer to peer file transfer 

We can share files over the network without our local bandwidth. The transfer is cross platform compatible.

```How to:``` For transferring, just open the webpage in the sender machine, and upload the file to your desired directory. [Web terminal server needs to be running only on receiver's box]

If the sender machine is linux, execute the below command in the directory:

```curl -i -X POST -H "Content-Type: multipart/form-data" -F "file=@<FILETOUPLOAD>" http://<RECEIVER_HOST>:<PORT>/uploadfile?directory=```


## ``` Monitor view:```

Can be accessed by opening/hovering on ```Monitor view``` icon (🖥️ -> from left of the page). 

A ```formuale``` is a piece of script that webterminal executes for every configurable interval of time and shows it on the webpage.

##### Usecases 

To check if ```appserver is running```, you can find the status on pw1_appserver monitor (refreshes for every 10 seconds)

## Admin guide 

To check the Admin guide ```developing apps, monitors and more```, please refer to this [DOC](README_ADMIN.md).

Build: TODO HERE

```diff

Disclaimer: 

+ This tool is strictly for internal use only
- DO NOT distribute to any client - not to be run on any PROD/UAT client systems.
```

##### Codename: Project everything 
