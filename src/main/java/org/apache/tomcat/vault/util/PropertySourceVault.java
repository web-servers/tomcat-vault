/*
 * JBoss, Home of Professional Open Source.
 * Copyright 2012, Red Hat, Inc., and individual contributors
 * as indicated by the @author tags. See the copyright.txt file in the
 * distribution for a full listing of individual contributors.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */

package org.apache.tomcat.vault.util;

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.io.File;

import org.apache.tomcat.util.IntrospectionUtils.PropertySource;
import org.apache.tomcat.vault.security.vault.SecurityVault;
import org.apache.tomcat.vault.security.vault.SecurityVaultException;
import org.apache.tomcat.vault.security.vault.SecurityVaultFactory;
import org.apache.tomcat.vault.security.vault.PicketBoxSecurityVault;

import org.apache.juli.logging.Log;
import org.apache.juli.logging.LogFactory;

public class PropertySourceVault implements PropertySource {
    private static final Log log = LogFactory.getLog(PropertySourceVault.class);

    private String PROPERTY_FILE_RELATIVE_PATH = "/conf/vault.properties";

    private SecurityVault vault;
    private PropertyFileManager pfm;
    private Properties properties;

    public PropertySourceVault() {
        this.vault = null;
        this.properties = null;

        String catalinaHome = System.getProperty("catalina.home");
        String catalinaBase = System.getProperty("catalina.base");
        String catalina = null;

        if (new File(catalinaBase + PROPERTY_FILE_RELATIVE_PATH).exists()) {
            catalina = catalinaBase;
            log.debug("vault.properties found in catalina.base [" + catalina + "]");
        } else if (new File(catalinaHome + PROPERTY_FILE_RELATIVE_PATH).exists()) {
            // Using catalina.home is kept for backwards compat
            catalina = catalinaHome;
            log.debug("vault.properties found in catalina.home [" + catalina + "]");
        } else {
            // Always default to catalina.base if it doesn't exist in either place
            catalina = catalinaBase;
            log.debug("vault.properties not found, using catalina.base [" + catalina + "]");
        }

        this.pfm = new PropertyFileManager(catalina + PROPERTY_FILE_RELATIVE_PATH);

        this.init();
    }

    public void init() {
        try {
            vault = SecurityVaultFactory.get();

            // Load vault property file
            properties = pfm.load();

            // If properties is null then there was an exception
            if (properties == null) {
                return;
            }

            Map<String, Object> options = new HashMap<String, Object>();
            options.put(PicketBoxSecurityVault.KEYSTORE_URL, properties.getProperty("KEYSTORE_URL"));
            options.put(PicketBoxSecurityVault.KEYSTORE_PASSWORD, properties.getProperty("KEYSTORE_PASSWORD"));
            options.put(PicketBoxSecurityVault.KEYSTORE_ALIAS, properties.getProperty("KEYSTORE_ALIAS"));
            options.put(PicketBoxSecurityVault.SALT, properties.getProperty("SALT"));
            options.put(PicketBoxSecurityVault.ITERATION_COUNT, properties.getProperty("ITERATION_COUNT"));
            options.put(PicketBoxSecurityVault.ENC_FILE_DIR, properties.getProperty("ENC_FILE_DIR"));

            vault.init(options);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
    }

    @Override
    public String getProperty(String arg0) {
        String result = null;

        // If the vault failed to init, then return without change
        if (!vault.isInitialized()) {
            return arg0;
        }

        if (arg0.startsWith("VAULT::")) {
            String vaultdata[] = arg0.split("::");
            if (vaultdata.length == 3) {
                if (vault.isInitialized()) {
                    try {
                        result = new String(vault.retrieve(vaultdata[1], vaultdata[2], null));
                    } catch (SecurityVaultException e) {
                        log.error(e.getMessage(), e);
                    }
                }
            }
        }
        return result;
    }
}
