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

import java.util.Calendar;

import junit.framework.TestCase;

import org.pentaho.runtime.IRuntimeContext;
import org.pentaho.services.BaseRequestHandler;
import org.pentaho.session.IPentahoSession;
import org.pentaho.session.StandaloneSession;
import org.pentaho.solution.SimpleParameterProvider;
import org.pentaho.system.PentahoSystem;
import org.pentaho.system.StandaloneApplicationContext;

import br.com.auster.minerva.pentaho.PentahoSchedulerRequest;

/**
 * @author framos
 * @version $Id$
 */
public class SchedulerTestCase extends TestCase {

	
	private IPentahoSession session;
	
	public void setUp() throws Exception {
		super.setUp();
		String solutionPath = "src/test/conf";
		StandaloneApplicationContext context = new StandaloneApplicationContext(solutionPath, null);
		PentahoSystem.init(context);
		session = new StandaloneSession("session");
	} 
	
	public void tearDown() throws Exception {
		PentahoSystem.shutdown();
	}
	
	public void testNotScheduledException() {
		
		try {
			PentahoSchedulerRequest request = new PentahoSchedulerRequest("test", "bean", "test1.xaction"); 
			request.setJobName("my test job");
			request.setTriggerName("trigger it now");
			
			SimpleParameterProvider params = new SimpleParameterProvider(request.getAttributes());
			
			BaseRequestHandler handler = new BaseRequestHandler(session, null, null, params, null);
			handler.setSolutionName(request.getSolution());
			handler.setAction(request.getReportPath(), request.getName());
			handler.setProcessId("");
			handler.handleActionRequest(0,0);
			fail();
		} catch (IllegalStateException ise) {
			assertTrue(true);
		} catch (Exception e) {
			e.printStackTrace();
			fail();
		}
	}

	public void testSimpleScheduling() {
		
		try {
			PentahoSchedulerRequest request = new PentahoSchedulerRequest("test", "bean", "test1.xaction"); 
			request.setJobName("my simple job");
			request.setTriggerName("trigger simple now");
			request.schedule(1, 20);
			
			SimpleParameterProvider params = new SimpleParameterProvider(request.getAttributes());
			
			BaseRequestHandler handler = new BaseRequestHandler(session, null, null, params, null);
			handler.setSolutionName(request.getSolution());
			handler.setAction(request.getReportPath(), request.getName());
			handler.setProcessId("");
	
			IRuntimeContext ctx = handler.handleActionRequest(0,0);
			assertEquals(IRuntimeContext.RUNTIME_STATUS_SUCCESS, ctx.getStatus());
		} catch (Exception e) {
			e.printStackTrace();
			fail();
		}
	}
	
	public void testCronScheduling() {
		
		try {
			PentahoSchedulerRequest request = new PentahoSchedulerRequest("test", "bean", "test1.xaction"); 
			request.setJobName("my cron job");
			request.setTriggerName("trigger cron now");
			
			Calendar now = Calendar.getInstance();
			now.roll(Calendar.SECOND, 15);
			
			String cronString = String.valueOf(now.get(Calendar.SECOND)) + " " +
			                    String.valueOf(now.get(Calendar.MINUTE)) + " " +
			                    String.valueOf(now.get(Calendar.HOUR_OF_DAY)) + " " +
			                    String.valueOf(now.get(Calendar.DAY_OF_MONTH)) + " " +
			                    String.valueOf(now.get(Calendar.MONTH)+1) + " " +
			                    "?";			
			request.schedule(cronString);
			
			SimpleParameterProvider params = new SimpleParameterProvider(request.getAttributes());
			
			BaseRequestHandler handler = new BaseRequestHandler(session, null, null, params, null);
			handler.setSolutionName(request.getSolution());
			handler.setAction(request.getReportPath(), request.getName());
			handler.setProcessId("");
	
			IRuntimeContext ctx = handler.handleActionRequest(0,0);
			assertEquals(IRuntimeContext.RUNTIME_STATUS_SUCCESS, ctx.getStatus());
		} catch (Exception e) {
			e.printStackTrace();
			fail();
		}
	}	
	
}
