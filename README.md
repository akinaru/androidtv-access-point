# Wifi AP configuration for Android TV #

[![License](http://img.shields.io/:license-mit-blue.svg)](LICENSE.md)

An Android TV application used to configure your Wifi Access Point

![screenshot](screen/screen.gif)

## Requirements

Android TV 5.0 (sdk 21) or Android TV 5.1.1 (sdk 22)

## Pros & Cons

Pros :
* address the lack of Wifi AP configuration in Google TV Settings
* built with Google Leanback framework
* interface very similar to Google TvSettings

Cons :
* uses private APIs that may not be available on devices
* doesnt work on some devices (see incompatible device list)
* doesnt work on Android M due to `android.provider.Settings.ACTION_MANAGE_WRITE_SETTINGS` intent which is required to be exposed by a system activity. On Android TV, Google `Settings` applications is not installed, only `TvSettings` application which doesnt have this intent

## Compatible device

* Bbox Miami

## Incompatible devices

* Sony Bravia 4K
* Philips 32PFH5500

## Not tested

* Nexus Player
* Nvida Shield

## External Lib

* `accesspoint` library by Daniel Martí from https://github.com/mvdan/accesspoint for managing access point and retrieving connected client
* `leanback-v17` with some example provided here : https://github.com/googlesamples/androidtv-Leanback and https://github.com/stari4ek/androidtv-Leanback