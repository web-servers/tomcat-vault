package org.apache.tomcat.util;
import java.util.HashMap;
import java.util.Map;

import org.apache.tomcat.util.IntrospectionUtils.PropertySource;
import org.jboss.security.vault.SecurityVault;
import org.jboss.security.vault.SecurityVaultException;
import org.jboss.security.vault.SecurityVaultFactory;
import org.picketbox.plugins.vault.PicketBoxSecurityVault;
public class PropertySourceVault implements PropertySource {

    private static SecurityVault vault;
    
    static {
    	try {
			vault = SecurityVaultFactory.get();
		} catch (SecurityVaultException e2) {
			// TODO Auto-generated catch block
			e2.printStackTrace();
		}
        Map<String,Object> options = new HashMap<String, Object>();
        options.put(PicketBoxSecurityVault.KEYSTORE_URL, "/home/jfclere/vault.jks");
        try {
			options.put(PicketBoxSecurityVault.KEYSTORE_PASSWORD, "MASK-3Q3gV8xwFBNtC81uTmBtFa");
		} catch (Exception e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
        options.put(PicketBoxSecurityVault.KEYSTORE_ALIAS, "vault");
        options.put(PicketBoxSecurityVault.SALT, "12345678");
        options.put(PicketBoxSecurityVault.ITERATION_COUNT, "44");
        options.put(PicketBoxSecurityVault.ENC_FILE_DIR, "/home/jfclere/vault_data/");
        try {
			vault.init(options);
		} catch (SecurityVaultException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }

	public String getProperty(String arg0) {
		String result = null;
		if (vault.isInitialized()) {
                   try {
                	System.out.println("key: " + arg0);
					result = new String(vault.retrieve("tomcat8", arg0, null));
					System.out.println("key: " + arg0 + " : value: " + result);
				} catch (SecurityVaultException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
                }
		return result;
	}
} 
