## Using CRYPT With the Vault

A new feature was added to the Vault which allows users to utilize encrypted values in configuration files which are **not** stored in the vault.

### Installing the Vault

See the [INSTALL](./INSTALL.md) file for instructions on installation and usage of the vault (minus the CRYPT feature, which is fully documented here).

### Using the CRYPT feature

#### Configuring Tomcat

Configure Tomcat to use your password from an encrypted or plain text string:

- **Encrypted**: Add the encryption password to the vault:

    ~~~
    $ bin/vault.sh --keystore /path/to/vault.keystore --keystore-password my_password123 --alias my_vault --enc-dir /path/to/vault/encryption_dir/ --iteration 120 --salt 1234abcd --vault-block my_block --attribute my_encryption_password --sec-attr MyEncryptionPassword
    ~~~

    then, put the VAULT reference in your vault.properties as follows:

    ~~~
    ENCRYPTION_PASSWORD=VAULT::my_block::my_encryption_password::
    ~~~

- **Plain Text**: Add the password to conf/catalina.properties, or pass it in as a system property to java:

    ~~~
    org.apache.tomcat.vault.util.ENCRYPTION_PASSWORD=MyEncryptionPassword
    ~~~

**Note: Setting ENCRYPTION_PASSWORD in vault.properties will override org.apache.tomcat.vault.util.ENCRYPTION_PASSWORD.**

Now that you have a password configured, you can encrypt some value and put the resulting string in your configuration file to be decrypted on the fly:

#### Encrypting Values for Use

You can encrypt values one of two ways.

Method 1, using a plain text encryption password:
~~~
$ bin/vault.sh --encrypt MyEncryptionPassword MyPassword
=========================================================================

  Tomcat Vault

  VAULT_HOME: /path/to/tomcat-vault/lib

  JAVA: java

=========================================================================

Encrypted value: CRYPT::a33AiwJkF4dMx9Uq9oxElYT6LdjXLJxf
~~~

Method 2, using an encryption password which is stored in the vault:

~~~
$ bin/vault.sh --keystore /path/to/vault.keystore --keystore-password my_password123 --alias my_vault --enc-dir /path/to/vault/encryption_dir/ --encrypt VAULT::my_block::my_encryption_password:: MyPassword
=========================================================================

  Tomcat Vault

  VAULT_HOME: /path/to/tomcat-vault/lib

  JAVA: java

=========================================================================

Dec 06, 2017 12:54:30 PM org.apache.tomcat.vault.security.vault.PicketBoxSecurityVault init
INFO: Default Security Vault Implementation Initialized and Ready
Encrypted value: CRYPT::z9zbQSywH7iqmNJOW/wM++TKfF13U8/e
~~~

#### Two Usage Examples

Once you have the encrypted string (copied from the command output above), place that string into your configuration inside of brackets, such as ${}, so that Tomcat's Digester correctly interpolates it.

An XML example would look like:

~~~
$ tail -n2 conf/tomcat-users.xml | head -n1
<user username="tomcat" password="${CRYPT::a33AiwJkF4dMx9Uq9oxElYT6LdjXLJxf}" roles="manager-gui"/>
~~~

A properties file example would look like:

~~~
$ tail -n1 conf/catalina.properties
test.property=${CRYPT::a33AiwJkF4dMx9Uq9oxElYT6LdjXLJxf}
~~~

Note that the properties file does **not** need quotations marks.
