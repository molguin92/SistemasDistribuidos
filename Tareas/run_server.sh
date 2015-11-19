#!/bin/bash
cd src
javac cc5303/tareas/olguin_manuel/*.java
rmiregistry &
java cc5303.tareas.olguin_manuel.GameServer $1 ../iplist.conf -n 4
killall rmiregistry
