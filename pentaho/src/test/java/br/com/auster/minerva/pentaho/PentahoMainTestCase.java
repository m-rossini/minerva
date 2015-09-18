package br.com.auster.minerva.pentaho;

import br.com.auster.minerva.Bootstrap;

public class PentahoMainTestCase {

	
	public static void main(String args[]) {
		(new PentahoMainTestCase()).run();
	}
	
	public void run() {
		
		String args[] = {"-c", "conf/test-configuration.xml"};
		Bootstrap.main(args);
	}

}
