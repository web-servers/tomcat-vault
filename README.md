# Vault for Apache Tomcat
Tomcat-vault is a PicketLink vault extension for Apache Tomcat. It allows you to place sensitive information, such as passwords, inside of a vault instead of the Tomcat configuration files.

# Installation
See the [INSTALL](./INSTALL.md) file for instructions on installation and usage.

# How it works
At start up, the Tomcat digester module parses configuration files and references the vault keystore when a ${parameter} is found within a Tomcat configuration file. If the ${parameter} is found within the vault, then the ${parameter} is replaced with the value of the corresponding attribute.

# Links
Tomcat System Properties :
https://tomcat.apache.org/tomcat-9.0-doc/config/systemprops.html
