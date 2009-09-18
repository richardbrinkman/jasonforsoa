CLASSPATH=build/classes:../JasonFramework/dist/JasonFramework.jar
TEST_CLASSPATH=$(CLASSPATH):lib/webservices-rt.jar:build/test/classes:/usr/share/java/junit4.jar

MAIN_SOURCES=$(wildcard src/*/*.java)
MAIN_CLASSES=$(patsubst src/%.java,build/classes/%.class,$(MAIN_SOURCES))
TEST_SOURCES=$(wildcard test/test/*.java)
TEST_CLASSES=$(patsubst test/%.java,build/test/classes/%.class,$(TEST_SOURCES))
TESTS=$(subst /,.,$(patsubst build/test/classes/%.class,%,$(filter build/test/classes/%Test.class,$(TEST_CLASSES))))
CLASSES=$(MAIN_CLASSES) $(TEST_CLASSES)

.PHONY: all runbank runshop runclient test clean

all: $(MAIN_CLASSES)

build/classes/%.class: src/%.java
	javac -d build/classes -sourcepath src -cp $(CLASSPATH) $<

build/test/classes/%.class: test/%.java
	javac -d build/test/classes -sourcepath test -cp $(TEST_CLASSPATH) $<

build/classes/BankService.wsdl: build/classes/bank/Bank.class
	wsgen -cp $(CLASSPATH) -d build/classes -wsdl bank.Bank
	sed -ie 's/REPLACE_WITH_ACTUAL_URL/http:\/\/localhost:8080\/Bank/' $@

build/classes/ShopService.wsdl: build/classes/shop/Shop.class
	wsgen -cp $(CLASSPATH) -d build/classes -wsdl shop.Shop
	sed -ie 's/REPLACE_WITH_ACTUAL_URL/http:\/\/localhost:8081\/Shop/' $@

build/classes/client/BankService.class: build/classes/BankService.wsdl
	wsimport -d build/classes -p client $<

build/classes/client/ShopService.class: build/classes/ShopService.wsdl
	wsimport -d build/classes -p client $<

runbank: build/classes/BankService.wsdl
	java -cp $(CLASSPATH) bank.Bank

runshop: build/classes/ShopService.wsdl
	java -cp $(CLASSPATH) shop.Shop

runclient: build/classes/client/Client.class
	java -cp $(CLASSPATH) client.Client

test: $(TEST_CLASSES)
	java -cp $(TEST_CLASSPATH) org.junit.runner.JUnitCore $(TESTS)
	
clean:
	rm -rf build/classes/*
