package org.apache.tomcat.vault.util;

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import org.apache.tomcat.util.IntrospectionUtils.PropertySource;
import org.apache.tomcat.vault.security.vault.SecurityVault;
import org.apache.tomcat.vault.security.vault.SecurityVaultException;
import org.apache.tomcat.vault.security.vault.SecurityVaultFactory;
import org.apache.tomcat.vault.security.vault.PicketBoxSecurityVault;

public class PropertySourceVault implements PropertySource {
    private String PROPERTY_FILE_RELATIVE_PATH = "/conf/vault.properties";

    private SecurityVault vault;
    private PropertyFileManager pfm;
    private Properties properties;

    public PropertySourceVault() {
        vault = null;
        properties = null;
        String catalinaHome = System.getProperty("catalina.home");
        if (catalinaHome == null)
           catalinaHome = System.getProperty("catalina.base");
        if (catalinaHome == null) {
           // Here probably need to guess the location...
           catalinaHome = ".";
        }
        pfm = new PropertyFileManager(catalinaHome + PROPERTY_FILE_RELATIVE_PATH);

        this.init();
    }

    public void init() {
        try {
            vault = SecurityVaultFactory.get();

            // Load vault property file
            properties = pfm.load();

            Map<String, Object> options = new HashMap<String, Object>();
            options.put(PicketBoxSecurityVault.KEYSTORE_URL, properties.getProperty("KEYSTORE_URL"));
            options.put(PicketBoxSecurityVault.KEYSTORE_PASSWORD, properties.getProperty("KEYSTORE_PASSWORD"));
            options.put(PicketBoxSecurityVault.KEYSTORE_ALIAS, properties.getProperty("KEYSTORE_ALIAS"));
            options.put(PicketBoxSecurityVault.SALT, properties.getProperty("SALT"));
            options.put(PicketBoxSecurityVault.ITERATION_COUNT, properties.getProperty("ITERATION_COUNT"));
            options.put(PicketBoxSecurityVault.ENC_FILE_DIR, properties.getProperty("ENC_FILE_DIR"));

            vault.init(options);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public String getProperty(String arg0) {
        String result = null;

        if (vault.isInitialized()) {
            try {
                System.out.println("key: " + arg0);
                result = new String(vault.retrieve(properties.getProperty("VAULT_BLOCK"), arg0, null));
                System.out.println("key: " + arg0 + " : value: " + result);
            } catch (SecurityVaultException e) {
                e.printStackTrace();
            }
        }
        return result;
    }
}
