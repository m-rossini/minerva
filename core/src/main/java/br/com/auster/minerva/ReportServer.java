/*
 * Copyright (c) 2004-2007 Auster Solutions. All Rights Reserved.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO,
 * THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR
 * PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR
 * CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL,
 * EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO,
 * PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS;
 * OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY,
 * WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE
 * OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE,
 * EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 *
 * Created on 11/04/2007
 */
package br.com.auster.minerva;

import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.security.GeneralSecurityException;

import javax.xml.parsers.ParserConfigurationException;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.OptionBuilder;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.commons.cli.PosixParser;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.w3c.dom.Element;
import org.xml.sax.SAXException;

import br.com.auster.common.io.IOUtils;
import br.com.auster.common.util.I18n;
import br.com.auster.common.xml.DOMUtils;
import br.com.auster.minerva.core.DispatcherThread;
import br.com.auster.minerva.core.ListenerThread;
import br.com.auster.minerva.core.MinervaConfigurationConstants;
import br.com.auster.minerva.core.MinervaManager;
import br.com.auster.minerva.core.ReportInitializationException;

/**
 * TODO What this class is responsible for
 * 
 * @author mtengelm
 * @version $Id$
 * @since 11/04/2007
 */
public class ReportServer {

	private static final I18n	i18n	= I18n.getInstance(ReportServer.class);
	private static final Log	log		= LogFactory.getLog(ReportServer.class);
	private CommandLine				line;

	/**
	 * TODO what this method is responsible for
	 * <p>
	 * Example:
	 * 
	 * <pre>
	 *    Create a use example.
	 * </pre>
	 * 
	 * </p>
	 * 
	 * @param args
	 */
	public long setArguments(String[] args) {
		try {
			CommandLineParser parser = new PosixParser();
			line = parser.parse(createOptions(), args);
		} catch (ParseException pe) {
			HelpFormatter formatter = new HelpFormatter();
			formatter.printHelp(ReportServer.class.getName(), createOptions());
			return 1;
		}
		return 0;
	}

	// ----------------------------
	// Protected methods
	// ----------------------------

	protected Options createOptions() {
		OptionBuilder
				.withArgName(MinervaConfigurationConstants.MINERVA_CMDLINE_OPTS_CONFIGFILE);
		OptionBuilder.hasArg();
		OptionBuilder.isRequired(true);
		OptionBuilder.withDescription(i18n.getString("bootstrap.configuration"));
		Option configurationFile = OptionBuilder
				.create(MinervaConfigurationConstants.MINERVA_CMDLINE_OPTS_CONFIGFILE_MNEMONIC);

		OptionBuilder
				.withArgName(MinervaConfigurationConstants.MINERVA_CMDLINE_OPTS_ENCRYPTED);
		OptionBuilder.hasArg(false);
		OptionBuilder.isRequired(false);
		OptionBuilder.withDescription(i18n.getString("bootstrap.security"));
		Option encrypted = OptionBuilder
				.create(MinervaConfigurationConstants.MINERVA_CMDLINE_OPTS_ENCRYPTED_MNEMONIC);

		Options options = new Options();
		options.addOption(configurationFile);
		options.addOption(encrypted);
		return options;
	}

	public CommandLine getParsedCommandLine() {
		return this.line;
	}

	/**
	 * TODO what this method is responsible for
	 * <p>
	 * Example:
	 * 
	 * <pre>
	 *    Create a use example.
	 * </pre>
	 * 
	 * </p>
	 * 
	 * @throws GeneralSecurityException
	 * @throws IOException
	 * @throws SAXException
	 * @throws ParserConfigurationException
	 * @throws ClassNotFoundException
	 * @throws IllegalAccessException
	 * @throws InstantiationException
	 * @throws IllegalArgumentException
	 * @throws ReportInitializationException
	 * @throws InvocationTargetException
	 * @throws NoSuchMethodException
	 * @throws SecurityException
	 * 
	 */
	public void process(CommandLine _options)
			throws ParserConfigurationException, SAXException, IOException,
			GeneralSecurityException, IllegalArgumentException,
			InstantiationException, IllegalAccessException, ClassNotFoundException,
			ReportInitializationException, SecurityException, NoSuchMethodException,
			InvocationTargetException {
		// opening configuration file
		String configFile = _options
				.getOptionValue(MinervaConfigurationConstants.MINERVA_CMDLINE_OPTS_CONFIGFILE_MNEMONIC);
		boolean encrypted = _options
				.hasOption(MinervaConfigurationConstants.MINERVA_CMDLINE_OPTS_ENCRYPTED_MNEMONIC);
		log.info(i18n.getString("running.configFile", configFile));
		Element root = DOMUtils.openDocument(IOUtils.openFileForRead(configFile,
				encrypted));
		// configuring manager instance
		Element managerConfiguration = DOMUtils.getElement(root,
				MinervaConfigurationConstants.MINERVA_CONFIGURATION_NAMESPACE,
				MinervaConfigurationConstants.MINERVA_MANAGER_CONFIGURATION_ELEMENT,
				true);
		MinervaManager manager = (MinervaManager) Class.forName(
				DOMUtils.getAttribute(managerConfiguration,
						MinervaConfigurationConstants.MINERVA_CLASSNAME_ATTRIBUTE, true))
				.newInstance();
		manager.configure(managerConfiguration);
		// create listeners and dispatchers threads
		Runnable[] listeners = startThreads(
				manager,
				ListenerThread.class,
				DOMUtils
						.getElement(
								root,
								MinervaConfigurationConstants.MINERVA_CONFIGURATION_NAMESPACE,
								MinervaConfigurationConstants.MINERVA_LISTENER_CONFIGURATION_ELEMENT,
								true));
		Runnable[] dispatchers = startThreads(
				manager,
				DispatcherThread.class,
				DOMUtils
						.getElement(
								root,
								MinervaConfigurationConstants.MINERVA_CONFIGURATION_NAMESPACE,
								MinervaConfigurationConstants.MINERVA_DISPATCHER_CONFIGURATION_ELEMENT,
								false));
		// now that all went OK, start them
		for (int i = 0; i < listeners.length; i++) {
			new Thread(listeners[i]).start();
		}
		if (dispatchers != null) {
			for (int i = 0; i < dispatchers.length; i++) {
				new Thread(dispatchers[i]).start();
			}
		}
	}

	private static Runnable[] startThreads(MinervaManager _manager,
			Class _threadKlass, Element _configuration) throws SecurityException,
			NoSuchMethodException, IllegalArgumentException, InstantiationException,
			IllegalAccessException, InvocationTargetException {
		if (_configuration == null) {
			return null;
		}
		String klass = DOMUtils.getAttribute(_configuration,
				MinervaConfigurationConstants.MINERVA_CLASSNAME_ATTRIBUTE, true);
		int count = DOMUtils.getIntAttribute(_configuration,
				MinervaConfigurationConstants.MINERVA_THREADCOUNT_ATTRIBUTE, true);
		Element config = DOMUtils.getElement(_configuration,
				MinervaConfigurationConstants.MINERVA_CONFIGURATION_SUBELEMENT, true);
		// count must always be at least 1
		if (count <= 0) {
			count = 1;
		}
		Runnable[] threads = new Runnable[count];
		for (int i = 0; i < count; i++) {
			Constructor c = _threadKlass.getConstructor(new Class[] {
					MinervaManager.class, String.class, Element.class });
			threads[i] = (Runnable) c.newInstance(new Object[] { _manager, klass,
					config });
		}
		return threads;
	}

}
