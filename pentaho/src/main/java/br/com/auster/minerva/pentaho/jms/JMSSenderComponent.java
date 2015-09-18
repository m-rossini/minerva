/*
 * Copyright (c) 2004 Auster Solutions. All Rights Reserved.
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
 * Created on 19/12/2005
 */
package br.com.auster.minerva.pentaho.jms;

import java.io.IOException;
import java.io.Serializable;
import java.util.List;
import java.util.Properties;
import java.util.Set;

import javax.jms.JMSException;
import javax.jms.ObjectMessage;
import javax.jms.Queue;
import javax.jms.QueueConnection;
import javax.jms.QueueConnectionFactory;
import javax.jms.QueueSender;
import javax.jms.QueueSession;
import javax.jms.Session;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.dom4j.Node;
import org.pentaho.component.ComponentBase;
import org.pentaho.runtime.IRuntimeContext;
import org.pentaho.session.IPentahoSession;
import org.pentaho.system.PentahoSystem;

import br.com.auster.common.util.I18n;

/**
 * Sends an object into a JMS queue.
 * <p>
 * The object is expected in serializable form, inside a <code>java.lang.String</code> instance. This object
 * 	must be placed in an input parameter named {@value #MESSAGE_CONTENT_INPUT}.    
 * <p>
 * All other configuration, like JNDI context parameters and JMS connection names are defined in the configuration
 * 	file, named {@value #JMS_CONFIGURATION_FILE}, which <strong>must</strong> exist in the Pentaho system configuration directory.
 * These parameters are mandatory and if not set the component will not be able to work as expected.
 * <p>
 * The parameters needed in this file for JNDI context creation are :
 * <li>
 * 	<ul>{@value #JMS_JNDI_CONTEXT_FACTORY}</ul>
 * 	<ul>{@value #JMS_JNDI_CONTEXT_PACKAGE}</ul>
 * 	<ul>{@value #JMS_JNDI_CONTEXT_URL}</ul>
 * </li>
 * <p>
 * For JMS connection, the following parameters are waited : 
 * <li>
 * 	<ul>{@value #JMS_FACTORY}</ul>
 * 	<ul>{@value #JMS_NAME}</ul>
 * </li>
 * <p>
 * The component allows that a retry counter be specified. This can be done by defining an optional property named {@value #JMS_RETRY_COUNT}
 * 	in the configuration file. If not set, of it set to an invalid value, then this counter is set to the default value of {@value #JMS_RETRY_COUNT_DEFAULT}.
 * Note that in the retry process, the JMS connection objects are re-opened.
 * <p>
 * Also, a wait period before retrying can be define. This is achieved by defining the {@value #JMS_RETRY_WAIT} property in the configuration
 * 	file. By default, this parameter is set to {@value #JMS_RETRY_WAIT_DEFAULT}. The wait period will only occur if the send operation didnot
 * 	finished successfully and the retry counter has not reached the predefined limit. 
 * The waiting moment is set to happen after the unsuccessful send operation, but before trying to reconnect to the JMS service provider.
 * <p>
 * If, for any reason or at any time, the reconnect process doesnot work, then the send operation is aborted and the execution method returns a non-sucess
 * 	return code.  	   	  
 * 
 * @author framos
 * @version $Id$
 */
public class JMSSenderComponent extends ComponentBase {

	
	
	// ----------------------------
	// Public constants
	// ----------------------------

	private static final I18n i18n = I18n.getInstance(JMSSenderComponent.class); 
	
