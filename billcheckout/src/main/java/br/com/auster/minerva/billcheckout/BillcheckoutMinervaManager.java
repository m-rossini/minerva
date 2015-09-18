/*
 * Copyright (c) 2004-2006 Auster Solutions. All Rights Reserved.
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
 * Created on 16/10/2006
 */
package br.com.auster.minerva.billcheckout;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Date;
import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;

import javax.naming.NamingException;

import org.apache.log4j.Logger;
import org.w3c.dom.Element;

import br.com.auster.common.sql.SQLConnectionManager;
import br.com.auster.common.util.I18n;
import br.com.auster.common.xml.DOMUtils;
import br.com.auster.minerva.billcheckout.report.BillcheckoutRequest;
import br.com.auster.minerva.core.MinervaConfigurationConstants;
import br.com.auster.minerva.core.ReportException;
import br.com.auster.minerva.core.ReportInitializationException;
import br.com.auster.minerva.core.impl.MinervaManagerBase;
import br.com.auster.minerva.core.impl.ReportFactoryBase;
import br.com.auster.minerva.csv.report.DatabaseReport;
import br.com.auster.minerva.spi.ReportRequest;

/**
 * NOTE: This manager only works with implementations of the
 * {@link br.com.auster.minerva.billcheckout.report.BillcheckoutReport}
 * 
 * @author framos
 * @version $Id$
 */
public abstract class BillcheckoutMinervaManager extends MinervaManagerBase {

	public static final String							MINERVA_MANAGER_THREADPOOL_SIZE_ATTRIBUTE	= "pool-size";

	public static final String							CONFIG_POOLNAME														= "pool-name";

	private static final Logger							log																				= Logger
																																												.getLogger(BillcheckoutMinervaManager.class);
	private static final I18n								i18n																			= I18n
																																												.getInstance(ReportFactoryBase.class);

	// ----------------------------
	// Instance variables
	// ----------------------------

	protected ExecutorService								threadPool;

	protected String												poolName;

	protected BlockingQueue<ReportRequest>	finishedQueue;

	public BillcheckoutMinervaManager() {
		this.finishedQueue = new LinkedBlockingQueue<ReportRequest>();
	}

	public abstract Map<String, String> getRuleList();

	public boolean enqueueRequest(ReportRequest _request) {
		Map<String, String> ruleIdList = getRuleList();
		try {
			Date currentDate = new Date();
			_request.setGenerationTime(currentDate.getTime());

			boolean doNotCleanUp = false;
			if (_request.getAttributes() != null) {
				doNotCleanUp = _request.getAttributes().containsKey(
						ReportRequest.ATTR_BYPASS_CLEANUP_PREVIOUS);
			}

			if (!doNotCleanUp) {
				clearPreviousExecutions(_request.getTransactionId());
			}

			for (String ruleId : ruleIdList.keySet()) {
				// preparing request to generate reports
				BillcheckoutRequest clonedRequest = cloneRequest(_request);
				if (clonedRequest == null) {
					return false;
				}

				clonedRequest.setRuleId(ruleId);
				clonedRequest.setRuleName(ruleIdList.get(ruleId));
				// running report
				DatabaseReport r = (DatabaseReport) factory.getReport(_request);
				if (r==null) {
					log.error("Report NOT FOUND for name:" + _request.getName() + ".Please check configuration file at manager/factory/configuration/report");
					return false;
				}
				log.trace("Gotten report from factory:" + factory.getClass().getName() +
						" for request:" + _request + ".Report:" + r);
				
				log.debug("Enqueued report for report " + clonedRequest.getName());

				threadPool.execute(new ReportThread(r, clonedRequest, this.poolName,
						this.finishedQueue));
			}
		} catch (Exception e) {
			log.error("Error running report", e);
			throw new ReportException(e);
		}
		return true;
	}

	/**
	 * @see br.com.auster.minerva.core.MinervaManager#nextFinishedRequest()
	 */
	public ReportRequest nextFinishedRequest() {
		try {
			return this.finishedQueue.take();
		} catch (InterruptedException ie) {
			log.error("Error taking finished report info", ie);
		}
		return null;
	}

