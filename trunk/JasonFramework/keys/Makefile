.PHONY: all clean

CAPASSWORD=CApassword

all: CAcertificate.crt PrivateKeyStore.jks

CAcertificate.crt: CAprivate.key
	openssl req -new -key $< -x509 -days 3650 -out $@ -passin pass:$(CAPASSWORD)

CAprivate.key:
	openssl genrsa -out CAprivate.key -des -passout pass:$(CAPASSWORD) 2048

PrivateKeyStore.jks:
	keytool -keystore PrivateKeyStore.jks -storepass storepass -genkeypair -alias JasonFramework -keypass keypass -dname "CN=Jason Framework, OU=Digital Security, O=Radboud University, L=Nijmegen, ST=Gelderland, C=NL"

clean:
	rm -f CAprivate.key CAcertificate.crt PrivateKeyStore.jks
