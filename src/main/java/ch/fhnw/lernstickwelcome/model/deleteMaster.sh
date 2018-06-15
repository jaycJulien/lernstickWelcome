#!/bin/bash
export HISTIGNORE="expect*";
passwd=$1
partitionName=$2
expect -c "
        spawn sudo cryptsetup luksKillSlot /dev/$partitionName 1
        expect "?assphrase:"
        send \"$passwd\r\"
        expect eof"
 
export HISTIGNORE="";