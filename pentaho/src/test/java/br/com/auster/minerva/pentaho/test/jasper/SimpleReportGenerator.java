package br.com.auster.minerva.pentaho.test.jasper;

import java.io.IOException;
import java.io.OutputStream;
import java.sql.Connection;
import java.util.Map;

import org.w3c.dom.Element;

import br.com.auster.minerva.core.ReportException;
import br.com.auster.minerva.pentaho.jasper.JasperReportGenerator;

public class SimpleReportGenerator implements JasperReportGenerator {

	public void configure(Element config) throws ReportException {}

	public void generateReport(Connection connection, Map parameters, OutputStream output) throws ReportException {
		try {
			String text = (String) parameters.get("text");
			output.write(text.getBytes());
		} catch (IOException ioe) {
			throw new ReportException(ioe);
		}
	}
	
}