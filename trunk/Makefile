.PHONY: annotations client compiler framework service runclient runclientsandbox runservice runservicesandbox test javadoc clean

all: annotations client compiler framework service javadoc

annotations:
	@$(MAKE) -C JasonAnnotations

client:
	@$(MAKE) -C JasonClient

compiler:
	@$(MAKE) -C JasonCompiler

framework:
	@$(MAKE) -C JasonFramework

service:
	@$(MAKE) -C JasonService

runservice:
	@$(MAKE) -C JasonService run

runclient:
	@$(MAKE) -C JasonClient run

runclientsandbox:
	@$(MAKE) -C JasonClient runsandbox


runservicesandbox:
	@$(MAKE) -C JasonService runsandbox

test:
	@$(MAKE) -C JasonService test

javadoc:
	@$(MAKE) -C javadoc

clean:
	@$(MAKE) -C JasonAnnotations clean
	@$(MAKE) -C JasonClient clean
	@$(MAKE) -C JasonCompiler clean
	@$(MAKE) -C JasonFramework clean
	@$(MAKE) -C JasonService clean
	@$(MAKE) -C javadoc clean
