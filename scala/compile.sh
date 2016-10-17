#!/usr/bin/env bash
scalac Main.scala
echo "Now you can run the program using the command: "
echo "scala Main user_1 user_2 file.csv"
echo "Example: scala Main 600dfbe2 5e7b40e1 reduced.csv"
echo "(the program only takes positional arguments: mind the order! Thanks)"
