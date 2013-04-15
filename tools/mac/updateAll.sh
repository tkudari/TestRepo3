echo Update all project directories ...
pushd ${WORKSPACE}
find . -name build.xml -execdir android update project -p . \;
popd
