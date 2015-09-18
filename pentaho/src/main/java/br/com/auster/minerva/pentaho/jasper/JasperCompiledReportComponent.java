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
 * Created on 22/12/2005
 */
package br.com.auster.minerva.pentaho.jasper;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.security.GeneralSecurityException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.sql.DataSource;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.dom4j.Node;
import org.pentaho.component.ComponentBase;
import org.pentaho.runtime.IActionParameter;
import org.pentaho.runtime.IRuntimeContext;
import org.pentaho.session.IPentahoSession;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import br.com.auster.common.util.I18n;
import br.com.auster.common.xml.DOMUtils;

/**
 * This component allows that Jasper reports be executed. Differently from the built-in component for Jasper reports, this
 *   one works with pre-compiled report classes.
 * <p>
 * It expects as input parameter a file name (action input {@value #RUNTIME_ACTIONINPUT_OUTPUT_NAME}) to where to write. As 
 *   optionals, the configuration can define the output directory ({@value #RUNTIME_ACTIONINPUT_OUTPUT_DIR}), a list of general-pourpose 
 *   parameters ({@value #RUNTIME_ACTIONINPUT_PARAMETERS}) and a map of values to iterate over ({@value #RUNTIME_ACTIONINPUT_ITERATE_LIST}).
 * <p>
 * <pre>
 *    &lt;action-inputs&gt;
 *       &lt;output-dir  	type="string" 		mapping=".."/&gt;
 *       &lt;output-name 	type="string" 		mapping=".."/&gt;
 *       &lt;parameters  	type="property-map" mapping="..."/&gt;
 *       &lt;iterate-report type="property-map"	mapping="..."/&gt;
 *    &lt;/action-inputs>
 * </pre>
 * <p>
 * When this iterator map is defined, each iteration will generate a new report. In such cases, the action input {@value #RUNTIME_ACTIONINPUT_OUTPUT_NAME}  
 *   is ignored and the name of each generated file is selected from the value part of the map. The key part is set as part of the
 *   {@value #RUNTIME_ACTIONINPUT_PARAMETERS} attribute, named after {@value #CONFIGURATION_REPORT_ITEM_NAME} specified in the configuration
 *   of the report component.
 * <p>
 * Besides the action inputs, this component also requires a configuration in the action sequence XML document. A skeleton of such
 * 	configuration is presented bellow :
 * <p>
 * <pre>
 *    &lt;component-definition&gt;
 *       &lt;!-- the report class and the name to represent each iterator key, when the iterator map exists --&gt; 
 *       &lt;report-definition 
 *                       report-class="org.foo.Boo"
 *                       iterate-item="item"&gt;
 *          &lt;!-- 
 *              any configuration needed by this Jasper report implementation 
 *          --&gt;
 *       &lt;/report-definition&gt;
 *      
 *       &lt;datasource-configuration&gt;
 *          &lt;!-- properties name must follow the expected ones, such as defined bellow --&gt;
 *          &lt;property-name-1&gt;property-value&lt;/property-name-1&gt;
 *       &lt;datasource-configuration&gt;
 *    &lt;/component-definition&gt;
 * </pre>
 * <p>
 * The {@value #CONFIGURATION_REPORT_ITEM_NAME} attribute is only needed when the {@value #RUNTIME_ACTIONINPUT_ITERATE_LIST} action input
 * 	is defined.
 * <p>
 * The XML enclosed by the <code>report-definition</code> is passed as-is into the <code>configure()</code> method
 * 	of the {@link br.com.auster.minerva.pentaho.jasper.JasperReportGenerator} implementation. 
 * <p>
 * After the report is generated, the action output attributes {@value #RUNTIME_ACTIONOUTPUT_FILENAMES} and {@value #RUNTIME_ACTIONOUTPUT_EXECTIMES}
 * 	are defined an populated into the current runtime context. Use the following XML text to define those attributes (which is mandatory):
 * <p> 
 * <pre>
 *    &lt;action-outputs&gt;
 *       &lt;generated-file type="string-list"/&gt;
 *       &lt;time-spent		type="string-list"/&gt; 
 *    &lt;/action-outputs&gt;
 * </pre>
 * 
 * @author framos
 * @version $Id$
 */
public class JasperCompiledReportComponent extends ComponentBase {

	

	// ----------------------------
	// Public constants
	// ----------------------------
	
	public static final I18n i18n = I18n.getInstance(JasperCompiledReportComponent.class);
	
