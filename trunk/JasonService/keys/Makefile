.PHONY: certificatesignrequest generatekeypair importcacertificate importcertificate clean

certificate: service.crt
certificatesignrequest: service.csr
generatekeypair: keystore.jks
importcacertificate: ../../JasonFramework/keys/CAcertificate.crt keystore.jks
	keytool -importcert -alias cacertificate -keypass keypass -storepass storepass -file $< -keystore keystore.jks
importcertificate: service.crt
	keytool -importcert -alias servicecertificate -keypass keypass -storepass storepass -file $< -keystore keystore.jks

keystore.jks:
	keytool -genkeypair -alias service -keysize 1024 -keystore $@ -storepass storepass -keypass keypass -dname "CN=JASON Example Service, OU=Digital Security, O=Radboud Universiteit, L=Nijmegen, ST=Gelderland, C=NL"

service.csr: keystore.jks
	keytool -certreq -alias service -keystore $< -keypass keypass -storepass storepass -file $@

service.crt: service.csr ../../JasonFramework/keys/CAcertificate.crt ../../JasonFramework/keys/CAprivate.key
	openssl x509 -req -in $< -out $@ -CA ../../JasonFramework/keys/CAcertificate.crt -CAkey ../../JasonFramework/keys/CAprivate.key -CAcreateserial -CAserial ../../JasonFramework/keys/serial.seq -passin pass:CApassword

../../JasonFramework/keys/%:
	$(MAKE) -C ../../JasonFramework/keys $(patsubst ../../JasonFramework/keys/%,%,$@)

clean:
	rm -f keystore.jks service.csr service.crt
