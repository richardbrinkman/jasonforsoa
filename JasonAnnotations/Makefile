.PHONY: all clean

SOURCES=$(filter-out %/package-info.java,$(wildcard src/jason/annotation/*.java) $(wildcard src/jason/framework/*.java))
CLASSES=$(patsubst src/%.java,build/classes/%.class,$(SOURCES))

all: dist/JasonAnnotations.jar

dist/JasonAnnotations.jar: $(CLASSES)
	@mkdir -p dist
	jar cf $@ -C build/classes jason

build/classes/%.class: src/%.java
	@mkdir -p build/classes
	javac -cp build/classes -d build/classes -sourcepath src $<

clean:
	rm -rf build dist
