/*
 * Copyright (c) 2004-2006 Auster Solutions do Brasil. All Rights Reserved.
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
 * Created on 18/10/2006
 */
package br.com.auster.minerva.billcheckout.report;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import org.apache.log4j.Logger;
import org.w3c.dom.Element;

import br.com.auster.common.sql.SQLConnectionManager;
import br.com.auster.common.xml.DOMUtils;
import br.com.auster.minerva.core.MinervaManager;
import br.com.auster.minerva.core.ReportInitializationException;
import br.com.auster.minerva.core.RequestDispatcher;
import br.com.auster.minerva.spi.ReportRequest;

/**
 * @author framos
 * @version $Id$
 */
public class DatabaseDispatcher implements RequestDispatcher {



	protected MinervaManager manager;
	protected String poolName;
	protected String descriptionPattern;


	private static final Logger log = Logger.getLogger(DatabaseDispatcher.class);

	protected static final String INSERT_STATEMENT =
		"INSERT INTO web_bundlefile (file_id, web_request_id, filename, create_datetime, message, record_count) "+
		" VALUES (bundlefile_sequence.nextval, ? , ?, sysdate, ?, ?)";


	/**
	 * @see br.com.auster.minerva.core.RequestDispatcher#configure(org.w3c.dom.Element)
	 */
	public void configure(Element _configuration) throws ReportInitializationException {
		this.poolName = DOMUtils.getAttribute(_configuration, "pool-name", true);
		this.descriptionPattern = DOMUtils.getAttribute(_configuration, "description-prefix", false);
	}

	/**
	 * @see br.com.auster.minerva.core.RequestDispatcher#dispatch()
	 */
	public void dispatch() {
		while (true) {
			_dispatch(this.manager.nextFinishedRequest());
		}
	}

	protected void _dispatch(ReportRequest _request) {
		Connection conn = null;
		PreparedStatement stmt = null;
		if (! (_request instanceof BillcheckoutRequest)) {
			throw new IllegalArgumentException("Cannot dispatch a non-Billcheckout request");
		}
		BillcheckoutRequest request = (BillcheckoutRequest) _request;
		try {
			SQLConnectionManager sqlManager = SQLConnectionManager.getInstance(this.poolName);
			conn = sqlManager.getConnection();
			stmt = conn.prepareStatement(INSERT_STATEMENT);
			stmt.setLong(1, Long.parseLong(request.getTransactionId()));
			stmt.setString(2, request.getGeneratedFile());
			stmt.setString(3, this.descriptionPattern + " "+ request.getRuleName());
			stmt.setString(4, request.getRecordCount());
			int ok = stmt.executeUpdate();
			log.info("Report dispatched with success ? " + (ok == 1));
		} catch (Exception e) {
			log.error("Error dispatching generated report", e);
		} finally {
			try {
				if (stmt != null) { stmt.close(); }
				if (conn != null) { conn.close(); }
			} catch (SQLException sqle) {
				log.error("Could not close database connection", sqle);
			}
		}
	}

	/**
	 * @see br.com.auster.minerva.core.RequestDispatcher#setManager(br.com.auster.minerva.core.MinervaManager)
	 */
	public void setManager(MinervaManager _manager) {
		this.manager = _manager;
	}

	/**
	 * @see br.com.auster.minerva.core.RequestDispatcher#stop()
	 */
	public void stop() {
		log.info("Database dispatcher closed.");
	}

}
