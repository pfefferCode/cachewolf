if not exist bin\CacheWolf mkdir bin\CacheWolf
if not exist bin\exp mkdir bin\exp
javac -cp ./lib/CompileEwe.zip;./lib/ewesoft.zip;./lib/EwesoftRegex.zip;./lib/HTML.zip;./lib/openmap.jar  -d ./bin/ -deprecation ./src/CacheWolf/*.java ./src/exp/*.java 