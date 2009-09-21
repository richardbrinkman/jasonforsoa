.PHONY: all run runsandbox test keys clean force

LIBRARY.CLASSPATH=../JasonFramework/dist/JasonFramework.jar:../JasonFramework/lib/commons-logging-1.0.4.jar:../JasonFramework/lib/opensaml-1.0.1.jar:../JasonFramework/lib/wss4j-1.5.4.jar:../JasonFramework/lib/xmlsec-1.4.0.jar:/usr/share/java/xalan-j2-2.7.0.jar

all: dist/JasonStartup.jar dist/JasonService.jar 

dist/JasonStartup.jar: \
  build/classes/Startup/jason/test/server/Startup.class
	jar cfe $@ jason.test.server.Startup -C build/classes/Startup jason

dist/JasonService.jar: \
  build/classes/Service/jason/test/server/Service.class \
  build/classes/Service/jason/test/server/jaxws/SayHello.class \
  build/classes/Service/jason/test/server/jaxws/SayHelloResponse.class \
  build/classes/Service/ServiceService.wsdl \
  build/classes/Service/Service.wsdl
	jar cf $@ -C build/classes/Service jason

build/classes/Startup/%.class: src/StartupSource/%.java ../JasonFramework/dist/JasonFramework.jar
	javac -cp build/classes/Startup:$(LIBRARY.CLASSPATH) -d build/classes/Startup -sourcepath src/StartupSource $<

build/classes/Service/%.class: src/ServiceSource/%.java ../JasonFramework/dist/JasonFramework.jar ../JasonCompiler/dist/JasonCompiler.jar
	javac -cp build/classes/Service:$(LIBRARY.CLASSPATH):../JasonCompiler/dist/JasonCompiler.jar -processor jason.compiler.JasonProcessor -d build/classes/Service -sourcepath src/ServiceSource $<

run: dist/JasonStartup.jar dist/JasonService.jar ../JasonFramework/dist/JasonFramework.jar
	java -cp dist/JasonStartup.jar:$(LIBRARY.CLASSPATH) jason.test.server.Startup

runsandbox: dist/JasonStartup.jar dist/JasonService.jar ../JasonFramework/dist/JasonFramework.jar build/classes/Startup/jason.policy
	java -cp dist/JasonStartup.jar:$(LIBRARY.CLASSPATH) -Djava.security.manager -Djava.security.policy=build/classes/Startup/jason.policy jason.test.server.Startup

test: \
  build/test/classes/jason/test/server/KeyStoreTest.class \
  build/test/classes/jason/test/server/CryptoTest.class \
  build/test/classes/jason/test/server/WsdlParseTest.class \
  ../JasonFramework/dist/JasonFramework.jar
	java -cp /usr/share/java/junit4.jar:build/test/classes:$(LIBRARY.CLASSPATH) org.junit.runner.JUnitCore jason.test.server.KeyStoreTest jason.test.server.CryptoTest jason.test.server.WsdlParseTest

build/test/classes/%.class: test/%.java ../JasonFramework/dist/JasonFramework.jar
	javac -d build/test/classes -cp /usr/share/java/junit4.jar:$(LIBRARY.CLASSPATH) $<

keys:
	$(MAKE) -C keys

clean:
	rm -rf build/classes/Service/* build/classes/Startup/* build/test/classes/* dist/*.jar

build/classes/Service/ServiceService.wsdl \
build/classes/Service/jason/test/server/jaxws/SayHello.class \
build/classes/Service/jason/test/server/jaxws/SayHelloResponse.class: \
  build/classes/Service/jason/test/server/Service.class
	wsgen -cp build/classes/Service -d build/classes/Service -wsdl jason.test.server.Service
	sed -ie 's/REPLACE_WITH_ACTUAL_URL/http:\/\/localhost:8080\/Service\/ServiceService/' build/classes/Service/ServiceService.wsdl

build/classes/Service/Service.wsdl: \
  build/classes/Service/ServiceService.wsdl \
  build/classes/Service/jason/test/server/Service.wsp
	$(MAKE) -C ../JasonCompiler dist/JasonCompiler.jar
	java -cp ../JasonCompiler/dist/JasonCompiler.jar jason.compiler.Merge $+ $@

build/classes/Startup/jason.policy: src/StartupSource/jason.policy
	cp $< build/classes/Startup

force: ;

../JasonCompiler/%: force
	make -C ../JasonCompiler $*

../JasonFramework/%: force
	make -C ../JasonFramework $*
