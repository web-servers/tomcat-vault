## Using CRYPT with the Vault

A new feature was added to the Vault which allows users to utilize encrypted passwords in configuration files which are **not** stored in the vault.

### Installation

See the [INSTALL](./INSTALL.md) file for instructions on installation and usage of the vault (minus the CRYPT feature).

### Using the CRYPT feature

Configure Tomcat to use your password from an encrypted or plain text string:

- **Encrypted**: Add the encryption password to the vault and put the VAULT reference in your vault.properties as follows:

    ~~~
    ENCRYPTION_PASSWORD=VAULT::some_block::some_attribute::
    ~~~

- **Plain Text**: Add the password to conf/catalina.properties, or pass it in as a system property to java:

    ~~~
    org.apache.tomcat.vault.util.ENCRYPTION_PASSWORD=MyEncryptionPassword
    ~~~

**Note: Setting ENCRYPTION_PASSWORD in vault.properties will override org.apache.tomcat.vault.util.ENCRYPTION_PASSWORD.**

Now that you have a password configured, you can encrypt some value and put the resulting string in your configuration file to be decrypted on the fly:

~~~
$ java -cp lib/tomcat-juli.jar:lib/tomcat-util.jar:lib/tomcat-vault.jar org.apache.tomcat.vault.util.PropertySourceVault MyEncryptionPassword MyPassword
Specified value: MyPassword
Encrypted value: CRYPT::9kofG2Sd1qUdDT0+XIKx+rzjsAZulJJQ
~~~

Once you have the encrypted string, copy and paste that string into your configuration. Example:

~~~
$ tail -n2 conf/tomcat-users.xml | head -n1
<user username="tomcat" password="${CRYPT::9kofG2Sd1qUdDT0+XIKx+rzjsAZulJJQ}" roles="manager-gui"/>
~~~

---

If you store the encrption password in vault like:

~~~
$ ./bin/vault.sh --keystore /tmp/vault/vault.keystore --keystore-password my_password123 --alias my_vault --enc-dir /tmp/vault/ --iteration 120 --salt 1234abcd --vault-block my_block --attribute my_encryption_password --sec-attr MyEncryptionPassword
~~~

and add ENCRYPTION_PASSWORD to vault.properties:

~~~
ENCRYPTION_PASSWORD=VAULT::my_block::my_encryption_password::
~~~

Then you can omit the encryption password from the command line argument by specifying the system property `org.apache.tomcat.vault.util.VAULT_PROPERTIES` which point to `conf/vault.properties`:

~~~
$ java -cp lib/tomcat-juli.jar:lib/tomcat-util.jar:lib/tomcat-vault.jar -Dorg.apache.tomcat.vault.util.VAULT_PROPERTIES=/path/to/conf/vault.properties org.apache.tomcat.vault.util.PropertySourceVault MyPassword
Dec 05, 2017 8:51:03 PM org.apache.tomcat.vault.security.vault.PicketBoxSecurityVault init
INFO: Default Security Vault Implementation Initialized and Ready
Specified value: MyPassword
Encrypted value: CRYPT::0jvbWXo8EVQqJCt0lJj8jtX3k4jWJr4f
~~~

