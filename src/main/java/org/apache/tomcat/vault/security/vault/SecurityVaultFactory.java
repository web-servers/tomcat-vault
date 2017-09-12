/*
 * JBoss, Home of Professional Open Source.
 * Copyright 2008, Red Hat Middleware LLC, and individual contributors
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

package org.apache.tomcat.vault.security.vault;

import org.apache.juli.logging.Log;
import org.apache.juli.logging.LogFactory;
import org.apache.tomcat.util.res.StringManager;

/**
 * A factory to instantiate a {@link SecurityVault}
 *
 * @author Anil.Saldhana@redhat.com
 * @since Aug 12, 2011
 */
public class SecurityVaultFactory {
    private static final StringManager sm = StringManager.getManager(SecurityVaultFactory.class);
    private static final Log log = LogFactory.getLog(SecurityVaultFactory.class);
    private static final StringManager msm = StringManager.getManager("org.apache.tomcat.vault.security.resources");

    private static String defaultVault = "org.apache.tomcat.vault.security.vault.PicketBoxSecurityVault";
    private static SecurityVault vault = null;

    /**
     * Get an instance of {@link SecurityVault}
     * Remember to initialize the vault by checking {@link SecurityVault#isInitialized()}
     *
     * @return an instance of {@link SecurityVault}
     * @throws SecurityVaultException
     */
    public static SecurityVault get() throws SecurityVaultException {
        return get(defaultVault);
    }

    /**
     * Get an instance of {@link SecurityVault}
     * Remember to initialize the vault by checking {@link SecurityVault#isInitialized()}
     *
     * @param fqn fully qualified name of the vault implementation
     * @return an instance of {@link SecurityVault}
     * @throws SecurityVaultException
     */
    public static SecurityVault get(String fqn) throws SecurityVaultException {
        SecurityManager sm = System.getSecurityManager();
        if (sm != null) {
            sm.checkPermission(new RuntimePermission(SecurityVaultFactory.class.getName() + ".get"));
        }
        if (fqn == null)
            return get();

        if (vault == null) {
            Class<?> vaultClass = SecurityActions.loadClass(SecurityVaultFactory.class, fqn);
            if (vaultClass == null)
                throw new SecurityVaultException(msm.getString("unableToLoadVaultMessage"));
            try {
                vault = (SecurityVault) vaultClass.newInstance();
            } catch (Exception e) {
                throw new SecurityVaultException(msm.getString("unableToCreateVaultMessage"), e);
            }
        } else {
            secondVaultInfo(null, fqn);
        }
        return vault;
    }

    /**
     * Get an instance of {@link SecurityVault}
     * Remember to initialize the vault by checking {@link SecurityVault#isInitialized()}
     *
     * @param classLoader the class loader to use loading the vault
     * @param fqn         fully qualified name of the vault implementation
     * @return an instance of {@link SecurityVault}
     * @throws SecurityVaultException
     */
    public static SecurityVault get(ClassLoader classLoader, String fqn) throws SecurityVaultException {
        if (classLoader == null) {
            throw new IllegalArgumentException(msm.getString("invalidNullArgument", "classLoader"));
        }
        if (fqn == null) {
            throw new IllegalArgumentException(msm.getString("invalidNullArgument", "fqn"));
        }
        SecurityManager sm = System.getSecurityManager();
        if (sm != null) {
            sm.checkPermission(new RuntimePermission(SecurityVaultFactory.class.getName() + ".get"));
        }
        if (vault == null) {
            Class<?> vaultClass = SecurityActions.loadClass(classLoader, fqn);
            if (vaultClass == null)
                throw new SecurityVaultException(msm.getString("unableToLoadVaultMessage"));
            try {
                vault = (SecurityVault) vaultClass.newInstance();
            } catch (Exception e) {
                throw new SecurityVaultException(msm.getString("unableToCreateVaultMessage"), e);
            }
        } else {
            secondVaultInfo(classLoader.toString(), fqn);
        }
        return vault;
    }

    private static void secondVaultInfo(String module, String className) {
        log.warn(sm.getString("securityVaultFactory.attemptToCreateSecondVault", module != null ? className + " @ " + module : className));
    }
}
