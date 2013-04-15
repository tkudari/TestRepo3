#!/bin/bash

# this guarantees * in filenames causes error if no match
shopt -s failglob

echo Script to automate pulling DashConfig apks from device.
if [ "$#" -eq 0 ]
then
   echo Usage: pullFromDevice.sh \<local path to place apks\>
   exit
fi
if [ ! -d "$1"  ]
then
   echo Invalid directory: "$1"
   exit
fi

echo Processing parameters ...
PACKAGES=( com.dashwire.config com.dashwire.config.widget com.dashwire.configurator3000 com.dashwire.config.email)

echo Identifying files on device associated with DashWire packages ...
pmRegex="package\:([^\=]*)"
for package in PACKAGES; do
	for line in `adb shell pm list packages -f com.dashwire`; do
	    if [[ "$line" =~ $pmRegex ]]; then
	    	echo Pulling file ${BASH_REMATCH[1]} ...
	        adb pull "${BASH_REMATCH[1]}" "$1"
	    else
	        echo "Error extracting filename from pm command output."
	    fi
	done
done