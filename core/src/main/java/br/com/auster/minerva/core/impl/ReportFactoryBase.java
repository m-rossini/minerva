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
 * Created on 07/12/2005
 */
package br.com.auster.minerva.core.impl;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import br.com.auster.common.util.I18n;
import br.com.auster.common.xml.DOMUtils;
import br.com.auster.minerva.core.MinervaConfigurationConstants;
import br.com.auster.minerva.core.Report;
import br.com.auster.minerva.core.ReportFactory;
import br.com.auster.minerva.core.ReportInitializationException;
import br.com.auster.minerva.spi.ReportRequest;

/**
 * This is the basic implementation of the <code>ReportFactory</code> interface. It expects that the 
 * 	report name be specified in the instance variable <strong>name</strong>. If the corresponding name 
 *  was not configured, then no report will be triggered.
 * <p> 
 * The configuration layout for this implementation is :
 * 
 * <pre>
 * 
 * &lt;!-- Configuration of each report to be executed --&gt;
 * &lt;configuration/&gt;
 * 
 *     &lt;!-- 
 *          For each report, specify its name and class implementation. If two or more
 *     		reports have the same name, only one - which cannot be determined - will be
 *     		really available. 
 *     --&gt;
 *     &lt;report name="..."
 *                class="..."&gt;
 *         &lt;!-- Any configuration needed by the report implementation --&gt;
 *         &lt;configuration/&gt;                
 *     &lt;/report&gt;
 *     
 * &lt;/configuration&gt;
 * </pre>
 *  
 * 
 * @author framos
 * @version $Id$
 */
public class ReportFactoryBase implements ReportFactory {

	
	
	// ----------------------------
	// Class constants
	// ----------------------------
	
    private static final I18n i18n = I18n.getInstance(ReportFactoryBase.class);
    private static final Log log = LogFactory.getLog(ReportFactoryBase.class);
	
	public static final String MINERVA_CONFIGURATION_REPORT_SUBELEMENT = "report";
	
	
	
	// ----------------------------
	// Instance variables
	// ----------------------------	
	
	private Map reportMap; 
	
	
	
	// ----------------------------
	// Interface methods
	// ----------------------------
	
	/**
	 * @see br.com.auster.minerva.core.ReportFactory#getReport(br.com.auster.minerva.spi.ReportRequest)
	 */
	public Report getReport(ReportRequest _request) {
		Report report = (Report) reportMap.get(_request.getName());
		log.trace("ReportMap:" + reportMap);
		if (report == null) {
			return null;
		}
		log.trace("Returning a new report. Using cloning from report instance. Report Class:" + report.getClass().getName());
		return (Report) report.clone();
	}

	/**
	 * @see br.com.auster.minerva.core.ReportFactory#isReportAvailable(br.com.auster.minerva.spi.ReportRequest)
	 */
	public boolean isReportAvailable(ReportRequest _request) {
		return reportMap.containsKey(_request.getName());
	}

	/**
	 * @see br.com.auster.minerva.core.ReportFactory#configure(org.w3c.dom.Element)
	 */
	@SuppressWarnings("unchecked")
	public void configure(Element _configuration) throws ReportInitializationException {
		// initializes the map of reports
		log.debug("Starting report factory configuration.");
		if (reportMap == null) {
			reportMap = new HashMap();
		}
		reportMap.clear();
		// read configuration for each report
		_configuration = DOMUtils.getElement(_configuration, MinervaConfigurationConstants.MINERVA_CONFIGURATION_SUBELEMENT, true);
		NodeList reportList = DOMUtils.getElements(_configuration, MINERVA_CONFIGURATION_REPORT_SUBELEMENT);
		try {			
			for (int i=0; i < reportList.getLength(); i++) {
				Element reportConfiguration = (Element) reportList.item(i);
				String klassName = DOMUtils.getAttribute(reportConfiguration, MinervaConfigurationConstants.MINERVA_CLASSNAME_ATTRIBUTE, true);
				log.info(i18n.getString("factory.base.configuredReport", klassName));
				Report r = (Report) Class.forName(klassName).newInstance();
				r.configure(DOMUtils.getElement(reportConfiguration, MinervaConfigurationConstants.MINERVA_CONFIGURATION_SUBELEMENT, true));
				reportMap.put(DOMUtils.getAttribute(reportConfiguration, MinervaConfigurationConstants.MINERVA_NAME_ATTRIBUTE, true), r);
			}
			log.trace("Report Map:" + reportMap);
		} catch (ClassNotFoundException cnfe) {
			log.error(i18n.getString("factory.base.configureError", cnfe));
			throw new ReportInitializationException(cnfe);
		} catch (InstantiationException ie) {
			log.error(i18n.getString("factory.base.configureError", ie));
			throw new ReportInitializationException(ie);
		} catch (IllegalAccessException iae) {
			log.error(i18n.getString("factory.base.configureError", iae));
			throw new ReportInitializationException(iae);
		}
	}

}
