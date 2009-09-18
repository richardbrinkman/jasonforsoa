SOURCES=$(wildcard\
  ../JasonClient/src/*/*/*/*.java\
  ../JasonCompiler/src/*/*/*.java\
  ../JasonService/src/ServiceSource/*/*/*.java\
  ../JasonService/src/StartupSource/*/*/*/*.java\
  ../JasonFramework/src/*/*/*.java)

../javadoc: $(SOURCES)
	$(MAKE) clean
	javadoc -classpath ../JasonFramework/lib/wss4j-1.5.4.jar -doctitle "Javacard As Secure Objects Network (JASON)" -author -quiet -windowtitle JASON -header "<a href=\"http://cs.ru.nl/jason\" target=\"_top\">JASON</a>" -link http://java.sun.com/javase/6/docs/api $^

clean:
	rm -rf *.html jason package-list resources stylesheet.css