	// expected configuration file in pentaho system dir.
	public static final String JMS_CONFIGURATION_FILE = "jms/jms.xml";
	// properties needed for creating a jndi context 
	public static final String JMS_JNDI_CONTEXT_FACTORY = "jndi.context.factory";
	public static final String JMS_JNDI_CONTEXT_PACKAGE = "jndi.context.package";
	public static final String JMS_JNDI_CONTEXT_URL = "jndi.context.url";
	// properties needed for jms connection
	public static final String JMS_FACTORY = "jndi.jms.factory";
	public static final String JMS_NAME = "jndi.jms.name";
	// retry count
	public static final String JMS_RETRY_COUNT = "component.retry.count";
	// time to wait before retrying
	public static final String JMS_RETRY_WAIT = "component.retry.wait";
	// default value for retries, if not set in the configuration file
	public static final int JMS_RETRY_COUNT_DEFAULT = 1;
//	 default value for retry wait, if not set in the configuration file
	public static final int JMS_RETRY_WAIT_DEFAULT = 5;
	// expected input parameter with the object to send, in serializable form
	public static final String MESSAGE_CONTENT_INPUT = "message-content";

	
	
	// ----------------------------
	// Instance variables
	// ----------------------------
	
	// jndi & connection properties
	private Properties connProperties = new Properties();
	// jndi context
	private Context jndiContext;
	// jms queue
	private Queue queue;
	// queue connection
	private QueueConnection queueConnection;
	// connection session
	private QueueSession queueSession;
	// jms sender 
	private QueueSender queueSender;
	// number of retries, configured for this component
	private int retryCount;
	// wait time, in seconds, before retrying
	private int retryWait;
	
	
	
	// ----------------------------
	// Constructors
	// ----------------------------
	
	public JMSSenderComponent( String _instanceId, String _actionName, String _processId, 
							   Node _componentDefinition, IRuntimeContext _runtimeContext, 
							   IPentahoSession _sessionContext, int _loggingLevel, List _messages ) {
		
		super(_instanceId, _actionName, _processId, _componentDefinition, 
			  _runtimeContext, _sessionContext, _loggingLevel, _messages);
	}
	

	
	// ----------------------------
	// Interface methods
	// ----------------------------
	
