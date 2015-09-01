package org.apache.tomcat.vault.security;

import org.jboss.logging.*;

import javax.security.auth.Subject;
import java.net.URL;
import java.security.*;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

@MessageLogger(projectCode = "PBOX")
public interface PicketBoxLogger extends BasicLogger {

    PicketBoxLogger LOGGER = Logger.getMessageLogger(PicketBoxLogger.class,
            PicketBoxLogger.class.getPackage().getName());

    

    @LogMessage(level = Logger.Level.TRACE)
    @Message(id = 355, value = "Begin execPasswordCmd, command: %s")
    void traceBeginExecPasswordCmd(String passwordCmd);

    @LogMessage(level = Logger.Level.TRACE)
    @Message(id = 356, value = "End execPasswordCmd, exit code: %s")
    void traceEndExecPasswordCmd(int exitCode);

    @LogMessage(level = Logger.Level.INFO)
    @Message(id = 361, value = "Default Security Vault Implementation Initialized and Ready")
    void infoVaultInitialized();
    
    @LogMessage(level = Logger.Level.ERROR)
    @Message(id = 362, value = "Cannot get MD5 algorithm instance for hashing password commands. Using NULL.")
    void errorCannotGetMD5AlgorithmInstance();

    @LogMessage(level = Logger.Level.TRACE)
    @Message(id = 363, value = "Retrieving password from the cache for key: %s")
    void traceRetrievingPasswordFromCache(String newKey);

    @LogMessage(level = Logger.Level.TRACE)
    @Message(id = 364, value = "Storing password to the cache for key: %s")
    void traceStoringPasswordToCache(String newKey);

    @LogMessage(level = Logger.Level.TRACE)
    @Message(id = 365, value = "Resetting cache")
    void traceResettingCache();

    @LogMessage(level = Logger.Level.ERROR)
    @Message(id = 366, value = "Error parsing time out number.")
    void errorParsingTimeoutNumber();

    @LogMessage(level = Logger.Level.DEBUG)
    @Message(id = 367, value = "Reading security vault data version %s target version is %s")
    void securityVaultContentVersion(String dataVersion, String targetVersion);

    @LogMessage(level = Logger.Level.ERROR)
    @Message(id = 368, value = "Security Vault contains both covnerted (%s) and pre-conversion data (%s). Try to delete %s file and start over again.")
    void mixedVaultDataFound(String vaultDatFile, String encDatFile, String encDatFile2);

    @LogMessage(level = Logger.Level.INFO)
    @Message(id = 369, value = "Ambiguos vault block and attribute name stored in original security vault. Delimiter (%s) is part of vault block or attribute name. Took the first delimiter. Result vault block (%s) attribute name (%s). Modify security vault manually.")
    void ambiguosKeyForSecurityVaultTransformation(String delimiter, String vaultBlock, String attributeName);

    @LogMessage(level = Logger.Level.WARN)
    @Message(id = 370, value = "Cannot delete original security vault file (%s). Delete the file manually before next start, please.")
    void cannotDeleteOriginalVaultFile(String file);

    @LogMessage(level = Logger.Level.INFO)
    @Message(id = 371, value = "Security Vault does not contain SecretKey entry under alias (%s)")
    void vaultDoesnotContainSecretKey(String alias);

    @LogMessage(level = Logger.Level.INFO)
    @Message(id = 372, value = "Security Vault key store successfuly converted to JCEKS type (%s). From now on use JCEKS as KEYSTORE_TYPE in Security Vault configuration.")
    void keyStoreConvertedToJCEKS(String keyStoreFile);

    @LogMessage(level = Logger.Level.WARN)
    @Message(id = 376, value = "Wrong Base64 string used with masked password utility. Following is correct (%s)")
    void wrongBase64StringUsed(String fixedBase64);


    @LogMessage(level = Logger.Level.WARN)
    @Message(id = 378, value = "Attempt to create the second Security Vault [%s] is invalid. Only one Security Vault is supported. Change your configuration, please.")
    void attemptToCreateSecondVault(String code);
}
