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

import br.com.auster.common.xml.DOMUtils;
import br.com.auster.minerva.core.MinervaManager;
import br.com.auster.minerva.core.ReportInitializationException;
import br.com.auster.minerva.core.RequestListener;
import br.com.auster.minerva.spi.ReportRequestBase;
import br.com.auster.minerva.spi.ReportRequest;

/**
 * @author framos
 * @version $Id$
 */
public class RandomGeneratorListener implements RequestListener {

	private MinervaManager manager;
	private int counter = 1;
	private int waitInterval = 1;
	
	
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
		counter = DOMUtils.getIntAttribute(_configuration, "number-of-requests", true);
		if (counter < 0) { counter = 1; }
		waitInterval = DOMUtils.getIntAttribute(_configuration, "wait-interval", true);
		if (waitInterval < 1) { waitInterval = 1; }
	}

	/**
	 * @see br.com.auster.minerva.core.RequestListener#listen()
	 */
	public void listen() {
		try {
			for (int i=0; i < counter; i++) {
				Thread.sleep(waitInterval*1000);
				manager.enqueueRequest(createRequest());
			}
		} catch (InterruptedException ie) {
			ie.printStackTrace();
		}
	}

	/**
	 * @see br.com.auster.minerva.core.RequestListener#stop()
	 */
	public void stop() {
	}

	
	private ReportRequest createRequest() {
		return new ReportRequestBase("dummy-report") { };
	}
}
