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
 * Created on 19/10/2006
 */
package br.com.auster.minerva.billcheckout;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.LinkedHashMap;
import java.util.Map;

import javax.naming.NamingException;

import org.apache.log4j.Logger;

import br.com.auster.common.sql.SQLConnectionManager;
import br.com.auster.minerva.core.ReportException;

/**
 * @author framos
 * @version $Id$
 */
public class BillcheckoutMinervaManagerBase extends BillcheckoutMinervaManager {
	private static final Logger log = Logger.getLogger(BillcheckoutMinervaManagerBase.class);
		
	public static final String REPORT_NAME_STMT = "report-list-query";

	/**
	 * @see br.com.auster.minerva.billcheckout.BillcheckoutMinervaManager#getRuleList()
	 */
	public Map<String, String> getRuleList() {
		Connection conn = null;
		Statement stmt = null;
		ResultSet rset = null;
		Map<String, String> ids = new LinkedHashMap<String, String>();
		try {
			conn = SQLConnectionManager.getInstance(this.poolName).getConnection();
			stmt = conn.createStatement();
			rset = stmt.executeQuery(getQuery());
			while (rset.next()) {
				String ruleCode = rset.getString(1);
				String ruleName = rset.getString(2);
				if ((ruleName == null) && (ruleName.trim().length() <= 0)) {
					ruleName = ruleCode;
				}
				ids.put(ruleCode, ruleName);
			}
			log.trace("Rule List IDS:" + ids);
		} catch (Exception e) {
			throw new ReportException(e);
		} finally {
    		try {
    			if (rset != null) { rset.close(); }
    			if (stmt != null) { stmt.close(); }
    			if (conn != null) { conn.close(); }
    		} catch (SQLException sqle) {
    			throw new ReportException(sqle);
    		}
		}
		return ids;
	} 
	

	protected String getQuery() {
		String sql = null;
		try {
	    sql = SQLConnectionManager.getInstance(poolName).getStatement(REPORT_NAME_STMT).getStatementText();
	    log.trace("SQL:" + sql);
    } catch (NamingException e) {
    	log.fatal("Unable to get statement named:" + REPORT_NAME_STMT + " in order to build reports.");
	    e.printStackTrace();
    }
		return sql;
	}
}
