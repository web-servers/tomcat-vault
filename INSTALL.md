PicketLink Vault extension for Apache Tomcat.

See the LICENSE file distributed with this work for information
regarding licensing.

=====================================================================

Requirements:
-------------

* Tomcat Vault tarball or source repository
* Apache Tomcat 8.0.15+
* Apache Maven

Prequisites:
------------

Configure the `CATALINA_BASE` environment variable to point to your Tomcat installation's `CATALINA_BASE`.

Building and Configuring Your Vault:
------------------------------------

1. Install Apache Tomcat (from an RPM, by hand, or however you prefer)

2. Compile Tomcat Vault from it's source directory:

    ~~~
    $ mvn package
    ~~~

3. Copy the generated tomcat-vault JAR to `$CATALINA_BASE/lib/`:

    ~~~
    $ cp lib/tomcat-vault.jar $CATALINA_BASE/lib/
    ~~~

4. Add the following line to `$CATALINA_BASE/conf/catalina.properties` so that Tomcat's Digester uses the tomcat-vault PropertySource implementation:

    ~~~
    org.apache.tomcat.util.digester.PROPERTY_SOURCE=org.apache.tomcat.vault.util.PropertySourceVault
    ~~~

5. Setup your Vault using `./bin/vault.sh`. Here is an example creating a keystore for the Vault and initializing it in `/tmp/vault`:

    ~~~
    # Make a directory for the Vault to live
    $ mkdir /tmp/vault

    # Create a keystore for the Vault
    $ keytool -genseckey -keystore /tmp/vault/vault.keystore -alias my_vault  -storetype jceks -keyalg AES -keysize 128 -storepass my_password123 -keypass my_password123 -validity 730

    # Initialize the Vault and save vault.properties
    $ bin/vault.sh --keystore /tmp/vault/vault.keystore --keystore-password my_password123 --alias my_vault --enc-dir /tmp/vault/ --iteration 44 --salt 1234abcd -g $CATALINA_BASE/conf/vault.properties
    ~~~

    **Note: You can also initialize the Vault in an interactive mode by executing bin/vault.sh with no arguments. If you do this, then you will need to create a file named vault.properties in `$CATALINA_BASE/conf` containing your Vault information as below (all of these keys must be defined and NOT empty). This information is provided by the interactive session at the end of the initialization.**

    ~~~
    KEYSTORE_URL=..
    KEYSTORE_PASSWORD=..
    KEYSTORE_ALIAS=..
    SALT=..
    ITERATION_COUNT=..
    ENC_FILE_DIR=..
    ~~~

7. Start Apache Tomcat!

Using Your New Vault:
---------------------

Now that the Vault has been initialized and Tomcat is loading it, you can start using the vault to store encrypted passwords in your configuration files.

Before you can do this, you will need to add the attributes that you'd like to encrypt to your vault. An example of how to add a secured attribute is listed below:

~~~
# Add a secured attribute to the Vault
$ bin/vault.sh --keystore /tmp/vault/vault.keystore --keystore-password my_password123 --alias my_vault --enc-dir /tmp/vault/ --iteration 120 --salt 1234abcd --vault-block my_block --attribute manager_password --sec-attr P@SSW0#D
~~~

Once the attribute has been added to the Vault, simply replace whatever property value you would like to hide in any Apache Tomcat configuration file with `${attribute_name}`. As an example, let's say that you wanted to use the password that we put into the Vault above (P@SSW0#D) as your tomcat user's password in the manager-gui role. To do that you would change the user in tomcat-users.xml from:

~~~
<user username="tomcat" password="P@SSW0#D" roles="manager-gui"/>
~~~

to:

~~~
<user username="tomcat" password="${VAULT::my_block::manager_password::}" roles="manager-gui"/>
~~~
