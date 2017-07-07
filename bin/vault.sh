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
    [ -n "$JBOSS_HOME" ] &&
        JBOSS_HOME=`cygpath --unix "$JBOSS_HOME"`
    [ -n "$JAVA_HOME" ] &&
        JAVA_HOME=`cygpath --unix "$JAVA_HOME"`
    [ -n "$JAVAC_JAR" ] &&
        JAVAC_JAR=`cygpath --unix "$JAVAC_JAR"`
fi

# Setup JBOSS_HOME
RESOLVED_JBOSS_HOME=`cd "$DIRNAME/.."; pwd`
if [ "x$JBOSS_HOME" = "x" ]; then
    # get the full path (without any relative bits)
    JBOSS_HOME=$RESOLVED_JBOSS_HOME
else
 SANITIZED_JBOSS_HOME=`cd "$JBOSS_HOME/.."; pwd`
 if [ "$RESOLVED_JBOSS" != "$SANITIZED_JBOSS_HOME" ]; then
   echo "WARNING JBOSS_HOME may be pointing to a different installation - unpredictable results may occur."
   echo ""
 fi
fi
export JBOSS_HOME

# Setup the JVM
if [ "x$JAVA" = "x" ]; then
    if [ "x$JAVA_HOME" != "x" ]; then
        JAVA="$JAVA_HOME/bin/java"
    else
        JAVA="java"
    fi
fi

if [ "x$JBOSS_MODULEPATH" = "x" ]; then
    if [ -d "$JBOSS_HOME/share/java/modules" ];then
        # rpm or zip install
        JBOSS_MODULEPATH="$JBOSS_HOME/share/java/modules"
        JBOSS_HOME="$JBOSS_HOME/share/java"
    elif [ -d "$JBOSS_HOME/modules" ];then
        JBOSS_MODULEPATH="$JBOSS_HOME/modules"
    else
        JBOSS_MODULEPATH="/usr/share/java/modules"
        JBOSS_HOME="/usr/share/java"
    fi
fi

###
# Setup the Tomcat Vault Tool classpath
###


# For Cygwin, switch paths to Windows format before running java
if $cygwin; then
    JBOSS_HOME=`cygpath --path --windows "$JBOSS_HOME"`
    JBOSS_MODULEPATH=`cygpath --path --windows "$JBOSS_MODULEPATH"`
fi

# Display our environment
echo "========================================================================="
echo ""
echo "  Tomcat Vault"
echo ""
echo "  VAULT_HOME: $JBOSS_HOME"
echo ""
echo "  JAVA: $JAVA"
echo ""
echo "========================================================================="
echo ""

eval \"$JAVA\" $JAVA_OPTS \
         -cp \"$JBOSS_HOME/modules/system/layers/base/tomcat-vault/main/tomcat-vault.jar\" \
         org.apache.tomcat.vault.VaultTool \
         '"$@"'

