package com.mw.logging.activator;

import com.liferay.petra.log4j.Log4JUtil;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;


public class LoggingActivator implements BundleActivator {

	@Override
	public void start(BundleContext context) throws Exception {
		
		String loggerName = "com.liferay.clam.antivirus.custom.CustomClamAntivirusScannerImpl";
		String priority = "ALL";
		
		String existingLevel;
		try {
			existingLevel = Log4JUtil.getOriginalLevel(loggerName);
			
			_log.info(loggerName + ", existing current: " + existingLevel);
		} catch (NullPointerException npe) {
			_log.info(loggerName + ", NPE...");
		}

		Log4JUtil.setLevel(loggerName, priority, true);
		
		String newLevel = Log4JUtil.getOriginalLevel(loggerName);
		
		_log.info(loggerName + ", new level: " + newLevel);
	}

	@Override
	public void stop(BundleContext context) throws Exception {
		
		
	}

	private static final Log _log = LogFactoryUtil.getLog(
			LoggingActivator.class);
}