	public void configure(Element _configuration)
			throws ReportInitializationException {
		super.configure(_configuration);
		// database pool name
		Element cfg = DOMUtils.getElement(_configuration,
				MinervaConfigurationConstants.MINERVA_CONFIGURATION_SUBELEMENT, true);
		this.poolName = DOMUtils.getAttribute(cfg, CONFIG_POOLNAME, true);

		try {
			SQLConnectionManager.init(cfg);
		} catch (Exception e) {
			throw new ReportInitializationException(e);
		}

		int poolSize = DOMUtils.getIntAttribute(cfg,
				MINERVA_MANAGER_THREADPOOL_SIZE_ATTRIBUTE, false);
		if (poolSize <= 0) {
			poolSize = 1;
		}
		log.info(i18n.getString("manager.threaded.poolSizeSet", String
				.valueOf(poolSize)));
		this.threadPool = Executors.newFixedThreadPool(poolSize);

	}

	protected BillcheckoutRequest cloneRequest(ReportRequest _source) {
		if (_source == null) {
			return null;
		}
		try {
			BillcheckoutRequest clonedReport = new BillcheckoutRequest(_source
					.getName());
			clonedReport.getAttributes().putAll(_source.getAttributes());
			clonedReport.setTransactionId(_source.getTransactionId());
			clonedReport.setGenerationTime(_source.getGenerationTime());
			return clonedReport;
		} catch (Exception e) {
			log.error("Could not clone report request", e);
		}
		return null;
	}

	protected void clearPreviousExecutions(String _transactionId) {
		Connection conn = null;
		Statement stmt = null;
		try {
			conn = SQLConnectionManager.getInstance(this.poolName).getConnection();
			stmt = conn.createStatement();
			int counter = stmt
					.executeUpdate("delete from web_bundlefile where web_request_id = "
							+ _transactionId);
			if (counter > 0) {
				log
						.warn("-------------------------------------------------------------------------------------");
				log.warn("Removed previous reports to transaction " + _transactionId
						+ " : counter =" + counter);
				log
						.warn("-------------------------------------------------------------------------------------");
			}
			if (!conn.getAutoCommit()) {
				conn.commit();
			}
		} catch (Exception e) {
			throw new ReportException(e);
		} finally {
			try {
				if (stmt != null) {
					stmt.close();
				}
				if (conn != null) {
					conn.close();
				}
			} catch (SQLException sqle) {
				throw new ReportException(sqle);
			}
		}
	}

	// ----------------------------
	// Inner classes
	// ----------------------------

	private class ReportThread implements Runnable {

		private DatabaseReport								report;
		private BillcheckoutRequest						request;
		private String												poolName;

		private BlockingQueue<ReportRequest>	finishedQueue;

		ReportThread(DatabaseReport _report, BillcheckoutRequest _request,
				String _poolName, BlockingQueue<ReportRequest> _finishedQueue) {
			log.trace("Creating a new Report Thread.Report:" + report + ".PoolName:" + _poolName + ".BloquingQueue:" + _finishedQueue );
			this.report = _report;
			this.request = _request;
			this.poolName = _poolName;
			this.finishedQueue = _finishedQueue;
		}

		/**
		 * @see java.lang.Runnable#run()
		 */
		public void run() {

			Connection conn = null;
			try {
				conn = SQLConnectionManager.getInstance(this.poolName).getConnection();
				log.trace("Generating report for request:" + request + ".Report:" + report);
				this.report.generate(conn, request);
				this.request.setGeneratedFile(this.report.getGeneratedFilename());
				this.finishedQueue.put(request);
			} catch (NamingException ne) {
				log.error("Error acessing database", ne);
			} catch (SQLException sqle) {
				log.error("Error acessing database", sqle);
			} catch (InterruptedException ie) {
				log.error("Error putting request into finished queue", ie);
			} finally {
				if (conn != null) {
					try {
						conn.close();
					} catch (SQLException sqle) {
						log.error("Error closing database connection", sqle);
					}
				}
			}
		}
	}

}
