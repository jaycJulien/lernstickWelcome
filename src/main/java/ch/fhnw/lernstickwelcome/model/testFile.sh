#!/bin/bash
export HISTIGNORE="expect*";
passwd=$1
expect -c "
        spawn sudo cryptsetup luksKillSlot /dev/sdb3 1
        expect "?assphrase:"
        send \"$passwd\r\"
        expect eof"
 
export HISTIGNORE="";