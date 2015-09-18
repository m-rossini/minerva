/*
 * Copyright (c) 2004-2005 Auster Solutions do Brasil. All Rights Reserved.
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
 * Created on Sep 14, 2005
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
 * @author framos
 * @version $Id$
 */
public class Bootstrap {

	
	
	// ----------------------------
	// Class constants
	// ----------------------------
    
    private static final Log log = LogFactory.getLog(Bootstrap.class);
    
    

	// ----------------------------
	// MAIN
	// ----------------------------
    
	public static void main(String[] args) {
		ReportServer rs = new ReportServer();
		rs.setArguments(args);
		try {
			rs.process(rs.getParsedCommandLine());
		} catch (IllegalArgumentException e) {
			shutDownReportBootStrap(1, e);
		} catch (SecurityException e) {
			shutDownReportBootStrap(2, e);
		} catch (ParserConfigurationException e) {
			shutDownReportBootStrap(3, e);
		} catch (SAXException e) {
			shutDownReportBootStrap(4, e);
		} catch (IOException e) {
			shutDownReportBootStrap(5, e);
		} catch (GeneralSecurityException e) {
			shutDownReportBootStrap(6, e);
		} catch (InstantiationException e) {
			shutDownReportBootStrap(7, e);
		} catch (IllegalAccessException e) {
			shutDownReportBootStrap(8, e);
		} catch (ClassNotFoundException e) {
			shutDownReportBootStrap(9, e);
		} catch (ReportInitializationException e) {
			shutDownReportBootStrap(10, e);
		} catch (NoSuchMethodException e) {
			shutDownReportBootStrap(11, e);
		} catch (InvocationTargetException e) {
			shutDownReportBootStrap(12, e);
		}		
		
	}

	protected static void shutDownReportBootStrap(long code, Throwable t) {
		if (t != null) {
			log.fatal(t);
			System.err.println(t);
		}
		System.out.println("Leaving report bootstrap with code:" + code);
		System.exit((int) code);
	}
	// TODO
    // add shutdown method to be called using JMX
    // add restart config. method to be called using JMX
	//     - report factory mapping
    //     - thread count for dispatchers / listeners
}
