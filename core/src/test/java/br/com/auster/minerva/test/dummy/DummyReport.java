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
 * Created on 08/12/2005
 */
package br.com.auster.minerva.test.dummy;

import org.w3c.dom.Element;

import br.com.auster.minerva.core.Report;
import br.com.auster.minerva.core.ReportException;
import br.com.auster.minerva.core.ReportInitializationException;
import br.com.auster.minerva.spi.ReportRequest;

/**
 * @author framos
 * @version $Id$
 */
public class DummyReport implements Report {

	/**
	 * @see br.com.auster.minerva.core.Report#generate(br.com.auster.minerva.spi.ReportRequest)
	 */
	public void generate(ReportRequest _request) throws ReportException {
		// just for testing
		System.out.println("generating report... done");
	}

	/**
	 * @see br.com.auster.minerva.core.Report#configure(org.w3c.dom.Element)
	 */
	public void configure(Element _configuration)throws ReportInitializationException {
		// nothing to configure
	}
	
	/**
	 * @see br.com.auster.minerva.core.Report#clone()
	 */
	public Object clone() {
		// dummy reports do not need to share attributes
		return new DummyReport();
	}

}
