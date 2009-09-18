.PHONY: client compiler framework service runclient runclientsandbox runservice runservicesandbox test javadoc clean

all: client compiler framework service javadoc

client:
	$(MAKE) -C JasonClient

compiler:
	$(MAKE) -C JasonCompiler

framework:
	$(MAKE) -C JasonFramework

service:
	$(MAKE) -C JasonService

runclient:
	$(MAKE) -C JasonClient run

runclientsandbox:
	$(MAKE) -C JasonClient runsandbox

runservice:
	$(MAKE) -C JasonService run

runservicesandbox:
	$(MAKE) -C JasonService runsandbox

test:
	$(MAKE) -C JasonService test

javadoc:
	$(MAKE) -C javadoc

clean:
	$(MAKE) -C JasonClient clean
	$(MAKE) -C JasonCompiler clean
	$(MAKE) -C JasonFramework clean
	$(MAKE) -C JasonService clean
	$(MAKE) -C javadoc clean
