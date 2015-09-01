package org.apache.tomcat.vault.security;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.security.GeneralSecurityException;
import java.security.KeyException;
import java.security.ProviderException;
import java.util.List;

import javax.management.AttributeNotFoundException;
import javax.naming.NamingException;
import javax.security.auth.callback.Callback;
import javax.security.auth.callback.UnsupportedCallbackException;
import javax.security.auth.login.FailedLoginException;
import javax.security.auth.login.LoginException;
import javax.xml.stream.Location;
import javax.xml.stream.XMLStreamException;

import org.jboss.logging.Cause;
import org.jboss.logging.Message;
import org.jboss.logging.MessageBundle;
import org.jboss.logging.Messages;
import org.jboss.logging.Param;

@MessageBundle(projectCode = "PBOX")
public interface PicketBoxMessages {

    PicketBoxMessages MESSAGES = Messages.getBundle(PicketBoxMessages.class);

    @Message(id = 4, value = "Argument %s cannot be null")
    IllegalArgumentException invalidNullArgument(String argumentName);

    @Message(id = 6, value = "Unable to load vault class")
    String unableToLoadVaultMessage();

    @Message(id = 7, value = "Unable to instantiate vault class")
    String unableToCreateVaultMessage();

    @Message(id = 105, value = "Ecrypt a password using the JaasSecurityDomain password\n\n"
            + "Usage: PBEUtils salt count domain-password password\n"
            + "  salt : the Salt attribute from the JaasSecurityDomain\n"
            + "  count : the IterationCount attribute from the JaasSecurityDomain\n"
            + "  domain-password : the plaintext password that maps to the KeyStorePass attribute from the JaasSecurityDomain\n"
            + "  password : the plaintext password that should be encrypted with the JaasSecurityDomain password\n")
    String pbeUtilsMessage();

    @Message(id = 112, value = "Invalid Base64 string: %s")
    IllegalArgumentException invalidBase64String(String base64Str);

    @Message(id = 118, value = "Invalid password command type: %s")
    IllegalArgumentException invalidPasswordCommandType(String type);

    @Message(id = 120, value = "Options map %s is null or empty")
    IllegalArgumentException invalidNullOrEmptyOptionMap(String mapName);

    @Message(id = 121, value = "Option %s is null or empty")
    String invalidNullOrEmptyOptionMessage(String optionName);

    @Message(id = 123, value = "File or directory %s does not exist")
    String fileOrDirectoryDoesNotExistMessage(String fileName);

    @Message(id = 128, value = "Unable to encrypt data")
    String unableToEncryptDataMessage();

    @Message(id = 130, value = "Unable to write vault data file (%s)")
    String unableToWriteVaultDataFileMessage(String fileName);

    @Message(id = 132, value = "The specified system property %s is missing")
    IllegalArgumentException missingSystemProperty(String sysProperty);

    @Message(id = 133, value = "Failed to match %s and %s")
    RuntimeException failedToMatchStrings(String one, String two);

    @Message(id = 134, value = "Unrecognized security vault content version (%s), expecting (from %s to %s)")
    RuntimeException unrecognizedVaultContentVersion(String readVersion, String fromVersion, String toVersion);

    @Message(id = 135, value = "Security Vault contains both covnerted (%s) and pre-conversion data (%s), failed to load vault")
    RuntimeException mixedVaultDataFound(String vaultDatFile, String encDatFile);

    @Message(id = 136, value = "Security Vault conversion unsuccessful missing admin key in original vault data")
    RuntimeException missingAdminKeyInOriginalVaultData();

    @Message(id = 137, value = "Security Vault does not contain SecretKey entry under alias (%s)")
    RuntimeException vaultDoesnotContainSecretKey(String alias);
    
    @Message(id = 138, value = "There is no SecretKey under the alias (%s) and the alias is already used to denote diffrent crypto object in the keystore.")
    RuntimeException noSecretKeyandAliasAlreadyUsed(String alias);

    @Message(id = 139, value = "Unable to store keystore to file (%s)")
    RuntimeException unableToStoreKeyStoreToFile(@Cause Throwable throwable, String file);

    @Message(id = 140, value = "Unable to get keystore (%s)")
    RuntimeException unableToGetKeyStore(@Cause Throwable throwable, String file);

    @Message(id = 142, value = "Keystore password should be either masked or prefixed with one of {EXT}, {EXTC}, {CMD}, {CMDC}, {CLASS}")
    String invalidKeystorePasswordFormatMessage();

    @Message(id = 143, value = "Unable to load password class (%s). Try to specify module to load class from using '{CLASS@module}class_name'")
    RuntimeException unableToLoadPasswordClass(@Cause Throwable t, String classToLoad);

    @Message(id = 144, value = "Trying to load null or empty class")
    RuntimeException loadingNullorEmptyClass();


}
