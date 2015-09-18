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
package br.com.auster.minerva.pentaho.bean;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.xml.parsers.ParserConfigurationException;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.dom4j.Node;
import org.pentaho.component.ComponentBase;
import org.pentaho.runtime.IRuntimeContext;
import org.pentaho.session.IPentahoSession;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import br.com.auster.common.util.I18n;
import br.com.auster.common.xml.DOMUtils;

/**
 * 
 * Creates an instance of any Javabean-like class, and sets its attributes with the available parameters. Although there is 
 * 	no definition of type/class for bean attributes, the values set to each one of them <strong>must</strong> match the 
 *  class definition type or a runtime cast exception will be raised.
 * <p>
 * Attributes can be set to constant values, setting the {@value #CONFIGURATION_ATTR_TYPE} in the component 
 *  configuration to {@value #CONFIGURATION_ATTR_TYPEIS_VALUE}. When set to {@value #CONFIGURATION_ATTR_TYPEIS_ACTIONINPUT} 
 *  then the value is used as key to lookup an action input parameter. If there is no parameter with such key, then a  
 *  <code>NullPointerException</code> is raised.
 * <p>
 * Check bellow how to configure this component :
 * <p>      
 * <pre>
 * 	&lt;component-definition&gt;
 *		&lt;!-- define as text of the node, the fully qualified name of the bean class --&gt;
 * 		&lt;bean-class&gt;...&lt;/bean-class&gt;
 *      &lt;!-- here comes the list of all bean attributes to be set --&gt;
 * 		&lt;attribute-mapping&gt;
 * 			&lt;!-- for each attribute, indicate its name and type. If type is 
 *                   * {@value #CONFIGURATION_ATTR_TYPEIS_ACTIONINPUT}: the name in the element text area is used as action input name
 *                   * {@value #CONFIGURATION_ATTR_TYPEIS_VALUE}: the element text area is copied as value for the attribute
 *          --&gt;
 * 			&lt;attribute name="..." type="value|input"&gt;...&lt;/attribute&gt;
 * 		&lt;/attribute-mapping&gt;
 * 	&lt;/component-definition&gt;
 * </pre>
 * <p>
 * After all set operations are completed, the resulting bean is exposed in the action output parameter identified as 
 *   {@value #CONFIGURATION_ACTIONOUTPUT_BEAN}. 
 * 
 * @author framos
 * @version $Id$
 */
public class BeanBuilderComponent extends ComponentBase {

	

	// ----------------------------
	// Public constants
	// ----------------------------
	
	public static final I18n i18n = I18n.getInstance(BeanBuilderComponent.class);
	
	
	public static final String CONFIGURATION_BEAN_CLASS_ELEMENT       = "bean-class";
	public static final String CONFIGURATION_ATTRIBUTE_MAPPING        = "attribute-mapping";
	public static final String CONFIGURATION_ATTRIBUTE_ELEMENT        = "attribute";
	public static final String CONFIGURATION_ATTR_NAME                = "name";
	public static final String CONFIGURATION_ATTR_TYPE                = "type";
	
	public static final String CONFIGURATION_ATTR_TYPEIS_VALUE        = "value";
	public static final String CONFIGURATION_ATTR_TYPEIS_ACTIONINPUT  = "input";
	
	public static final String CONFIGURATION_ACTIONOUTPUT_BEAN        = "result-bean";
	
	
	// ----------------------------
	// Instance variables
	// ----------------------------
	
	private String classname = null;
	private Map inputAttributes = new HashMap();
	private Map constAttributes = new HashMap();
	

	
	// ----------------------------
	// Constructors
	// ----------------------------
	
	public BeanBuilderComponent( String _instanceId, String _actionName, String _processId, 
						 	   			  Node _componentDefinition, IRuntimeContext _runtimeContext, 
						 	   			  IPentahoSession _sessionContext, int _loggingLevel, List _messages ) {
		
		super(_instanceId, _actionName, _processId, _componentDefinition, 
			  _runtimeContext, _sessionContext, _loggingLevel, _messages);
		
	}
	

	
	// ----------------------------
	// Interface methods
	// ----------------------------
	
