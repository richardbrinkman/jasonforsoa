.PHONY: all run runsandbox keys clean force

LIBRARY.CLASSPATH=../JasonFramework/dist/JasonFramework.jar:../JasonFramework/lib/commons-logging-1.0.4.jar:../JasonFramework/lib/opensaml-1.0.1.jar:../JasonFramework/lib/wss4j-1.5.4.jar:../JasonFramework/lib/xmlsec-1.4.0.jar

all: dist/JasonClient.jar ../JasonFramework/dist/JasonFramework.jar

dist/JasonClient.jar: \
  build/classes/jason/test/client/Client.class \
  build/classes/jason/test/client/package-info.class \
  build/classes/jason/test/client/ObjectFactory.class \
  build/classes/jason/test/client/Service.class \
  build/classes/jason/test/client/ServiceService.class \
  build/classes/jason/test/client/SayHello.class \
  build/classes/jason/test/client/SayHelloResponse.class
	jar cfe dist/JasonClient.jar jason.test.client.Client -C build/classes jason

build/classes/%.class: src/%.java
	$(MAKE) -C ../JasonFramework dist/JasonFramework.jar
	javac -cp build/classes:$(LIBRARY.CLASSPATH) -d build/classes $<

run: dist/JasonClient.jar ../JasonFramework/dist/JasonFramework.jar
	java -cp build/classes:$(LIBRARY.CLASSPATH) jason.test.client.Client

runsandbox: dist/JasonClient.jar ../JasonFramework/dist/JasonFramework.jar build/classes/jason.policy
	java -cp dist/JasonClient.jar:../JasonFramework/dist/JasonFramework.jar:../JasonFramework/lib/commons-logging-1.0.4.jar:../JasonFramework/lib/opensaml-1.0.1.jar:../JasonFramework/lib/wss4j-1.5.4.jar:../JasonFramework/lib/xmlsec-1.4.0.jar -Djava.security.manager -Djava.security.policy=build/classes/jason.policy jason.test.client.Client
#	cd build/classes; java -cp .:../../../JasonFramework/dist/JasonFramework.jar:../../../JasonFramework/lib/commons-logging-1.0.4.jar:../../../JasonFramework/lib/opensaml-1.0.1.jar:../../../JasonFramework/lib/wss4j-1.5.4.jar:../../../JasonFramework/lib/xmlsec-1.4.0.jar -Djava.security.manager -Djava.security.policy=jason.policy jason.test.client.Client; cd ../../

keys:
	$(MAKE) -C keys

clean:
	rm -rf build/classes/*

../JasonFramework/dist/JasonFramework.jar: force
	make -C ../JasonFramework dist/JasonFramework.jar

build/classes/jason/test/client/Service.class \
build/classes/jason/test/client/ServiceService.class \
build/classes/jason/test/client/SayHello.class \
build/classes/jason/test/client/SayHelloResponse.class: \
  ../JasonService/build/classes/Service/ServiceService.wsdl
	wsimport -d build/classes -p jason.test.client ../JasonService/build/classes/Service/ServiceService.wsdl

../JasonService/build/classes/Service/ServiceService.wsdl:
	make -C ../JasonService build/classes/Service/ServiceService.wsdl

build/classes/jason.policy: src/jason.policy
	cp $< build/classes

#dependencies:
build/classes/jason/test/client/Client.class: \
  build/classes/jason/test/client/Service.class \
  build/classes/jason/test/client/ServiceService.class
