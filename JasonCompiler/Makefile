.PHONY: all clean

all: dist/JasonCompiler.jar

dist/JasonCompiler.jar: \
  build/classes/jason/compiler/JasonProcessor.class \
  build/classes/jason/compiler/Merge.class
	jar cfe $@ jason.compiler.Merge -C build/classes jason

build/classes/%.class: src/%.java 
	@$(MAKE) --no-print-directory -C ../JasonFramework $*
	javac -cp build/classes:../JasonFramework/dist/JasonFramework.jar -d build/classes $<

clean:
	rm -rf build/classes/* dist/*
