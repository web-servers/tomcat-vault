package unit.org.apache.tomcat.vault;

import org.apache.commons.cli.*;
import org.apache.tomcat.vault.VaultTool;
import org.apache.tomcat.vault.exception.VaultException;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.lang.reflect.Field;
import static org.junit.Assert.*;

@RunWith(MockitoJUnitRunner.class)
public class VaultToolTest {

    // Helper method to access private fields using reflection
    private Object getPrivateField(Object instance) throws NoSuchFieldException, IllegalAccessException {
        Field field = instance.getClass().getDeclaredField("cmdLine");
        field.setAccessible(true);
        return field.get(instance);
    }


    // Test: Parse valid arguments successfully
    @Test
    public void testValidArguments() throws NoSuchFieldException, IllegalAccessException {
        String[] args = {"-k", "keystore.jks", "-p", "password", "-x", "secure-value"};
        VaultTool vaultTool = new VaultTool(args);

        CommandLine cmdLine = (CommandLine) getPrivateField(vaultTool);

        assertTrue(cmdLine.hasOption("k"));
        assertEquals("keystore.jks", cmdLine.getOptionValue("k"));

        assertTrue(cmdLine.hasOption("p"));
        assertEquals("password", cmdLine.getOptionValue("p"));

        assertTrue(cmdLine.hasOption("x"));
        assertEquals("secure-value", cmdLine.getOptionValue("x"));
    }

    // Test: Missing required argument throws the correct error
    @Test
    public void testMissingArgument() {
        String[] args = {"-k", "keystore.jks", "-p"};
        try {
            new VaultTool(args);
        } catch (VaultException e) {
            Assert.assertEquals(2, e.getExitCode());
        }
    }


    @Test
    public void testInvalidArguments() throws Exception {
        String[] args = {"-z", "invalid-option"};
        try {
            new VaultTool(args);
        } catch (VaultException e) {
            Assert.assertEquals(2, e.getExitCode());
        }
    }


    @Test
    public void testMissingRequiredOptionGroup() throws Exception {
        ByteArrayOutputStream outContent = new ByteArrayOutputStream();

        System.setErr(new PrintStream(outContent));
        String[] args = {"-k", "keystore.jks", "-p", "password"};
        try {
            new VaultTool(args);
        } catch (VaultException e) {
            Assert.assertEquals(2, e.getExitCode());
        }
    }

    // Test: Help option works without errors
    @Test
    public void testHelpOption() throws NoSuchFieldException, IllegalAccessException, VaultException {
        String[] args = {"-h"};
        VaultTool vaultTool = new VaultTool(args);

        CommandLine cmdLine = (CommandLine) getPrivateField(vaultTool);
        assertTrue(cmdLine.hasOption("h"));
    }
}
