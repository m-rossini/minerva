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
 * Created on 13/02/2006
 */

package br.com.auster.minerva.csv.report;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.Map;

import org.apache.log4j.Logger;

import br.com.auster.common.io.MultiFileOutputStream;
import br.com.auster.common.log.LogFactory;
import br.com.auster.minerva.core.ReportException;
import br.com.auster.minerva.spi.ReportRequest;

public class SimpleCSVReport extends AbstractCSVReport {

	
	private static final Logger log = LogFactory.getLogger(SimpleCSVReport.class);

	
	
	public void generate(Connection connection, ReportRequest _request) throws ReportException {
		PreparedStatement st = null;
		ResultSet rs = null;
		MultiFileOutputStream output = null;
		try {
			output = getOutputStream(_request);
			Map parameters = _request.getAttributes();
			st = (PreparedStatement) runQuery(connection, parameters);
			rs = st.executeQuery();
			ResultSetMetaData meta = rs.getMetaData();
			int columnCount = meta.getColumnCount();
			if (columnCount > 0) {
				PrintWriter writer = new PrintWriter(output);
				boolean headerDone = false;
				int recordCount = 0;
				while (rs.next()) {
					if (!headerDone) {
						this.printHeader(meta, columnCount, writer);
						recordCount = 1;
						headerDone = true;
					}
					if ((this.rowLimit > 1) && ((recordCount % this.rowLimit) == 0)) {
						writer.close();
						writer = new PrintWriter(output);
						this.printHeader(meta, columnCount, writer);
						recordCount++;
					}
					this.printRecord(rs, columnCount, writer);
					recordCount++;
				}
				if (recordCount == 0) {
					writer.print(this.emptyReportMessage);
				}
				writer.close();
			} else {
				log.warn("[" + reportName + "] No columns returned from query");
			}
			ZIPMultiFile(_request, output);
			log.info("[" + reportName + "] CSV Report successfully generated.");
		} catch (Exception e) {
			throw new ReportException(e);
		} finally {
			try {
				this.releaseResources(st, rs);
				this.removeTemporaryFiles(output);
			} catch (SQLException sqle) {
				throw new ReportException(sqle);
			} catch (IOException ioe) {
				throw new ReportException(ioe);
			}
		}
	}

	private void printHeader(ResultSetMetaData meta, int count, PrintWriter writer)
	    throws SQLException {
		String name = meta.getColumnLabel(1);
		writer.print(this.textDelimiter);
		writer.print(name);
		writer.print(this.textDelimiter);
		for (int i = 2; i <= count; i++) {
			name = meta.getColumnLabel(i);
			writer.print(this.fieldDelimiter);
			writer.print(this.textDelimiter);
			writer.print(name);
			writer.print(this.textDelimiter);
		}
		writer.println();
	}

	private void printRecord(ResultSet rs, int count, PrintWriter writer) throws SQLException {
		Object value = rs.getObject(1);
		if (value instanceof String) {
			writer.print(this.textDelimiter);
			writer.print(value.toString());
			writer.print(this.textDelimiter);
		} else {
			writer.print(value.toString());
		}
		for (int i = 2; i <= count; i++) {
			value = rs.getObject(i);
			writer.print(this.fieldDelimiter);
			if (value instanceof String) {
				writer.print(this.textDelimiter);
				writer.print(value.toString());
				writer.print(this.textDelimiter);
			} else {
				writer.print(value.toString());
			}
		}
		writer.println();
	}

	public Object clone() {
		SimpleCSVReport cloned = new SimpleCSVReport();
		cloned.emptyReportMessage=this.emptyReportMessage;
		cloned.fieldDelimiter=this.fieldDelimiter;
		cloned.filePattern=this.filePattern;
		cloned.jdbcQuery=this.jdbcQuery;
		cloned.rowLimit=this.rowLimit;
		cloned.textDelimiter=this.textDelimiter;
		cloned.outputFilename=this.outputFilename;
		cloned.paramNames=this.paramNames;
		cloned.reportName=this.reportName;
		cloned.reportQuery=this.reportQuery;		
		return cloned;
	}
}
