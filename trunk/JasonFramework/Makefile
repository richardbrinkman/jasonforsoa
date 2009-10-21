SOURCES=$(filter-out %/package-info.java,$(wildcard src/*/*/*.java src/*/*/*/*.java))
CLASSES=$(patsubst src/%.java,build/classes/%.class,$(SOURCES))

LIBRARY.CLASSPATH=dist/JasonFramework.jar:lib/commons-logging-1.0.4.jar:lib/opensaml-1.0.1.jar:lib/wss4j-1.5.4.jar:lib/xmlsec-1.4.0.jar

.PHONY: all run stop force clean

all: dist/JasonFramework.jar 

dist/JasonFramework.jar: \
  keys/PrivateKeyStore.jks \
	$(CLASSES) \
  build/classes/AdministrativeService.wsdl
	jar cfe $@ jason.framework.AdministrativeService -C build/classes jason -C build/classes AdministrativeService.wsdl
	jarsigner -keystore $< -storepass storepass -keypass keypass $@ JasonFramework

build/classes/%.class: src/%.java ../JasonAnnotations/dist/JasonAnnotations.jar
	javac -cp build/classes:lib/wss4j-1.5.4.jar:../JasonAnnotations/dist/JasonAnnotations.jar -d build/classes -sourcepath src $<

build/classes/%.wsp: src/%.java ../JasonAnnotations/dist/JasonAnnotations.jar ../JasonCompiler/dist/JasonCompiler.jar
	javac -cp build/classes:lib/wss4j-1.5.4.jar:../JasonAnnotations/dist/JasonAnnotations.jar:../JasonCompiler/dist/JasonCompiler.jar -d build/classes -processor jason.compiler.JasonProcessor -sourcepath src $<

build/classes/AdministrativeServiceService.wsdl:\
  build/classes/jason/framework/AdministrativeService.class
	wsgen -cp build/classes -keep -d build/classes -wsdl jason.framework.AdministrativeService
	sed -ie 's/REPLACE_WITH_ACTUAL_URL/http:\/\/localhost:8484\/Framework\/AdministrativeService/' $@

build/classes/AdministrativeService.wsdl: \
  build/classes/AdministrativeServiceService.wsdl \
  build/classes/jason/framework/AdministrativeService.wsp \
  ../JasonCompiler/dist/JasonCompiler.jar
	java -jar ../JasonCompiler/dist/JasonCompiler.jar build/classes/AdministrativeServiceService.wsdl build/classes/jason/framework/AdministrativeService.wsp $@

run: dist/JasonFramework.jar
	java -cp $(LIBRARY.CLASSPATH) jason.framework.AdministrativeService start

stop: dist/JasonFramework.jar
	java -cp $(LIBRARY.CLASSPATH) jason.framework.AdministrativeService stop

keys/PrivateKeyStore.jks:
	$(MAKE) -C keys PrivateKeyStore.jks

../JasonCompiler/%: force
	$(MAKE) -C ../JasonCompiler $*

force: ;

../JasonAnnotations/%: force
	$(MAKE) -C ../JasonAnnotations $*

clean:
	rm -rf build/classes/* dist/*
