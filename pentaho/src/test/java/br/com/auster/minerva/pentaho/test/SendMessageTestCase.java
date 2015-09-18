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
 * Created on 20/12/2005
 */
package br.com.auster.minerva.pentaho.test;

import java.io.ByteArrayOutputStream;

import junit.framework.TestCase;

import org.pentaho.services.BaseRequestHandler;
import org.pentaho.session.IPentahoSession;
import org.pentaho.session.StandaloneSession;
import org.pentaho.solution.SimpleOutputHandler;
import org.pentaho.solution.SimpleParameterProvider;
import org.pentaho.system.PentahoSystem;
import org.pentaho.system.StandaloneApplicationContext;

import br.com.auster.minerva.pentaho.jms.JMSSenderComponent;

/**
 * @author framos
 * @version $Id$
 */
public class SendMessageTestCase extends TestCase {

	
	private IPentahoSession session;
	
	public void setUp() throws Exception {
		super.setUp();
		
		String solutionPath = "src/test/resources/conf";
		StandaloneApplicationContext context = new StandaloneApplicationContext(solutionPath, null);
		PentahoSystem.init(context);
		session = new StandaloneSession("session");
	} 
	
	public void tearDown() throws Exception {
		PentahoSystem.shutdown();
	}
	
	public void testJMSSenderComponent() {

		try {
			ByteArrayOutputStream pout = new ByteArrayOutputStream();
			SimpleOutputHandler outHandler = new SimpleOutputHandler(pout, true);
			
			SimpleParameterProvider params = new SimpleParameterProvider();
			params.setParameter(JMSSenderComponent.MESSAGE_CONTENT_INPUT, "Frederico Ramos");
			
			BaseRequestHandler handler = new BaseRequestHandler(session, null, outHandler, params, null);
			handler.setSolutionName("test");
			handler.setAction("jms", "test1.xaction");
			handler.setProcessId("");
	
			handler.handleActionRequest(0,0);
			
		} catch (Exception e) {
			e.printStackTrace();
			fail();
		}
		assertTrue(true);
	}

}
