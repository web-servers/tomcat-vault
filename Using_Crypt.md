## Using CRYPT with the Vault

A new feature was added to the Vault which allows users to utilize encrypted passwords in configuration files which are **not** stored in the vault.

### Installation

See the [INSTALL](./INSTALL) file for instructions on installation and usage of the vault (minus the CRYPT feature).

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
Encrypted value: CRYPT::9kofG2Sd1qUdDT0+XIKx+rzjsAZulJJQ
~~~

Once you have the encrypted string, copy and paste that string into your configuration. Example:

~~~
$ tail -n2 conf/tomcat-users.xml | head -n1
<user username="tomcat" password="${CRYPT::9kofG2Sd1qUdDT0+XIKx+rzjsAZulJJQ}" roles="manager-gui"/>
~~~
