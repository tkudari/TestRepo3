#!/bin/bash
# Call this script with product oem as parameter, for example
# ./initShell.sh touchwall samsung
echo DashConfig Environment Configuration

help() 
{
	echo "Usage: initShell.sh product brand oem"
	echo "	product: 	r2g | touchwall"
	echo "	brand:		dashwire | att"
	echo "	oem:		htc4 | htc5 | samsung | motorolla | sony | pantech"
	echo ""
	echo "	This script is intentended as a helper to allow developers to set up"
	echo "	their work environments efficiently. See custom_rules.xml for the full"
	echo "	set up supported commands."
}


if [ $# -ne 3 ]; then
     help
     exit 127
fi


PRODUCT=$1
BRAND=$2
OEM=$3
DIR="$( cd "$( dirname "${BASH_SOURCE[0]}" )" && pwd )/"
PATH=${DIR}/tools/mac:$PATH
export PATH
export WORKSPACE=${DIR}/
cd ${DIR}
echo DIR = ${DIR}
echo BRAND = ${BRAND}
if [ ${PRODUCT} = "r2g" ]
then
    cd ${DIR}/extensions/${OEM}/common/dashconfig/project
else
    cd ${DIR}/ra/${PRODUCT}/${BRAND}/main
fi
export PRODUCT
export OEM
export BRAND
bash