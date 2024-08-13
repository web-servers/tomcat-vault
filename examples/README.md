# just an example to use the vault with a web.xml and context.xml
to use the example:

build the valve and copy the jar in the ${catalina.base}/lib directory.

build the webapp and copy the war in the ${catalina.base}/webapps directory.

the trace output is going in the catalina.out and in the header vault.param

# HOW TO TEST
simply run mvn test

You can also run it by using the java command

java -ea -cp path/to/testclasses:path/to/junit-4.13.2.jar SingleJUnitTestRunner
path.to.test.ClassName#TestName
