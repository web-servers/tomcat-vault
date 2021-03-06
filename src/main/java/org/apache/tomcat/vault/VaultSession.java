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

package org.apache.tomcat.vault;

import org.apache.tomcat.vault.security.plugins.PBEUtils;
import org.apache.tomcat.vault.security.vault.PicketBoxSecurityVault;
import org.apache.tomcat.vault.security.vault.SecurityVault;
import org.apache.tomcat.vault.security.vault.SecurityVaultException;
import org.apache.tomcat.vault.security.vault.SecurityVaultFactory;

import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.PBEParameterSpec;
import java.io.File;
import java.io.PrintStream;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.Map;

import org.jasypt.util.text.BasicTextEncryptor;

/**
 * Non-interactive session for {@link VaultTool}
 *
 * @author Peter Skopek
 */
public final class VaultSession {

    public static final String VAULT_ENC_ALGORITHM = "PBEwithMD5andDES";

    static final Charset CHARSET = Charset.forName("UTF-8");

    private String keystoreURL;
    private String keystorePassword;
    private String keystoreMaskedPassword;
    private String encryptionDirectory;
    private String salt;
    private int iterationCount;

    private SecurityVault vault;
    private String vaultAlias;
    private byte[] handshakeKey;

    /**
     * Constructor to create VaultSession.
     *
     * @param keystoreURL
     * @param keystorePassword
     * @param encryptionDirectory
     * @param salt
     * @param iterationCount
     * @throws Exception
     */
    public VaultSession(String keystoreURL, String keystorePassword, String encryptionDirectory, String salt, int iterationCount)
            throws Exception {
        this.keystoreURL = keystoreURL;
        this.keystorePassword = keystorePassword;
        this.encryptionDirectory = encryptionDirectory;
        this.salt = salt;
        this.iterationCount = iterationCount;
        validate();
    }

    /**
     * Validate fields sent to this class's constructor.
     */
    private void validate() throws Exception {
        validateKeystoreURL();
        validateEncryptionDirectory();
        validateSalt();
        validateIterationCount();
        validateKeystorePassword();
    }

    protected void validateKeystoreURL() throws Exception {

        File f = new File(keystoreURL);
        if (!f.exists()) {
            throw new Exception("Keystore [" + keystoreURL + "] doesn't exist."
                    + "\nkeystore could be created: keytool -genseckey -alias vault -storetype jceks -keyalg AES -keysize 128 -keystore "
                    + keystoreURL);
        } else if (!f.canWrite() || !f.isFile()) {
            throw new Exception("Keystore [" + keystoreURL + "] is not writable or not a file.");
        }
    }

    protected void validateKeystorePassword() throws Exception {
        if (keystorePassword == null) {
            throw new Exception("Keystore password has to be specified.");
        }
    }

    protected void validateEncryptionDirectory() throws Exception {
        if (encryptionDirectory == null) {
            throw new Exception("Encryption directory has to be specified.");
        }
        if (!encryptionDirectory.endsWith("/") || encryptionDirectory.endsWith("\\")) {
            encryptionDirectory = encryptionDirectory + (System.getProperty("file.separator", "/"));
        }
        File d = new File(encryptionDirectory);
        if (!d.exists()) {
            if (!d.mkdirs()) {
                throw new Exception("Cannot create encryption directory " + d.getAbsolutePath());
            }
        }
        if (!d.isDirectory()) {
            throw new Exception("Encryption directory is not a directory or doesn't exist. (" + encryptionDirectory + ")");
        }
    }

    protected void validateIterationCount() throws Exception {
        if (iterationCount < 1 && iterationCount > Integer.MAX_VALUE) {
            throw new Exception("Iteration count has to be withing 1 - " + Integer.MAX_VALUE + ", but is " + iterationCount
                    + ".");
        }
    }

    protected void validateSalt() throws Exception {
        if (salt == null || salt.length() != 8) {
            throw new Exception("Salt has to be exactly 8 characters long.");
        }
    }

    /**
     * Method to compute masked password based on class attributes.
     *
     * @return masked password prefixed with {link @PicketBoxSecurityVault.PASS_MASK_PREFIX}.
     * @throws Exception
     */
    private String computeMaskedPassword() throws Exception {

        // Create the PBE secret key
        SecretKeyFactory factory = SecretKeyFactory.getInstance(VAULT_ENC_ALGORITHM);

        char[] password = "somearbitrarycrazystringthatdoesnotmatter".toCharArray();
        PBEParameterSpec cipherSpec = new PBEParameterSpec(salt.getBytes(), iterationCount);
        PBEKeySpec keySpec = new PBEKeySpec(password);
        SecretKey cipherKey = factory.generateSecret(keySpec);

        String maskedPass = PBEUtils.encode64(keystorePassword.getBytes(), VAULT_ENC_ALGORITHM, cipherKey, cipherSpec);

        return PicketBoxSecurityVault.PASS_MASK_PREFIX + maskedPass;
    }

    /**
     * Initialize the underlying vault.
     *
     * @throws Exception
     */
    private void initSecurityVault() throws Exception {
        try {
            this.vault = SecurityVaultFactory.get();
            this.vault.init(getVaultOptionsMap());
            handshake();
        } catch (SecurityVaultException e) {
            throw new Exception("Exception encountered: " + e.getLocalizedMessage(), e);
        }
    }

    /**
     * Start the vault with given alias.
     *
     * @param vaultAlias
     * @throws Exception
     */
    public void startVaultSession(String vaultAlias) throws Exception {
        if (vaultAlias == null) {
            throw new Exception("Vault alias has to be specified.");
        }
        this.keystoreMaskedPassword = computeMaskedPassword();
        this.vaultAlias = vaultAlias;
        initSecurityVault();
    }

