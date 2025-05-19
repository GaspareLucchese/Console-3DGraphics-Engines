@echo off
mkdir out 2>nul

dir /S /B *.java > sources.txt
javac -d out @sources.txt
del sources.txt

mkdir out\scene 2>nul

copy /Y scene\Teapot.txt out\scene\Teapot.txt >nul

java -cp out scene.Testing
