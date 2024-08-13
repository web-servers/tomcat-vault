package unit.org.apache.tomcat.vault.util;

import org.apache.tomcat.vault.security.vault.PicketBoxSecurityVault;
import org.apache.tomcat.vault.security.vault.SecurityVaultException;
import org.apache.tomcat.vault.security.vault.SecurityVaultFactory;
import org.apache.tomcat.vault.util.PropertyFileManager;
import org.apache.tomcat.vault.util.PropertySourceVault;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.*;
import org.mockito.junit.MockitoJUnitRunner;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import java.util.Properties;


@RunWith(MockitoJUnitRunner.class)
public class PropertySourceVaultTest {

    @InjectMocks
    private PropertySourceVault propertySourceVault;

    @Mock
    private PicketBoxSecurityVault mockVault;


    @Mock
    private PropertyFileManager mockPfm;


    @Before
    public void setUp() {when(mockVault.isInitialized()).thenReturn(true);}

    private Properties mockProperties() {
        Properties properties = new Properties();
        properties.setProperty("KEYSTORE_URL", "KEYSTORE_URL");
        properties.setProperty("KEYSTORE_PASSWORD", "MASK-KEYSTORE_PASSWORD");
        properties.setProperty("KEYSTORE_ALIAS", "KEYSTORE_ALIAS");
        properties.setProperty("SALT", "SALT");
        properties.setProperty("ITERATION_COUNT", "10");
        properties.setProperty("ENC_FILE_DIR", "ENC_FILE_DIR");

        return properties;

    }

    @Test
    public void testInit() {


        try (MockedStatic<SecurityVaultFactory> mock = mockStatic(SecurityVaultFactory.class)) {

            when(mockPfm.load()).thenReturn(mockProperties());
            mock.when(SecurityVaultFactory::get).thenReturn(Mockito.mock(PicketBoxSecurityVault.class));

            propertySourceVault.init();
            assertNotNull(propertySourceVault);

        }
    }

    @Test
    public void testGetPropertyFromVault() throws SecurityVaultException {
        // Test vault retrieval
        when(mockVault.retrieve(anyString(), anyString(), any())).thenReturn("mockValue".toCharArray());
        when(mockVault.isInitialized()).thenReturn(true);
        String result = propertySourceVault.getProperty("VAULT::category::key");
        assertEquals("mockValue", result);
    }

    @Test
    public void testGetPropertyFromVaultWithWrongPrefix_shouldReturnNull(){
        when(mockVault.isInitialized()).thenReturn(true);
        String result = propertySourceVault.getProperty("WRONG::PREFIX::key");
        assertNull(result);
    }

}