	/**
	 * @see org.pentaho.component.IComponent#init()
	 */
	public boolean init() {
		
		try {
			connProperties.clear();
			// jndi context factory  
			connProperties.setProperty(Context.INITIAL_CONTEXT_FACTORY,
					   PentahoSystem.getSystemSetting(JMS_CONFIGURATION_FILE, JMS_JNDI_CONTEXT_FACTORY, null));
			getLogger().debug(i18n.getString("jmssender.configuration.propertyFound", 
							  JMS_JNDI_CONTEXT_FACTORY, 
							  connProperties.getProperty(Context.INITIAL_CONTEXT_FACTORY)));
			
			connProperties.setProperty(Context.URL_PKG_PREFIXES,
					   PentahoSystem.getSystemSetting(JMS_CONFIGURATION_FILE, JMS_JNDI_CONTEXT_PACKAGE, null));
			getLogger().debug(i18n.getString("jmssender.configuration.propertyFound", 
							  JMS_JNDI_CONTEXT_PACKAGE, 
							  connProperties.getProperty(Context.URL_PKG_PREFIXES)));
			
			connProperties.setProperty(Context.PROVIDER_URL,
					   PentahoSystem.getSystemSetting(JMS_CONFIGURATION_FILE, JMS_JNDI_CONTEXT_URL, null));
			getLogger().debug(i18n.getString("jmssender.configuration.propertyFound", 
							  JMS_JNDI_CONTEXT_URL, 
							  connProperties.getProperty(Context.PROVIDER_URL)));
			// jms information
			connProperties.setProperty(JMS_FACTORY,
					   PentahoSystem.getSystemSetting(JMS_CONFIGURATION_FILE, JMS_FACTORY, null));
			getLogger().debug(i18n.getString("jmssender.configuration.propertyFound", 
							  JMS_FACTORY, 
							  connProperties.getProperty(JMS_FACTORY)));

			connProperties.setProperty(JMS_NAME,
					   PentahoSystem.getSystemSetting(JMS_CONFIGURATION_FILE, JMS_NAME, null));
			getLogger().debug(i18n.getString("jmssender.configuration.propertyFound", 
							  JMS_NAME, 
							  connProperties.getProperty(JMS_NAME)));
			// retry count
			try {
				this.retryCount = Integer.parseInt(PentahoSystem.getSystemSetting(JMS_CONFIGURATION_FILE, JMS_RETRY_COUNT, null));
			} catch (NumberFormatException nfe) {
				this.retryCount=JMS_RETRY_COUNT_DEFAULT;
				getLogger().warn(i18n.getString("jmssender.configuration.retryCount.parseError", 
								 JMS_RETRY_COUNT, 
								 String.valueOf(JMS_RETRY_COUNT_DEFAULT)));
			} catch (NullPointerException npe) {
				this.retryCount=JMS_RETRY_COUNT_DEFAULT;
				getLogger().warn(i18n.getString("jmssender.configuration.retryCount.parseError", 
								 JMS_RETRY_COUNT, 
								 String.valueOf(JMS_RETRY_COUNT_DEFAULT)));
			} 
			getLogger().debug(i18n.getString("jmssender.configuration.propertyFound", 
							  JMS_RETRY_COUNT, 
							  String.valueOf(this.retryCount)));
			// retry wait 
			try {
				this.retryWait = Integer.parseInt(PentahoSystem.getSystemSetting(JMS_CONFIGURATION_FILE, JMS_RETRY_WAIT, null));
			} catch (NumberFormatException nfe) {
				this.retryWait=JMS_RETRY_WAIT_DEFAULT;
				getLogger().warn(i18n.getString("jmssender.configuration.retryCount.parseError", 
						 		 JMS_RETRY_WAIT, 
						 		 String.valueOf(JMS_RETRY_WAIT_DEFAULT)));
			} catch (NullPointerException npe) {
				this.retryWait=JMS_RETRY_WAIT_DEFAULT;
				getLogger().warn(i18n.getString("jmssender.configuration.retryCount.parseError", 
						 		 JMS_RETRY_WAIT, 
						 		 String.valueOf(JMS_RETRY_WAIT_DEFAULT)));
			} 
			// saving wait time in millisecs
			this.retryWait*=1000;
			getLogger().debug(i18n.getString("jmssender.configuration.propertyFound", 
					  		  JMS_RETRY_WAIT, 
					  		  String.valueOf(this.retryWait/1000)));
			
			// jndi context object
			getLogger().debug(i18n.getString("jmssender.configuration.namingInit", connProperties));
			this.jndiContext = new InitialContext(connProperties);
			
			if (! this.connectToQueue()) {
				return false;
			}
		} catch (NamingException ne) {
			getLogger().error(i18n.getString("jmssender.runtime.namingException"), ne);
			return false;
		}
		getLogger().info(i18n.getString("jmssender.configuration.done"));
		return true;
	}

	/**
	 * Puts the object, built back to its original form, in the JMS queue. If any JMS exception is raised due to 
	 * 	inconsistent connection variables, then it tries to reconnect and re-send the message.
	 *  
	 * @see org.pentaho.component.ComponentBase#executeAction()
	 */
	protected boolean executeAction() throws Throwable {
		int tries=0;
		do {
			try {
				send();
				return true;
			} catch (JMSException jmse) {
				getLogger().error(i18n.getString("jmssender.runtime.jmsException"), jmse);
				tries++;
				try {
					Thread.sleep(retryWait);
				} catch (InterruptedException ie) {
					getLogger().warn(i18n.getString("jmssender.runtime.waitPeriod"));
				}
				this.done();
				if (!this.connectToQueue()) { throw new IllegalStateException(i18n.getString("jmssender.runtime.cannotConnect")); }
			} catch (IOException ioe) {
				getLogger().error(i18n.getString("jmssender.runtime.deserializationError"), ioe);
				throw ioe;
			} catch (ClassNotFoundException cnfe) {
				getLogger().error(i18n.getString("jmssender.runtime.contentClassNotFound"), cnfe);
				throw cnfe;
			}
			getLogger().info(i18n.getString("jmssender.runtime.runningRetry", String.valueOf(tries)));
		} while (tries<=this.retryCount);
		getLogger().info(i18n.getString("jmssender.runtime.abortedSend"));
		return false;
	}

