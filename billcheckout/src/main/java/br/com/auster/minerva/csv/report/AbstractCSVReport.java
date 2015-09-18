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

import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.w3c.dom.Element;

import br.com.auster.common.io.CompressUtils;
import br.com.auster.common.io.MultiFileOutputStream;
import br.com.auster.common.log.LogFactory;
import br.com.auster.common.xml.DOMUtils;
import br.com.auster.minerva.core.ReportException;
import br.com.auster.minerva.core.ReportInitializationException;
import br.com.auster.minerva.spi.ReportRequest;

/**
 * 
 * @author mtengelm
 * @version $Id: CsvQueryReportGenerator.java 364 2006-02-13 18:28:45Z mtengelm $
 */

public abstract class AbstractCSVReport implements DatabaseReport {

	public static final String	CONFIG_FILENAME_ELEM									= "filename";

	public static final String	CONFIG_REPORT_ELEM										= "report-configuration";
	public static final String	CONFIG_RE_ATTR_NAME										= "name";
	public static final String	CONFIG_RE_QUERY												= "report-query";
	public static final String	CONFIG_RE_DELIMITER										= "delimiter";
	public static final String	CONFIG_RE_ATTR_FIELD									= "field";
	public static final String	CONFIG_RE_ATTR_TEXT										= "text";
	public static final String	CONFIG_RE_ROW_LIMIT										= "row-limit";
	public static final String	CONFIG_RE_ATTR_VALUE									= "value";
	public static final String	CONFIG_RE_GENERATE_EMPTY_MESSAGE_ATTR	= "empty-message";
	public static final String	CONFIG_RE_FILEPATTERN									= "file-pattern";
	public static final String	CONFIR_RE_FILE_OPTIOS_ELT							= "file-options";
	public static final String	CONFIR_RE_FILE_NORMAL_EXT_ATTR				= "file-extension";

	private static final Logger	log																		= LogFactory
																																				.getLogger(AbstractCSVReport.class);

	private static final String	DEFAULT_EXTENSION											= "csv";

	protected String						reportName;
	protected String						reportQuery;
	protected String						jdbcQuery;
	protected List							paramNames;
	protected String						fieldDelimiter;
	protected String						textDelimiter;
	protected long							rowLimit;
	protected String						emptyReportMessage;
	protected String						filePattern;

	protected String						outputFilename;

	protected String							extension;
	
	public void configure(Element config) throws ReportInitializationException {

		String filename = DOMUtils
				.getAttribute(config, CONFIG_FILENAME_ELEM, false);
		if ((filename != null) && (filename.trim().length() > 0)) {
			try {
				config = DOMUtils.openDocument(filename, false);
			} catch (Exception e) {
				throw new ReportInitializationException(e);
			}
		}

		log.info("Configuring CSV Query Report Generator.");
		if (!CONFIG_REPORT_ELEM.equals(config.getLocalName())) {
			config = DOMUtils.getElement(config, CONFIG_REPORT_ELEM, true);
		}
		// Report name
		reportName = DOMUtils.getAttribute(config, CONFIG_RE_ATTR_NAME, true);
		// Report query
		Element query = DOMUtils.getElement(config, CONFIG_RE_QUERY, true);
		reportQuery = DOMUtils.getText(query).toString();
		QueryProcessor processor = new QueryProcessor(reportQuery);
		jdbcQuery = processor.extractJDBCQuery();
		paramNames = processor.extractParameters();
		if (paramNames.size() > 0) {
			for (Iterator i = paramNames.iterator(); i.hasNext();) {
				log.info("[" + reportName + "] New report parameter found: ["
						+ (String) i.next() + "]");
			}
		} else {
			log.info("[" + reportName + "] No parameters found!");
		}
		// Delimiters
		Element delimiters = DOMUtils.getElement(config, CONFIG_RE_DELIMITER, true);
		fieldDelimiter = DOMUtils.getAttribute(delimiters, CONFIG_RE_ATTR_FIELD,
				true);
		textDelimiter = DOMUtils
				.getAttribute(delimiters, CONFIG_RE_ATTR_TEXT, true);
		// Row limit
		Element limit = DOMUtils.getElement(config, CONFIG_RE_ROW_LIMIT, true);
		rowLimit = DOMUtils.getIntAttribute(limit, CONFIG_RE_ATTR_VALUE, true);
		// Decide how to handle empty reports.
		emptyReportMessage = DOMUtils.getAttribute(config,
				CONFIG_RE_GENERATE_EMPTY_MESSAGE_ATTR, false);
		if (null == emptyReportMessage || emptyReportMessage.length() == 0) {
			emptyReportMessage = "Sem dados para relatório";
		}
		// output file pattern
		Element filePatternCfg = DOMUtils.getElement(config, CONFIG_RE_FILEPATTERN,
				true);
		this.filePattern = DOMUtils.getText(filePatternCfg).toString();

		// output file extension
		this.extension = DEFAULT_EXTENSION;
		Element fileOptElt = DOMUtils.getElement(config, CONFIR_RE_FILE_OPTIOS_ELT,
				false);
		if (fileOptElt != null) {
			String ext = DOMUtils.getAttribute(fileOptElt,
					CONFIR_RE_FILE_NORMAL_EXT_ATTR, false);
			if ((ext != null) && (ext.trim().length() > 0)) {
				this.extension = ext;
			}
		}

		log.info("[" + reportName + "] CSV Query Report successfully configured!");
	}

