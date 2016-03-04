# Wifi AP configuration for Android TV #

[![Build Status](https://travis-ci.org/akinaru/androidtv-access-point.svg?branch=master)](https://travis-ci.org/akinaru/androidtv-access-point)
[![License](https://img.shields.io/hexpm/l/plug.svg)](LICENSE.md)

An Android TV application used to configure your Wifi Access Point (for Android L only, this wont work on Android M)

![screenshot](screen/screen.gif)

This application uses private API for enabling/disabling Access Point. It may not be working on your Android TV

## Requirements

Android TV 5.0 (sdk 21) or Android TV 5.1.1 (sdk 22)

## Pros & Cons

Pros :
* address the lack of Wifi AP configuration in Google TV Settings
* built with Google Leanback framework
* interface very similar to Google TvSettings

Cons :
* uses private APIs that may not be available on devices
* doesnt work on some devices (see incompatible device list) - require AOSP patches to fix Wifi Tethering (didnt work on first releases)
* doesnt work on Android M due to `android.provider.Settings.ACTION_MANAGE_WRITE_SETTINGS` intent which is required to be exposed by a System Activity. On Android TV, Google `Settings` applications is not present, only `TvSettings` application is installed which doesnt have this intent

## Compatible devices

* Bbox Miami (5.1.1)

## Incompatible devices

* Sony Bravia 4K (5.1.1)

There seems to have a System service restarting Wifi automatically every second preventing AP to setup :
```
I/BluetoothResetService_v3.11: check wifi and bt status
I/BluetoothResetService_v3.11: checkWifiBtStatus
E/BluetoothResetService_nat_v1.8: run ioctl  fail : Network is down (100)
I/BluetoothResetService_nat_v1.8: bt driver status = 1, if reture 0, bt framework need reset
I/BluetoothResetService_v3.11: bt and wifi Status is normal, nothing to do
```

<hr/>

* Philips 32PFH5500 (5.1.1)
* Freebox Mini 4K (5.0.2)
* Nexus Player Android 5.0/5.1.1

We have an error coming from AP configuration at `netd` level on these devices :
```
E/WifiStateMachine: Exception in softap start java.lang.IllegalArgumentException: command '50 softap fwreload wlan0 AP' failed with '501 50 SoftAP command has failed'
E/WifiStateMachine: Exception in softap re-start java.lang.IllegalArgumentException: command '52 softap fwreload wlan0 STA' failed with '501 52 SoftAP command has failed'
```

## Not tested

* Nvidia Shield (5.1.1)

## External Lib

* `accesspoint` library by Daniel Martí from https://github.com/mvdan/accesspoint for managing access point and retrieving connected client
* `leanback-v17` with some example provided here : https://github.com/googlesamples/androidtv-Leanback and https://github.com/stari4ek/androidtv-Leanback

## License

```
Copyright 2016 Bertrand Martel

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

  http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
```