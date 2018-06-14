#!/bin/bash
sudo cryptsetup luksChangeKey /dev/sdb3 -i1 -d "waleed" "julien" || fail