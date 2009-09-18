.PHONY: all clean

all: dist/JasonCompiler.jar

dist/JasonCompiler.jar: \
  build/classes/jason/compiler/JasonProcessor.class \
  build/classes/jason/compiler/Merge.class
	jar cf $@ -C build/classes jason

build/classes/%.class: src/%.java ../JasonFramework/dist/JasonFramework.jar
	javac -cp build/classes:../JasonFramework/dist/JasonFramework.jar -d build/classes $<

../JasonFramework/dist/JasonFramework.jar:
	make -C ../JasonFramework dist/JasonFramework.jar

clean:
	rm -rf build/classes/* dist/*
