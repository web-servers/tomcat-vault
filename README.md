# Tomcat-vault
Tomcat-vault is a PicketLink vault extension for Apache Tomcat. It allows you to "hide" paswords from Tomcat configuration files.

# How to use it ?
Refer to the INSTALL file for installation and usage instructions.

# How does it work ?
At start up, the Tomcat digester module parses configuration files and triggers a PropertySource implementation when finding a ${parameter}.

The PropertySource implementation then replaces the ${parameter} with the value of the corresponding attribut in the vault keystore.

# Links
Tomcat System Properties :
https://tomcat.apache.org/tomcat-8.0-doc/config/systemprops.html
