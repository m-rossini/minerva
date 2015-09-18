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
package br.com.auster.minerva.billcheckout.test;

import org.w3c.dom.Element;

import br.com.auster.minerva.core.MinervaManager;
import br.com.auster.minerva.core.ReportInitializationException;
import br.com.auster.minerva.core.RequestListener;
import br.com.auster.minerva.spi.ReportRequest;
import br.com.auster.minerva.spi.ReportRequestBase;

/**
 * @author framos
 * @version $Id$
 */
public class RandomGeneratorListener implements RequestListener {

	private MinervaManager manager;

	
	
	/**
	 * @see br.com.auster.minerva.core.RequestListener#setManager(br.com.auster.minerva.core.MinervaManager)
	 */
	public void setManager(MinervaManager _manager) {
		this.manager = _manager;
	}

	/**
	 * @see br.com.auster.minerva.core.RequestListener#configure(org.w3c.dom.Element)
	 */
	public void configure(Element _configuration) throws ReportInitializationException {
	}

	/**
	 * @see br.com.auster.minerva.core.RequestListener#listen()
	 */
	public void listen() {
		manager.enqueueRequest(createRequest());
	}

	/**
	 * @see br.com.auster.minerva.core.RequestListener#stop()
	 */
	public void stop() {
	}

	
	private ReportRequest createRequest() {
		ReportRequestBase r = new ReportRequestBase("simple-report") { };
		r.setTransactionId("100");
		return r;
	}
}
