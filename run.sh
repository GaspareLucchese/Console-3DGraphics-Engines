mkdir -p out

javac -d out $(find . -name "*.java")

mkdir -p out/scene

cp scene/Teapot.txt out/scene/

java -cp out scene.Testing