	public static final String RUNTIME_ACTIONINPUT_OUTPUT_NAME        = "output-name";
	public static final String RUNTIME_ACTIONINPUT_OUTPUT_DIR         = "output-dir";
	public static final String RUNTIME_ACTIONINPUT_PARAMETERS         = "parameters";
	public static final String RUNTIME_ACTIONINPUT_ITERATE_LIST       = "iterate-report";
	public static final String RUNTIME_ACTIONOUTPUT_FILENAMES         = "generated-file";
	public static final String RUNTIME_ACTIONOUTPUT_EXECTIMES         = "time-spent";
	
	public static final String RUNTIME_ACTIONINPUT_DEFAULT_ITEMNAME   = "__item";
	
	public static final String CONFIGURATION_REPORT_DEFINITION        = "report-definition";
	public static final String CONFIGURATION_REPORT_CLASS             = "report-class";
	public static final String CONFIGURATION_REPORT_ITEM_NAME         = "iterate-item";
	public static final String CONFIGURATION_DATASOURCE_CONFIGURATION = "datasource-configuration";
	
	
	public static final String CONFIGURATION_DATASOURCE_JNDI_NAME     = "jndi-name";
	public static final String CONFIGURATION_DATASOURCE_FDBC_DRIVER   = "driver";
	public static final String CONFIGURATION_DATASOURCE_JDBC_URL      = "url";

	
	
	// ----------------------------
	// Instance variables
	// ----------------------------
	
	// defines the name each value from the iteration list will be given, when put in the parameters list for execution
	private String iterateItemName;
	// report implementation class name 
	private String reportClass;
	// jdbc properites
	private Properties jdbcProperties;
	

	
	// ----------------------------
	// Constructors
	// ----------------------------
	
	public JasperCompiledReportComponent( String _instanceId, String _actionName, String _processId, 
						 	   			  Node _componentDefinition, IRuntimeContext _runtimeContext, 
						 	   			  IPentahoSession _sessionContext, int _loggingLevel, List _messages ) {
		
		super(_instanceId, _actionName, _processId, _componentDefinition, 
			  _runtimeContext, _sessionContext, _loggingLevel, _messages);
	}
	

	
	// ----------------------------
	// Interface methods
	// ----------------------------
	
	/**
	 * For this component to execute successfully, it must have at least the 
	 * 	<code>output-name</code> action input parameter.
	 * <p>
	 * This method will not validate the <code>&lt;component-defintion¨gt;</code> configuration
	 * 	node in the action sequence XML file. If such block is incorrect, then the {@link #init()} method
	 * 	will raise an error.
	 * <p>
	 * The other validations handled here are :
	 * <li>
	 * 	<ul>if <code>{@value #RUNTIME_ACTIONINPUT_OUTPUT_DIR}</code> input action parameter is set, then the directory must exist and be a directory
	 *  <ul>if <code>paremeters</code> input action paremeter is set, then it must be of type <code>property-map</code>
	 *  <ul>if <code>iterate-report</code> input action paremeter is set, then it must be of type <code>property-map</code>
	 * </li>
	 * Although it checks the <strong>defintion</strong> of each parameter, this method does not validates if the 
	 * 	values of them are really of the expected type. 
	 *  
	 * @see org.pentaho.component.ComponentBase#validateAction()
	 */
	protected boolean validateAction() {
		IRuntimeContext ctx = this.getRuntimeContext();
		
		if (!(ctx.getInputNames().contains(RUNTIME_ACTIONINPUT_OUTPUT_NAME) || ctx.getInputNames().contains(RUNTIME_ACTIONINPUT_ITERATE_LIST))) {
			return false;
		}
		// checking output-dir action input
		if (ctx.getInputNames().contains(RUNTIME_ACTIONINPUT_OUTPUT_DIR)) {
			String outputDir = ctx.getInputParameterStringValue(RUNTIME_ACTIONINPUT_OUTPUT_DIR);
			File f = new File(outputDir);
			if ((!f.exists()) || (!f.isDirectory())) {
				return false;
			}
		}
		// checking parameters action input
		if (ctx.getInputNames().contains(RUNTIME_ACTIONINPUT_PARAMETERS)) {
			IActionParameter params = ctx.getInputParameter(RUNTIME_ACTIONINPUT_PARAMETERS);
			if (!"property-map".equals(params.getType())) {
				return false;
			}
		}
		// checking parameters action input
		if (ctx.getInputNames().contains(RUNTIME_ACTIONINPUT_ITERATE_LIST)) {
			IActionParameter params = ctx.getInputParameter(RUNTIME_ACTIONINPUT_ITERATE_LIST);
			if (!"property-map".equals(params.getType())) {
				return false;
			}
		}
		return true;
	}

