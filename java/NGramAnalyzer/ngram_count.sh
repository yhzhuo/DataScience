#!/bin/sh
javac *.java
java expand_fsm $1 > $2