# Backend

You can run the backend with the gradle task `:backend:run`

# App

The android app requires you to configure the build flavor and potentially the backend url.

## Build Flavor

there are 2 important build flavors. lanBackend and remoteBackend.

### Connecting to lan backend

If you are testing with a local backend on your computer, you can just set
the build flavor lanBackend and run the app in the simulator. It should connect successfully.
The IP address for the simulator to reach your computer is set as the default. If you are running
the app on your mobile phone you have to set the ip address of your computer in the local.properties
file for the phone to connect to your backend on your computer. It looks something like this:

```
backend.lan.host="192.168.1.87"
```

### Connecting 