	/**
	 * @see org.pentaho.component.ComponentBase#validateSystemSettings()
	 */
	protected boolean validateSystemSettings() {
		return true;
	}

	/**
	 * @see org.pentaho.component.ComponentBase#done()
	 */
	public void done() {
	}

	/**
	 * @see org.pentaho.component.ComponentBase#executeAction()
	 */
	protected boolean executeAction() throws Throwable {
		
		IRuntimeContext ctx = this.getRuntimeContext();
		
		// define output directory 
		String outputDir = System.getProperty("user.dir");
		if (ctx.getInputNames().contains(RUNTIME_ACTIONINPUT_OUTPUT_DIR)) {
			outputDir = ctx.getInputParameterStringValue(RUNTIME_ACTIONINPUT_OUTPUT_DIR);
		}
		getLogger().debug(i18n.getString("reportcomponent.runtime.outdir", outputDir));
		
		// building parameter map
		Map parameters = new HashMap();
		if (ctx.getInputNames().contains(RUNTIME_ACTIONINPUT_PARAMETERS)) {
			IActionParameter params = ctx.getInputParameter(RUNTIME_ACTIONINPUT_PARAMETERS);
			parameters.putAll((Map) params.getValue());
		}
		getLogger().debug(i18n.getString("reportcomponent.runtime.parameters", parameters));

		// datasource connection create AND closed for each execution
		Connection connection = createConnection();
		// jasper generator instance is created for each execution also
		JasperReportGenerator generator = createGenerator();
		// output attributes
		ArrayList outputNames = new ArrayList();
		ArrayList outputTimes = new ArrayList();
		long starttime, endtime;
		try {
			// building iteraction list && executing report
			Collection iterateList = null;
			if (ctx.getInputNames().contains(RUNTIME_ACTIONINPUT_ITERATE_LIST)) {
				if (this.iterateItemName == null) {
					this.iterateItemName = RUNTIME_ACTIONINPUT_DEFAULT_ITEMNAME;
				}
				IActionParameter params = ctx.getInputParameter(RUNTIME_ACTIONINPUT_ITERATE_LIST);
				Map iterateMap = (Map)params.getValue();
				iterateList = ((Map)params.getValue()).keySet();
				for (Iterator it=iterateList.iterator(); it.hasNext();) {
					Object key = it.next();
					// building output filename
					String outputName = outputDir + System.getProperty("file.separator") + (String)iterateMap.get(key);
					getLogger().debug(i18n.getString("reportcomponent.runtime.outputName", outputName));
					// execute iteraction
					parameters.put(this.iterateItemName, key);
					getLogger().debug(i18n.getString("reportcomponent.runtime.iterateItem", this.iterateItemName, key));
					starttime = Calendar.getInstance().getTimeInMillis();
					generateReport(connection, generator, outputName, parameters);
					endtime = Calendar.getInstance().getTimeInMillis();
					// setting output attributes
					outputNames.add(outputName);
					outputTimes.add(String.valueOf(endtime-starttime));
				}
			} else {
				// building output filename
				String outputName = outputDir + System.getProperty("file.separator") + ctx.getInputParameterStringValue(RUNTIME_ACTIONINPUT_OUTPUT_NAME);
				getLogger().debug(i18n.getString("reportcomponent.runtime.outputName", outputName));
				// execute without iteraction
				starttime = Calendar.getInstance().getTimeInMillis();
				generateReport(connection, generator, outputName, parameters);
				endtime = Calendar.getInstance().getTimeInMillis();
				// setting output attributes
				outputNames.add(outputName);
				outputTimes.add(String.valueOf(endtime-starttime));
			}
		} finally {
			connection.close();
		}
		// setting output attributes
		ctx.setOutputValue(RUNTIME_ACTIONOUTPUT_FILENAMES, outputNames);
		getLogger().debug(i18n.getString("reportcomponent.runtime.outputNames", outputNames));
		ctx.setOutputValue(RUNTIME_ACTIONOUTPUT_EXECTIMES, outputTimes);
		getLogger().debug(i18n.getString("reportcomponent.runtime.outputTimes", outputTimes));
		// done!
		getLogger().debug(i18n.getString("reportcomponent.runtime.done"));		
		return true;
	}