	/**
	 * @see org.pentaho.component.ComponentBase#validateAction()
	 */
	protected boolean validateAction() {
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
	public void done() {}

	/**
	 * @see org.pentaho.component.ComponentBase#executeAction()
	 */
	protected boolean executeAction() throws Throwable {
		IRuntimeContext ctx = this.getRuntimeContext();
		// creates the bean instance
		Object bean = Class.forName(this.classname).newInstance();
		getLogger().debug(i18n.getString("beancomponent.runtime.instanceCreated", this.classname));
		// sets all constant values
		for (Iterator it=constAttributes.entrySet().iterator(); it.hasNext();) {
			Map.Entry entry = (Map.Entry)it.next();
			getLogger().debug(i18n.getString("beancomponent.runtime.propertySet", entry.getKey(), entry.getValue()));
			BeanUtils.setProperty(bean, (String)entry.getKey(), entry.getValue());
		}
		// sets all attributes from action inputs
		for (Iterator it=inputAttributes.entrySet().iterator(); it.hasNext();) {
			Map.Entry entry = (Map.Entry)it.next();
			Object value = getActionInput(ctx, (String)entry.getValue());
			getLogger().debug(i18n.getString("beancomponent.runtime.propertySet", entry.getKey(), value));
			BeanUtils.setProperty(bean, (String)entry.getKey(), value);
		}
		getLogger().debug(i18n.getString("beancomponent.runtime.beanPutInContext"));
		ctx.setOutputValue(CONFIGURATION_ACTIONOUTPUT_BEAN, bean);
		return true;
	}

	/**
	 * @see org.pentaho.component.ComponentBase#init()
	 */
	public boolean init() {
		
		constAttributes.clear();
		inputAttributes.clear();
		getLogger().debug(i18n.getString("beancomponent.configuration.start"));
		try {
			String str = this.getComponentDefinition().asXML();
			Element componentDefinition = DOMUtils.openDocument(new ByteArrayInputStream(str.getBytes()));
		
			this.classname = DOMUtils.getText(DOMUtils.getElement(componentDefinition, CONFIGURATION_BEAN_CLASS_ELEMENT, true)).toString();
			getLogger().debug(i18n.getString("beancomponent.configuration.classname", this.classname));			
			NodeList list = DOMUtils.getElements(DOMUtils.getElement(componentDefinition, CONFIGURATION_ATTRIBUTE_MAPPING, true), CONFIGURATION_ATTRIBUTE_ELEMENT);
			for (int i=0; i < list.getLength(); i++) {
				Element prop = (Element) list.item(i);
				String attrname = DOMUtils.getAttribute(prop, CONFIGURATION_ATTR_NAME, true);
				String attrvalue = DOMUtils.getText(prop).toString();
				if (CONFIGURATION_ATTR_TYPEIS_ACTIONINPUT.equals(DOMUtils.getAttribute(prop, CONFIGURATION_ATTR_TYPE, true))) {
					inputAttributes.put(attrname, attrvalue);
					getLogger().debug(i18n.getString("beancomponent.configuration.inputAttribute", attrname, attrvalue));
				} else {
					constAttributes.put(attrname, attrvalue);
					getLogger().debug(i18n.getString("beancomponent.configuration.constAttribute", attrname, attrvalue));
				}
			}
			getLogger().info(i18n.getString("beancomponent.configuration.finishedOK"));
			return true;
		} catch (SAXException saxe) {
			getLogger().error(i18n.getString("beancomponent.configuration.componentConfError"), saxe);
		} catch (ParserConfigurationException pce) {
			getLogger().error(i18n.getString("beancomponent.configuration.componentConfError"), pce);
		} catch (GeneralSecurityException gse) {
			getLogger().error(i18n.getString("beancomponent.configuration.componentConfError"), gse);
		} catch (IOException ioe) {
			getLogger().error(i18n.getString("beancomponent.configuration.componentConfError"), ioe);
		}
		return false;
	}

	/**
	 * @see org.pentaho.system.PentahoBase#getLogger()
	 */
	public Log getLogger() {
		return LogFactory.getLog(BeanBuilderComponent.class);
	}

	
	
	// ----------------------------
	// Private methods
	// ----------------------------

	private Object getActionInput(IRuntimeContext _ctx, String _actionName) {
		return _ctx.getInputParameterValue(_actionName);
	}
}
