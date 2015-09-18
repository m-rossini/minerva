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
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.log4j.Logger;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import br.com.auster.common.io.MultiFileOutputStream;
import br.com.auster.common.log.LogFactory;
import br.com.auster.common.xml.DOMUtils;
import br.com.auster.minerva.billcheckout.report.BillcheckoutRequest;
import br.com.auster.minerva.core.MinervaException;
import br.com.auster.minerva.core.Report;
import br.com.auster.minerva.core.ReportException;
import br.com.auster.minerva.core.ReportInitializationException;
import br.com.auster.minerva.spi.ReportRequest;

public class RowFlipCSVReport extends AbstractCSVReport {

	private static final Logger  log = LogFactory.getLogger(RowFlipCSVReport.class);

	public static final String	ROW_FLIP_ELMT = "row-flip";
	public static final String	KEYS_ELMT = "keys";
	public static final String	COLUMNS_ELMT = "columns";
	public static final String	FIELD_ELMT = "field";
	public static final String	NAME_ATTR = "name";
	public static final String  HIDE_ATTR = "hide";
	public static final String	HEADER_ATTR	= "header";
	public static final String	VALUE_ATTR = "value";

	private List<String> keyList;
	private List<String> hideKeyList;
	private Map<String, String>	colList;

	private String oneTimeHeader;
	protected Element configuration;
	/**
	 * @see br.com.auster.billcheckout.report.AbstractCSVReportGenerator#configure(org.w3c.dom.Element)
	 */
	public void configure(Element config) throws ReportInitializationException {
		this.configuration=config;
		String filename = DOMUtils.getAttribute(config, CONFIG_FILENAME_ELEM, false);
		if ((filename != null) && (filename.trim().length() > 0)) {
			try {
				config = DOMUtils.openDocument(filename, false);
			} catch (Exception e) {
				throw new ReportInitializationException(e);
			}
		}

		super.configure(config);
		Element row = DOMUtils.getElement(config, ROW_FLIP_ELMT, false);
		if (row != null) {
			Element keysElement = DOMUtils.getElement(row, KEYS_ELMT, true);
			NodeList keyFldfElements = DOMUtils.getElements(keysElement, FIELD_ELMT);
			int qtd = keyFldfElements.getLength();
			this.keyList = new LinkedList<String>();
			this.hideKeyList = new LinkedList<String>();
			for (int i = 0; i < qtd; i++) {
				Element node = (Element) keyFldfElements.item(i);
				String name = DOMUtils.getAttribute(node, NAME_ATTR, true);
				this.keyList.add(name);
				boolean hideThisCol = DOMUtils.getBooleanAttribute(node, HIDE_ATTR, false);
				if (hideThisCol) {
					this.hideKeyList.add(name);
				}
				log.debug("Getting Key Column named:" + name + "; will be hided? " + hideThisCol);
			}
			Element colsElement = DOMUtils.getElement(row, COLUMNS_ELMT, true);
			NodeList colFldElements = DOMUtils.getElements(colsElement, FIELD_ELMT);
			qtd = colFldElements.getLength();
			this.colList = new LinkedHashMap<String, String>();
			for (int i = 0; i < qtd; i++) {
				Element node = (Element) colFldElements.item(i);
				String header = DOMUtils.getAttribute(node, HEADER_ATTR, true);
				String value = DOMUtils.getAttribute(node, VALUE_ATTR, true);
				this.colList.put(header, value);
				log.debug("Getting Flip Data Configuration. Header will be Column:" + header + " and data for this will be Column:" + value);
			}
		}
	}

