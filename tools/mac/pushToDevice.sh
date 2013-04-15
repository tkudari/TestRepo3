#!/bin/bash

# this guarantees * in filenames causes error if no match
shopt -s failglob

echo Script to automate installing DashConfig apks to device.
if [ "$#" -eq 0 ]
then
   echo Usage: pushToDevice.sh \<local path to apks\>
   exit
fi
if [ ! -d "$1"  ]
then
   echo Invalid apk directory: "$1"
   exit
fi

echo Switching to apk directory ...
pushd $1

echo Processing parameters ...
PACKAGES=( com.dashwire.config com.dashwire.config.widget com.dashwire.configurator3000 com.dashwire.config.email)
APKS=( *.apk )


echo Waiting for device ...
adb wait-for-device

echo Ensuring adb is running as root ...
adb root
sleep 3s

echo Mounting with write permission /system filesystem ...
# adb shell mount -o rw,remount /dev/block/st19 /system

# Old method, replaced by above.
# echo Remounting /system filesystem ...
adb remount

echo Forcing processes to stop ...
for var in "${PACKAGES[@]}"
do
  adb shell "am force-stop ${var}"
done

echo Identifying files on device associated with Dashwire packages, and deleting them.
pmRegex="package\:([^\=]*)"
for package in PACKAGES; do
	for line in `adb shell pm list packages -f com.dashwire`; do
	    if [[ "$line" =~ $pmRegex ]]; then
	        echo Deleting file ${BASH_REMATCH[1]}
	        adb shell "rm -r ${BASH_REMATCH[1]}"
	    else
	        echo "Error extracting filename from pm command output."
	    fi
	done
done

echo Uninstalling Dashwire packages ...
for var in "${PACKAGES[@]}"
do
  echo   ${var}
  adb uninstall ${var}
done

echo Pushing new apks to device ...
for var in "${APKS[@]}"
do
  echo   ${var}
  adb push ${var} /system/app
done

# echo Rebooting the device ...
# adb -d reboot 

# read -p "Press [Enter] once device has restarted ..."

# echo Waiting for device to wake back up ...
# adb wait-for-device

# read -p "Press [Enter] when ready to launch Configurator3000 ..."

# adb shell am start -n com.dashwire.configurator3000/.SplashActivity

popd
