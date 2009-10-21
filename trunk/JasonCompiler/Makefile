.PHONY: all clean force

all: dist/JasonCompiler.jar

dist/JasonCompiler.jar: \
  build/classes/jason/compiler/JasonProcessor.class \
  build/classes/jason/compiler/Merge.class
	jar cfe $@ jason.compiler.Merge -C build/classes jason

build/classes/%.class: src/%.java ../JasonAnnotations/dist/JasonAnnotations.jar
	javac -cp build/classes:../JasonAnnotations/dist/JasonAnnotations.jar -d build/classes $<

../JasonAnnotations/%: force
	$(MAKE) -C ../JasonAnnotations $*

clean:
	rm -rf build/classes/* dist/*
