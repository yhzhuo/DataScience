#!/bin/sh

while read line ;
do
    if [[ "$(echo $line | carmel -slik 1 $1 2>&1)" == *Empty* ]]
    then
        echo "$line => *none* $(echo $line | carmel -slik 1 $1 2>/dev/null)"
    else
        var="$(echo $line | carmel -slik 1 $1 2>/dev/null)"
        echo "$line => $(java trans $(echo $var | sed 's/\"/ /g'))"

fi
done < $2