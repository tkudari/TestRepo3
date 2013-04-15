echo Build Project

echo Remove bin directory
rm -r bin

echo Change to directory /ra/wallpaper/android/main
cd ../../../../../ra/wallpaper/android/main

echo Change to Library project
ant change-to-library

echo Change to directory /extensions/../common/dashconfig/project 
cd ../../../../extensions/motorola/common/dashconfig/project

echo Include Wallpaper Android Project as Library
ant include-android-wallpaper

echo Build APK
ant clean debug