    private Map<String, Object> getVaultOptionsMap() {
        Map<String, Object> options = new HashMap<String, Object>();
        options.put(PicketBoxSecurityVault.KEYSTORE_URL, keystoreURL);
        options.put(PicketBoxSecurityVault.KEYSTORE_PASSWORD, keystoreMaskedPassword);
        options.put(PicketBoxSecurityVault.KEYSTORE_ALIAS, vaultAlias);
        options.put(PicketBoxSecurityVault.SALT, salt);
        options.put(PicketBoxSecurityVault.ITERATION_COUNT, Integer.toString(iterationCount));
        options.put(PicketBoxSecurityVault.ENC_FILE_DIR, encryptionDirectory);
        return options;
    }

    private void handshake() throws SecurityVaultException {
        Map<String, Object> handshakeOptions = new HashMap<String, Object>();
        handshakeOptions.put(PicketBoxSecurityVault.PUBLIC_CERT, vaultAlias);
        handshakeKey = vault.handshake(handshakeOptions);
    }

    /**
     * Add secured attribute to specified vault block. This method can be called only after successful
     * startVaultSession() call.
     *
     * @param vaultBlock
     * @param attributeName
     * @param attributeValue
     */
    public void addSecuredAttribute(String vaultBlock, String attributeName, char[] attributeValue) throws Exception {
        if (handshakeKey == null) {
            throw new Exception("addSecuredAttribute method has to be called after successful startVaultSession() call.");
        }
        vault.store(vaultBlock, attributeName, attributeValue, handshakeKey);
        attributeCreatedDisplay(vaultBlock, attributeName);
    }

    /**
     * Check whether secured attribute is already set for given vault block and attribute name. This method can be called only after
     * successful startVaultSession() call.
     *
     * @param vaultBlock
     * @param attributeName
     * @return true is password already exists for given vault block and attribute name.
     * @throws Exception
     */
    public boolean checkSecuredAttribute(String vaultBlock, String attributeName) throws Exception {
        if (handshakeKey == null) {
            throw new Exception("checkSecuredAttribute method has to be called after successful startVaultSession() call.");
        }
        return vault.exists(vaultBlock, attributeName);
    }

    /**
     * Remove secured attribute with given vault block and attribute name. This method can be called only after
     * successful startVaultSession() call.
     *
     * @param vaultBlock
     * @param attributeName
     * @throws Exception
     */
    public void removeSecuredAttribute(String vaultBlock, String attributeName) throws Exception {
        if (handshakeKey == null) {
            throw new Exception("checkSecuredAttribute method has to be called after successful startVaultSession() call.");
        }
        vault.remove(vaultBlock, attributeName, handshakeKey);
    }

    /**
     * Encrypt a value using the CRYPT feature.
     *
     * @param encryptionPassword Encryption password; could be stored in the vault
     * @param valueToEncrypt
     *
     * @throws Exception if there is an issue retrieving the encryptionPassword from the vault.
     */
    public void encryptValueWithCRYPT(String encryptionPassword, String valueToEncrypt) throws Exception {
        String decryptedPassword = null;

        // if the encryptionPassword is in the VAULT, decrypt it
        // This logic came from o.a.t.vault.util.PropertySourceVault, may need to move to a helper
        // class to reduce duplication.
        if (encryptionPassword.startsWith("VAULT::")) {
            String vaultdata[] = encryptionPassword.split("::");
            if (vaultdata.length == 3) {
                if (vault.isInitialized()) {
                    try {
                        decryptedPassword = new String(vault.retrieve(vaultdata[1], vaultdata[2], null));
                    } catch (SecurityVaultException e) {
                        System.out.println(e.getMessage());
                    }
                }
            }
        } else {
            decryptedPassword = encryptionPassword;
        }

        BasicTextEncryptor textEncryptor = new BasicTextEncryptor();
        textEncryptor.setPassword(decryptedPassword);
        System.out.println("Encrypted value: CRYPT::" + textEncryptor.encrypt(valueToEncrypt));
    }

    /**
     * Display info about stored secured attribute.
     *
     * @param vaultBlock
     * @param attributeName
     */
    private void attributeCreatedDisplay(String vaultBlock, String attributeName) {
        String keyAsString = new String(handshakeKey, CHARSET);
        System.out.println("Secured attribute value has been stored in vault. ");
        System.out.println("Please make note of the following:");
        System.out.println("********************************************");
        System.out.println("Vault Block:" + vaultBlock);
        System.out.println("Attribute Name:" + attributeName);
        System.out.println("Shared Key:" + keyAsString);
        System.out.println("Configuration should be done as follows:");
        System.out.println("VAULT::" + vaultBlock + "::" + attributeName + "::" + keyAsString);
        System.out.println("********************************************");
    }

    /**
     * Display info about vault itself in form of AS7 configuration file.
     */
    public void vaultConfigurationDisplay() {
        System.out.println("Vault Configuration in tomcat properties file:");
        System.out.println("********************************************");
        outputConfig(System.out);
        System.out.println("********************************************");
    }

    /**
     * Print AS7 configuration file to stream.
     *
     * @param out stream to print config.
     */
    public void outputConfig(PrintStream out) {
        out.println("KEYSTORE_URL=" + keystoreURL.replace("\\", "/"));
        out.println("KEYSTORE_PASSWORD=" + keystoreMaskedPassword);
        out.println("KEYSTORE_ALIAS=" + vaultAlias);
        out.println("SALT=" + salt);
        out.println("ITERATION_COUNT=" + iterationCount);
        out.println("ENC_FILE_DIR=" + encryptionDirectory.replace("\\", "/"));
    }

}
