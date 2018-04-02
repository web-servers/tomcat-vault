#!/bin/sh

DIRNAME=`dirname "$0"`
PROGNAME=`basename "$0"`
GREP="grep"

# Use the maximum available, or set MAX_FD != -1 to use that
MAX_FD="maximum"

#
# Helper to complain.
#
warn() {
    echo "${PROGNAME}: $*"
}

#
# Helper to puke.
#
die() {
    warn $*
    exit 1
}

# OS specific support (must be 'true' or 'false').
cygwin=false;
darwin=false;
linux=false;
case "`uname`" in
    CYGWIN*)
        cygwin=true
        ;;

    Darwin*)
        darwin=true
        ;;

    Linux)
        linux=true
        ;;
esac

# For Cygwin, ensure paths are in UNIX format before anything is touched
if $cygwin ; then
    [ -n "$VAULT_HOME" ] &&
        VAULT_HOME=`cygpath --unix "$VAULT_HOME"`
    [ -n "$JAVA_HOME" ] &&
        JAVA_HOME=`cygpath --unix "$JAVA_HOME"`
    [ -n "$JAVAC_JAR" ] &&
        JAVAC_JAR=`cygpath --unix "$JAVAC_JAR"`
fi

# Setup VAULT_HOME
VAULT_HOME=`cd "$DIRNAME/.."; pwd`
export VAULT_HOME

# Setup the JVM
if [ "x$JAVA" = "x" ]; then
    if [ "x$JAVA_HOME" != "x" ]; then
        JAVA="$JAVA_HOME/bin/java"
    else
        JAVA="java"
    fi
fi

# Setup the classpath for vault that contains the tomcat jars
if [ "x$VAULT_CLASSPATH" = "x" ]; then
    if [ -d "$VAULT_HOME/share/java" ];then
        # rpm or zip install
        VAULT_HOME="$VAULT_HOME/share/java"
        VAULT_CLASSPATH="$VAULT_HOME/tomcat/tomcat-util.jar:$VAULT_HOME/../tomcat/bin/tomcat-juli.jar"
    elif [ -d "$VAULT_HOME/lib" ];then
        VAULT_CLASSPATH="$VAULT_HOME/lib/tomcat-util.jar:$VAULT_HOME/bin/tomcat-juli.jar"
	    VAULT_HOME="$VAULT_HOME/lib"
    else
        VAULT_HOME="/usr/share/java"
        VAULT_CLASSPATH="/usr/share/java/tomcat/tomcat-util.jar:/usr/share/tomcat/bin/tomcat-juli.jar"
    fi
fi

###
# Setup the Tomcat Vault Tool classpath
###

# For Cygwin, switch paths to Windows format before running java
if $cygwin; then
    VAULT_HOME=`cygpath --path --windows "$VAULT_HOME"`
    VAULT_CLASSPATH=`cygpath --path --windows "$VAULT_CLASSPATH"`
fi

# Display our environment
if [[ "$@" != "-h" && "$@" != "--help" ]]; then
echo "========================================================================="
echo ""
echo "  Tomcat Vault"
echo ""
echo "  VAULT_HOME: $VAULT_HOME"
echo ""
echo "  JAVA: $JAVA"
echo ""
echo "========================================================================="
echo ""
fi

eval \"$JAVA\" $JAVA_OPTS \
         -cp \"$VAULT_HOME/tomcat-vault.jar:$VAULT_CLASSPATH\" \
         org.apache.tomcat.vault.VaultTool \
         '"$@"'

