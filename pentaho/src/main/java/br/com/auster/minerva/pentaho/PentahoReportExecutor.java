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

import java.io.IOException;

import org.pentaho.services.BaseRequestHandler;
import org.pentaho.session.IPentahoSession;
import org.pentaho.solution.IParameterProvider;
import org.pentaho.solution.SimpleParameterProvider;
import org.w3c.dom.Element;

import br.com.auster.common.util.I18n;
import br.com.auster.minerva.core.Report;
import br.com.auster.minerva.core.ReportException;
import br.com.auster.minerva.core.ReportInitializationException;
import br.com.auster.minerva.interfaces.ReportRequest;
import br.com.auster.minerva.pentaho.bean.BeanBuilderComponent;

/**
 * @author framos
 * @version $Id$
 */
public class PentahoReportExecutor implements Report {

	
	
	// ----------------------------
	// Class constants
	// ----------------------------
	
	public static final I18n i18n = I18n.getInstance(BeanBuilderComponent.class);
	
	
	
	// ----------------------------
	// Instance variables
	// ----------------------------
	
	private IPentahoSession session = null;
	
	
	
	// ----------------------------
	// Constructors
	// ----------------------------
	
	public PentahoReportExecutor(IPentahoSession _session) {
		this.session = _session;
	}
	
	
	
	// ----------------------------
	// Interface methods
	// ----------------------------
	
	/**
	 * @see br.com.auster.minerva.core.Report#generate(br.com.auster.minerva.interfaces.ReportRequest)
	 */
	public void generate(ReportRequest _request) throws ReportException {
		try {
			runActionSequence((PentahoReportRequest) _request);
		} catch (IOException ioe) {
			throw new ReportException(ioe);
		}
	}

	/**
	 * @see br.com.auster.minerva.core.Report#configure(org.w3c.dom.Element)
	 */
	public void configure(Element arg0) throws ReportInitializationException {
		// nothing to do
	}

	/**
	 * @see br.com.auster.minerva.core.Report#clone()
	 */
	public Object clone() {
		return null;
	}

	
	
	// ----------------------------
	// Private methods
	// ----------------------------
	
	private void runActionSequence(PentahoReportRequest _request) throws IOException {
		// adding request attributes before executing 
		IParameterProvider parameterProvider = createParameterProvider(_request);
		// running report
		BaseRequestHandler handler = new BaseRequestHandler(this.session, null, null, parameterProvider, null);
		handler.setSolutionName(_request.getSolution());
		handler.setAction(_request.getReportPath(), _request.getName());
		handler.setProcessId("");
		handler.handleActionRequest(0,0);
	}
	
	private IParameterProvider createParameterProvider(PentahoReportRequest _request) {
		return new SimpleParameterProvider(_request.getAttributes());
	}
	
}
