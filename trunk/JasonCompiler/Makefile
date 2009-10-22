.PHONY: all clean force

all: dist/JasonCompiler.jar

dist/JasonCompiler.jar: \
  build/classes/jason/compiler/JasonProcessor.class \
  build/classes/jason/compiler/Merge.class
	@mkdir -p dist
	jar cfe $@ jason.compiler.Merge -C build/classes jason

build/classes/%.class: src/%.java ../JasonAnnotations/dist/JasonAnnotations.jar
	@mkdir -p build/classes
	javac -cp build/classes:../JasonAnnotations/dist/JasonAnnotations.jar -d build/classes $<

../JasonAnnotations/%: force
	@$(MAKE) -C ../JasonAnnotations $*

clean:
	rm -rf build/classes dist
