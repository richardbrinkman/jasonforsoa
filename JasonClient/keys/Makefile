.PHONY: none importcacertificate importservicecertificate clean

none: 

importcacertificate: ../../JasonFramework/keys/CAcertificate.crt
	keytool -importcert -alias cacertificate -keypass keypass -storepass storepass -file $< -keystore keystore.jks

importservicecertificate: ../../JasonService/keys/service.crt
	keytool -importcert -alias servicecertificate -keypass keypass -storepass storepass -file $< -keystore keystore.jks

../../JasonFramework/keys/%:
	$(MAKE) -C ../../JasonFramework/keys $@

../../JasonService/keys/%:
	$(MAKE) -C ../../JasonService/keys $@

clean:
	rm -f keystore.jks
