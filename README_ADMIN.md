
# Admin guide:

Running the commands from the **The *easy* way** downloads/updates the server with latest version. Default ```app workspace``` is ```webterminal-app``` folder in current user directory

## ```- Apps```

Application data (apps) is persisted in ```webterminal-data/plugins``` folder (in app workspace). 

To ```add an app```, we need drop the implementation into above directory and the change would be live dynamically. Simple apps can be created from UI.

Every app has a ```appspace directory``` and the main file to specify data is ```action.json```.

##### Each app execution has a ```lifecycle```:

* Resource download (downloads content from urls specified in ```requirements.list```) [optional]

* Pre execution (executes the script ```Pre.java```, if available) [optional]

* Command execution (Executes the command mentioned in ```action.json```)

* Email notification (Sends an email post execution ```WEBTERMINAL_EMAIL_NOTIFICATION```, if specified from UI) [optional]

```json
action.json (minimum fields)
  {
    "command": "./test.sh",
    "workspace": "/home/user"
  }

Variables:
  Optionally, you can declare variables (PROCESSPID below) and they would be prompted to user during execution.
  {
    "command": "kill -9 $(PROCESSPID)",
    "workspace": ""
  }

  Similarly, @(VARIABLENAME) would be injected to Pre.java as environment variable.

Optional properties:
  {
    "description": "Renders the HTML content on confirmation panel EG: This action does <br> <b>something</b>",  
    "showconfirmation": "default=true, EG: false",    
    "compatibility": "this would only show up the app in specified OS EG: windows OR linux",
    "command_os": { "linux": "./test.sh", "windows": "test.bat" }
  }
```

Every command would get executed in a dedicated directory under ```webterminal-temp``` folder (in app workspace). Please lookout for the size and cleanup if needed.

## ```- Monitor```

Monitoring data (monitor) is persisted in ```webterminal-data/formulae``` folder (in app workspace). Folder structure would be similar to apps.

Every monitor can be implemented in its folder -  ```Formula.java```. 

The method ```getFrequency``` need to refresh time in seconds, method - ```execute``` needs to return the data to be displayed (as string/html).

Web terminal would ```display all formulaes``` on the UI and refresh them as per their frequency.