	public final void generate(ReportRequest _request)
			throws br.com.auster.minerva.core.ReportException {
		throw new IllegalStateException(
				"should call this method specifying a database connection");
	}

	public Object clone() {		
		Object obj = this.clone();
		log.trace("Cloning Object. CloneClass:" + obj.getClass().getName());
		return obj;
	}

	public String getFilenamePattern(ReportRequest _request) {
		return this.filePattern + "." + _request.getTransactionId();
	}

	public String getGeneratedFilename() {
		return this.outputFilename;
	}

	// Protected Methods

	protected void ZIPMultiFile(ReportRequest _request, MultiFileOutputStream _out)
			throws IOException {
		this.outputFilename = getFilenamePattern(_request) + ".zip";
		log.trace("Output ZIP File name as String:" + outputFilename);
		CompressUtils.createZIPBundle(_out.getGeneratedFilesList(),
				this.outputFilename);
	}

	protected void removeTemporaryFiles(MultiFileOutputStream _out)
			throws IOException {
		if ((_out == null) || (_out.getGeneratedFilesList() == null)) {
			return;
		}
		for (Iterator it = _out.getGeneratedFilesList().iterator(); it.hasNext();) {
			File f = new File((String) it.next());
			f.delete();
		}
	}

	protected MultiFileOutputStream getOutputStream(ReportRequest _request)
			throws IOException {
		String filename = getFilenamePattern(_request) + ".{000}." + this.extension;
		log.trace("File name as String:" + filename);
		return new MultiFileOutputStream(filename);
	}

	protected Statement runQuery(Connection connection, Map parameters)
			throws SQLException, ReportException {
		PreparedStatement st = null;
		log.info("[" + reportName + "] Preparing report query.");
		// Running SQL query
		st = connection.prepareStatement(jdbcQuery);
		int j = 1;
		for (Iterator i = this.paramNames.iterator(); i.hasNext(); j++) {
			String paramName = (String) i.next();
			String paramValue = (String) parameters.get(paramName);
			if (paramValue == null) {
				throw new ReportException("[" + reportName
						+ "] Missing mandatory parameter [" + paramName + "]");
			}
			st.setString(j, paramValue);
			log.info("[" + reportName + "] ==> Parameter set: [" + j + ":"
					+ paramName + "]=[" + paramValue + "]");
		}
		log.info("[" + reportName + "] Running report query.");
		return st;
	}

	protected void releaseResources(PreparedStatement _st, ResultSet _rs)
			throws SQLException {
		log.trace("Releasing database resources for report.");
		if (_rs != null) {
			_rs.close();
		}
		if (_st != null) {
			_st.close();
		}
	}
}
