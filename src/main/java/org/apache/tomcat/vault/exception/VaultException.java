package org.apache.tomcat.vault.exception;

public class VaultException extends RuntimeException {
    int exitCode = -1;
    public VaultException(String message) {
        super(message);
    }

    public VaultException(String message, int exitCode) {
        super(message);
        this.exitCode = exitCode;
    }

    public int getExitCode() {
        return exitCode;
    }
}