	/**
	 * @see org.pentaho.component.ComponentBase#init()
	 */
	public boolean init() {
		
		getLogger().debug(i18n.getString("reportcomponent.configuration.start"));
		if (this.jdbcProperties == null) {
			 this.jdbcProperties = new Properties();
		}
		jdbcProperties.clear();
		
		try {
			String str = this.getComponentDefinition().asXML();
			Element componentDefinition = DOMUtils.openDocument(new ByteArrayInputStream(str.getBytes()));
		
			Element jdbcConf = DOMUtils.getElement(componentDefinition, CONFIGURATION_DATASOURCE_CONFIGURATION, true);
			NodeList list = DOMUtils.getElements(jdbcConf, null);
			for (int i=0; i < list.getLength(); i++) {
				Element prop = (Element) list.item(i);
				jdbcProperties.setProperty(prop.getNodeName(), DOMUtils.getText(prop).toString());
				getLogger().debug(i18n.getString("reportcomponent.configuration.property", prop.getNodeName(), jdbcProperties.getProperty(prop.getNodeName())));
			}
			
			Element reportConf = DOMUtils.getElement(componentDefinition, CONFIGURATION_REPORT_DEFINITION, true);
			this.reportClass = DOMUtils.getAttribute(reportConf, CONFIGURATION_REPORT_CLASS, true);
			getLogger().debug(i18n.getString("reportcomponent.configuration.reportClass", this.reportClass));
			this.iterateItemName = DOMUtils.getAttribute(reportConf, CONFIGURATION_REPORT_ITEM_NAME, false);
			if (this.iterateItemName != null) {
				getLogger().debug(i18n.getString("reportcomponent.configuration.iterateItem", this.iterateItemName));
			}
			
			getLogger().debug(i18n.getString("reportcomponent.configuration.finishedOK"));
			return true;
			
		} catch (SAXException saxe) {
			getLogger().error(i18n.getString("reportcomponent.configuration.componentConfError"), saxe);
		} catch (ParserConfigurationException pce) {
			getLogger().error(i18n.getString("reportcomponent.configuration.componentConfError"), pce);
		} catch (GeneralSecurityException gse) {
			getLogger().error(i18n.getString("reportcomponent.configuration.componentConfError"), gse);
		} catch (IOException ioe) {
			getLogger().error(i18n.getString("reportcomponent.configuration.componentConfError"), ioe);
		}
		return false;
	}

	/**
	 * @see org.pentaho.system.PentahoBase#getLogger()
	 */
	public Log getLogger() {
		return LogFactory.getLog(JasperCompiledReportComponent.class);
	}

	
	
	// ----------------------------
	// Private methods
	// ----------------------------

	private JasperReportGenerator createGenerator() throws Throwable {
		String str = this.getComponentDefinition().asXML();
		Element componentDefinition = DOMUtils.openDocument(new ByteArrayInputStream(str.getBytes()));
		JasperReportGenerator generator = (JasperReportGenerator) Class.forName(this.reportClass).newInstance();
		generator.configure(DOMUtils.getElement(componentDefinition, CONFIGURATION_REPORT_DEFINITION, true));
		getLogger().debug(i18n.getString("reportcomponent.runtime.generatorCreated"));
		return generator;
	}
	
	private void generateReport(Connection _conn, JasperReportGenerator _generator, String _outputName, Map _parameters) throws Throwable {
		OutputStream outStream = new FileOutputStream(_outputName);
		try {
			_generator.generateReport(_conn, _parameters, outStream);
		} finally {
			outStream.close();
		}
	}
	
	private Connection createConnection() throws Throwable {
		try {
			Context ctx = new InitialContext(jdbcProperties);
			DataSource ds = (DataSource) ctx.lookup(jdbcProperties.getProperty(CONFIGURATION_DATASOURCE_JNDI_NAME));
			return ds.getConnection();
		} catch (Exception e) {
			getLogger().debug(i18n.getString("reportcomponent.runtime.jndiConnNotOK"));
			// try direct connection creation
			Class.forName(jdbcProperties.getProperty(CONFIGURATION_DATASOURCE_FDBC_DRIVER));
			// must have 'user' and 'password' properties in jdbcProperties to connect to database 
			getLogger().debug(i18n.getString("reportcomponent.runtime.jdbcUrlConnected",jdbcProperties.getProperty(CONFIGURATION_DATASOURCE_JDBC_URL)));
			return DriverManager.getConnection(jdbcProperties.getProperty(CONFIGURATION_DATASOURCE_JDBC_URL), jdbcProperties);
		}
	}
}
