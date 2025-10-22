Azure MySQL connection setup (local dev)

This project includes `src/main/resources/DigiCertGlobalRootG2.crt.pem` which you can import into a Java truststore to allow the JVM to verify the Azure MySQL server certificate when using sslMode=VERIFY_CA.

Steps

1) Create the truststore (run from project root):

   keytool -importcert -alias digicert-global-root-g2 -file src/main/resources/DigiCertGlobalRootG2.crt.pem \
     -keystore src/main/resources/azure-truststore.jks -storepass changeit -noprompt

2) Update `application.properties` if needed:

   - Ensure `spring.datasource.username` is in the form `user@servername` (example already set in file).
   - Keep `spring.datasource.url` with `?sslMode=VERIFY_CA`.

3) Run the app with the truststore JVM args (example):

   ./mvnw.cmd spring-boot:run -Dspring-boot.run.jvmArguments="-Djavax.net.ssl.trustStore=src/main/resources/azure-truststore.jks -Djavax.net.ssl.trustStorePassword=changeit"

   Or when running the packaged jar:

   java -Djavax.net.ssl.trustStore=src/main/resources/azure-truststore.jks -Djavax.net.ssl.trustStorePassword=changeit -jar target/dentalsurgeryapp-0.0.1-SNAPSHOT.jar

4) Verification

   - Check the application logs for successful Hibernate startup and no SSL errors.
   - If connection fails, temporarily try `sslMode=REQUIRED` to see if the problem is certificate verification versus connectivity/credentials.

Security notes

- Do NOT commit production truststores or passwords to source control.
- Use secure secret storage for credentials (Azure Key Vault, environment variables, or Spring Cloud Config) in production.
