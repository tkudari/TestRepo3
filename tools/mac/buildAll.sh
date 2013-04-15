echo Build all projects ...
pushd ${WORKSPACE}
find . -name build.xml -execdir ant clean debug \; | grep -E 'Buildfile|BUILD'
popd