	/**
	 * @see org.pentaho.component.IComponent#done()
	 */
	public void done() {
		try {
			getLogger().info(i18n.getString("jmssender.runtime.done"));
			queueSender.close();
			queueSession.close();
			queueConnection.close();
		} catch (JMSException jmse) {
			getLogger().error(i18n.getString("jmssender.done.jmsException"), jmse);
		}
	}

	/**
	 * @see org.pentaho.component.ComponentBase#validateSystemSettings()
	 */
	protected boolean validateSystemSettings() {
		// configuration not properly set
		if ((PentahoSystem.getSystemSetting(JMS_CONFIGURATION_FILE, JMS_JNDI_CONTEXT_FACTORY, null)==null) ||
			(PentahoSystem.getSystemSetting(JMS_CONFIGURATION_FILE, JMS_JNDI_CONTEXT_PACKAGE, null)==null) ||
			(PentahoSystem.getSystemSetting(JMS_CONFIGURATION_FILE, JMS_JNDI_CONTEXT_URL, null)==null)) {
			getLogger().error(i18n.getString("jmssender.configuration.namingParameters"));
			return false;
		}
		// jms connection info not set
		if ((PentahoSystem.getSystemSetting(JMS_CONFIGURATION_FILE, JMS_FACTORY, null)==null) ||
			(PentahoSystem.getSystemSetting(JMS_CONFIGURATION_FILE, JMS_NAME, null)==null)) {
			getLogger().error(i18n.getString("jmssender.configuration.jmsParameters"));
			return false;
		}
		return true;
	}
	
	/**
	 * @see org.pentaho.component.ComponentBase#validateAction()
	 */
	protected boolean validateAction() {
		IRuntimeContext runtime = this.getRuntimeContext();
		Set inputs = runtime.getInputNames();
		// no serialized object found
		if (! inputs.contains(MESSAGE_CONTENT_INPUT)) {
			getLogger().error(i18n.getString("jmssender.runtime.noContent"));
			return false;
		}
		return true;
	}

	/**
	 * @see org.pentaho.component.PentahoBase#getLogger()
	 */
	public Log getLogger() {
		return LogFactory.getLog(JMSSenderComponent.class);
	}


	
	// ----------------------------
	// Private methods
	// ----------------------------	
	
	/**
	 * The real send message code
	 */
	private void send() throws JMSException, ClassNotFoundException, IOException {

		IRuntimeContext runtime = this.getRuntimeContext();
		Serializable contentAsObject = (Serializable) runtime.getInputParameterValue(MESSAGE_CONTENT_INPUT);
		//String content = runtime.getInputParameterStringValue(MESSAGE_CONTENT_INPUT);
		//Serializable contentAsObject = (Serializable) JMSSerializerUtil.deserializeObject(content);
		
		ObjectMessage msg = queueSession.createObjectMessage();	  
		queueConnection.start();
		msg.setObject(contentAsObject);
		queueSender.send(msg);
	}
	
	/**
	 * Opens the connection to the JMS queue 
	 */
	private boolean connectToQueue() {
		try {
			QueueConnectionFactory qcf = (QueueConnectionFactory) jndiContext.lookup(connProperties.getProperty(JMS_FACTORY));
			queue = (Queue) jndiContext.lookup(connProperties.getProperty(JMS_NAME));
			queueConnection = qcf.createQueueConnection();
			queueSession = queueConnection.createQueueSession(false, Session.AUTO_ACKNOWLEDGE);
			queueSender = queueSession.createSender(queue);
		} catch (JMSException jmse) {
			getLogger().error(i18n.getString("jmssender.runtime.jmsException"), jmse);
			return false;
		} catch (NamingException ne) {
			getLogger().error(i18n.getString("jmssender.runtime.namingException"), ne);
			return false;
		}
		return true;
	}
	
}
