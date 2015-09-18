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
 * Created on 09/03/2007
 */
package br.com.auster.minerva.csv.report;

import java.io.IOException;
import java.security.GeneralSecurityException;

import javax.xml.parsers.ParserConfigurationException;

import org.apache.log4j.Logger;
import org.w3c.dom.Element;
import org.xml.sax.SAXException;

import br.com.auster.common.xml.DOMUtils;
import br.com.auster.minerva.core.ReportInitializationException;

import junit.framework.TestCase;


/**
 * TODO What this class is responsible for
 *
 * @author mtengelm
 * @version $Id$
 * @since 09/03/2007
 */
public class TesteRowFlipReport extends TestCase {

	private static Logger log = Logger.getLogger(TesteRowFlipReport.class);
	
	private static final String	CONFIGURE_FILE	= "report/testVIVOReportConfiguration.xml";

	private RowFlipCSVReport	report;
	/**
	 * TODO why this methods was overriden, and what's the new expected behavior.
	 * 
	 * @throws java.lang.Exception
	 * @see junit.framework.TestCase#setUp()
	 */
	protected void setUp() throws Exception {
		super.setUp();
		report = new RowFlipCSVReport();
		try { 
		report.configure(DOMUtils.openDocument(CONFIGURE_FILE, false));
		} catch (Exception e) {
			e.printStackTrace();
			fail("Error due to exception.");
		}
	}

	public void testReport() throws ReportInitializationException, ParserConfigurationException, SAXException, IOException, GeneralSecurityException {
		String generatedFilename = report.getGeneratedFilename();
		log.info("Generated File name is:" + generatedFilename);
		

		
	}
	/**
	 * TODO why this methods was overriden, and what's the new expected behavior.
	 * 
	 * @throws java.lang.Exception
	 * @see junit.framework.TestCase#tearDown()
	 */
	protected void tearDown() throws Exception {
		report = null;
		super.tearDown();
	}

}