	public void generate(Connection _connection, ReportRequest _request) throws ReportException {
		PreparedStatement st = null;
		ResultSet rs = null;
		MultiFileOutputStream output = null;
		try {
			output = getOutputStream(_request);
			Map parameters = _request.getAttributes();
			log.trace("Request Parameter Map:" + parameters);
			st = (PreparedStatement) runQuery(_connection, parameters);
			rs = st.executeQuery();
			ResultSetMetaData meta = rs.getMetaData();
			int columnCount = meta.getColumnCount();
			if (columnCount > 0) {
				PrintWriter writer = new PrintWriter(output);
				int recordCount = 0;

				boolean headerPrinted = false;
				StringBuffer record = new StringBuffer();
				StringBuffer keyHeader = new StringBuffer();
				StringBuffer colHeader = new StringBuffer();

				StringBuffer previousRS = new StringBuffer();
				StringBuffer currentRS = new StringBuffer();
				int rowCount = 0;
				//Entra no loop de acesso ao banco.
				//Para cada Linha a ser lida do banco, será montado de acordo com a parametrizacao (Ver configure)
				// uma String que é a chave de quebra do relatorio.
				// A Chave de quebra do relátorio é montada em função do resultado da query.
				//Para o Header é utilizado o meta data da consulta.
				//	Para os atributos definidos como chave o Header é montado uma unica vez, já para a parte variavel,
				//	o header é construido conforme cada rs.next() é realizado.
				//
				while (rs.next()) {
					rowCount++;
					currentRS.setLength(0);
					// Build The Key
					for (Iterator itr = this.keyList.iterator(); itr.hasNext();) {
						currentRS.append(rs.getObject((String) itr.next()));
						currentRS.append('|');
					}

					if (currentRS.toString().equals(previousRS.toString())) {
						//Vai apenas montar a parte variavel do header e o registro de dados
						// DOES NOTHING
					} else {
						//Houve quebra de chave.
						previousRS = new StringBuffer(currentRS);
						//Se montou um registro inteiro..
						if ((recordCount > 0) ) {
							if (!headerPrinted) {
								//E ainda não imprimiu o header...Imprime o Header
								writer.print(keyHeader.toString() + colHeader.toString());
								writer.println();
								// Não vamos contar mais o header como uma linha
								//recordCount++;
								headerPrinted = true;
							}
							//Já imprimiu o header (Nesse passo ou algum passo anterior...) Imprime os dados e inicializa os buffers
							writer.print(record.toString());
							writer.println();
							record.setLength(0);
							keyHeader.setLength(0);
							colHeader.setLength(0);
							//Se existe limite de linhas por aquivo....
							if ((this.rowLimit > 1) && ((recordCount % this.rowLimit) == 0)) {
								//Existe e chegou no limite....Fecha o Stream e abre o proximo.....
								//Adicionalmente indica que precisa imprimir o Header.
								writer.close();
								writer = new PrintWriter(output);
								headerPrinted = false;
							}
						}
						//Começa a montar um registro..... Primeiro a Parte Fixa do Header
						recordCount++;
						for (Iterator itr = this.keyList.iterator(); itr.hasNext();) {
							String colName = (String) itr.next();
							Object colValue = rs.getObject(colName);
							// this enables the key-fields hide feature
							if (! this.hideKeyList.contains(colName)) {
								keyHeader.append(colName);
								keyHeader.append(this.fieldDelimiter);
								//Print Field, apenas adiciona no StringBuffer..nada é gerado no output
								printField(colValue, record, true);
							}
						}
					}
					for (Iterator itr = this.colList.entrySet().iterator(); itr.hasNext();) {
						Map.Entry entry = (Entry) itr.next();
						//Monta o Detalhe a ser impresso.
						printField(rs.getObject((String) entry.getValue()), record, true);
						log.debug("Object Header=>ColumnName:" + entry.getKey() + ".ColumnNameValue:" + rs.getObject((String) entry.getKey()));
						//Monta a parte variável do Header.
						Object headerInfo = rs.getObject((String) entry.getKey());
						if (headerInfo != null) {
							colHeader.append(headerInfo);
						}
						colHeader.append(this.fieldDelimiter);
					}
					//Salva o header para usar CASO seja necessário imprimir o Header fora deste While (TKT#11)
					oneTimeHeader = keyHeader.toString() + colHeader.toString();
					log.debug("Current Counters. Row:" + rowCount + ".Record:" + recordCount);
				}
				log.debug("RecordCount " + recordCount);
				((BillcheckoutRequest)_request).setRecordCount(recordCount);
				//Após o while....Verifica se leu registros do banco.
				if (recordCount==0) {
					//Não leu nenhum....Monta a saida com mensagem de Not Data Found.
					record.append(this.emptyReportMessage);
				} else if (!headerPrinted) {
					//Existe registro no banco e não imprimiu Header.Imprime.
					writer.print(oneTimeHeader);
					writer.println();
				}
				//Imprime Ultima linha de dados ou a de mensagem No Data Found
				writer.print(record.toString());
				writer.close();
			} else {
				log.warn("[" + reportName + "] No columns returned from query");
			}
			//Cria arquivo ZIP com relatorios.....
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

	/**
	 * TODO why this methods was overriden, and what's the new expected behavior.
	 *
	 * @return
	 * @throws CloneNotSupportedException
	 * @see java.lang.Object#clone()
	 */
	@Override
	public Object clone()  {
		try {
			Report cloned = this.getClass().newInstance();
			cloned.configure(configuration);
			return cloned;
		} catch (ReportInitializationException e) {
			MinervaException ex = new MinervaException("Unable to configure cloned report.");
			ex.initCause(e);
			throw ex;
		} catch (InstantiationException e) {
			MinervaException ex = new MinervaException("Unable to configure cloned report.");
			ex.initCause(e);
			throw ex;
		} catch (IllegalAccessException e) {
			MinervaException ex = new MinervaException("Unable to configure cloned report.");
			ex.initCause(e);
			throw ex;
		}
	}

	private void printField(Object value, StringBuffer writer, boolean sep) throws SQLException {
		log.trace("Printing field:" + value);
		if (value != null) {
			if (value instanceof String) {
				writer.append(this.textDelimiter);
				writer.append(value.toString());
				writer.append(this.textDelimiter);
			} else {
				writer.append(value.toString());
			}
		}
		if (sep) {
			log.trace("Printing field delimiter.Delimiter:" + this.fieldDelimiter);
			writer.append(this.fieldDelimiter);
		}
	}
}
