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
package br.com.auster.minerva.pentaho;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.pentaho.session.IPentahoSession;
import org.pentaho.session.StandaloneSession;
import org.pentaho.system.PentahoSystem;
import org.pentaho.system.StandaloneApplicationContext;
import org.w3c.dom.Element;

import br.com.auster.common.util.I18n;
import br.com.auster.common.xml.DOMUtils;
import br.com.auster.minerva.core.MinervaConfigurationConstants;
import br.com.auster.minerva.core.Report;
import br.com.auster.minerva.core.ReportFactory;
import br.com.auster.minerva.core.ReportInitializationException;
import br.com.auster.minerva.interfaces.ReportRequest;
import br.com.auster.minerva.pentaho.bean.BeanBuilderComponent;



/**
 * @author framos
 * @version $Id$
 */
public class PentahoReportFactory implements ReportFactory {

	
	
	// ----------------------------
	// Class constants
	// ----------------------------
	
	public static final I18n i18n = I18n.getInstance(BeanBuilderComponent.class);
	private static final Log log = LogFactory.getLog(BeanBuilderComponent.class);
		
	public static final String PENTAHO_CONFIGURATION_FILENAME = "pentaho-file";

	
	
	// ----------------------------
	// Instance variables
	// ----------------------------
	
	private StandaloneApplicationContext appContext = null;
	private IPentahoSession session = null;
	

	
	// ----------------------------
	// Interface methods
	// ----------------------------
	
	/**
	 * @see br.com.auster.minerva.core.ReportFactory#getReport(br.com.auster.minerva.interfaces.ReportRequest)
	 */
	public Report getReport(ReportRequest _request) {
		if (_request instanceof PentahoReportRequest) {
			return buildActionSequenceExecutor();
		}
		throw new IllegalArgumentException(i18n.getString("factory.pentaho.invalidReport", _request.getClass()));
	}

	/**
	 * @see  br.com.auster.minerva.core.ReportFactory#isReportAvailable(br.com.auster.minerva.interfaces.ReportRequest)
	 */
	public boolean isReportAvailable(ReportRequest _request) {
		return true;
	}

	/**
	 * @see  br.com.auster.minerva.core.ReportFactory#configure(org.w3c.dom.Element)
	 */
	public void configure(Element _configuration) throws ReportInitializationException {
		_configuration = DOMUtils.getElement(_configuration, MinervaConfigurationConstants.MINERVA_CONFIGURATION_SUBELEMENT, true);
		StringBuffer filename = DOMUtils.getText(DOMUtils.getElement(_configuration, PENTAHO_CONFIGURATION_FILENAME, true));
		log.debug(i18n.getString("factory.pentaho.initfile", filename.toString()));
		this.appContext = new StandaloneApplicationContext(filename.toString(), null);
		PentahoSystem.init(this.appContext);
		log.debug(i18n.getString("factory.pentaho.systemInit"));
		this.session = new StandaloneSession("minerva-pentaho");
	}

	
	
	// ----------------------------
	// Private methods
	// ----------------------------
	
	private Report buildActionSequenceExecutor() {
		return new PentahoReportExecutor(this.session);
	}
}
