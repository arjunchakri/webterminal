
1)

To use a custom JDK,
- Create a file - javahome.json
- Configure the java home in the file, in one line.
- Make sure to the jdk path before "bin" directory (similar to how you would set JAVA_HOME system property).
- Use this option if you dont have jdk set in system path (eg, if javac command doesnt work)

2)

If you are facing issues with special characters while creating an action,
- Create a file - commands-translator.properties
- Set the property key value pair as key -> original "command" term, value -> value to be replaced
- By default, there are a few translations in the classpath under "javaconfig/commands-translator.properties"
