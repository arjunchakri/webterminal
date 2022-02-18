package com.sherlock.webterminal.a_testplugin;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class Pre {

	public static void main(String[] args) throws IOException {

		// READING PROPERTY

		Properties webterminalProperties = new Properties();
		String DEFAULT_PLUGIN_PROPERTIES = "webterminalplugin.properties";
		InputStream input = new FileInputStream(DEFAULT_PLUGIN_PROPERTIES);
		webterminalProperties.load(input);

		String propertyUI = webterminalProperties.getProperty("SOMEPROPERTYFROMUI");

		// WRITING VALUES TO A PROPERTY FILE/or any file

		String systemPropertiesPath = "conf/internals/system.properties";

		Properties systemProperties = new Properties();
		systemProperties.put("some.property.key", "some.property.value");

		if (propertyUI != null && !propertyUI.isEmpty()) {
			systemProperties.setProperty("some.optional.property.key", propertyUI);
		}

		File ipFile = new File(systemPropertiesPath);
		ipFile.getParentFile()
				.mkdirs();
		ipFile.createNewFile();
		FileOutputStream inputPropertiesStream = new FileOutputStream(ipFile);
		systemProperties.store(inputPropertiesStream, "sysProps");
		inputPropertiesStream.close();

	}

}
