SOURCES=$(filter-out %/package-info.java,$(wildcard src/*/*/*.java src/*/*/*/*.java))
CLASSES=$(patsubst src/%.java,build/classes/%.class,$(SOURCES))

.PHONY: all jar clean

all: dist/JasonFramework.jar 

dist/JasonFramework.jar: \
  keys/PrivateKeyStore.jks \
	$(CLASSES)
	jar cf $@ -C build/classes jason
	jarsigner -keystore $< -storepass storepass -keypass keypass $@ JasonFramework

build/classes/%.class: src/%.java
	javac -cp build/classes:lib/wss4j-1.5.4.jar -d build/classes -sourcepath src $<

keys/PrivateKeyStore.jks:
	make -C keys PrivateKeyStore.jks

clean:
	rm -rf build/classes/* dist/*
