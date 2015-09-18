/*
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
 * Created on Sep 15, 2005
 */
package br.com.auster.minerva.core;

/**
 * @author framos
 * @version $Id$
 */
public abstract class MinervaConfigurationConstants {

    
	/*
	 * Command-line Arguments
	 */
	public static final String MINERVA_CMDLINE_OPTS_CONFIGFILE = "configuration";
	public static final String MINERVA_CMDLINE_OPTS_CONFIGFILE_MNEMONIC = "c";
	
	public static final String MINERVA_CMDLINE_OPTS_ENCRYPTED = "security-flag";
	public static final String MINERVA_CMDLINE_OPTS_ENCRYPTED_MNEMONIC = "s";
	
	/*
	 * Configuration Tokens 
	 */
    public static final String MINERVA_CONFIGURATION_NAMESPACE = "http://www.auster.com.br/minerva";
    
    public static final String MINERVA_LISTENER_CONFIGURATION_ELEMENT = "listeners";
    public static final String MINERVA_DISPATCHER_CONFIGURATION_ELEMENT = "dispatchers";
    public static final String MINERVA_THREADCOUNT_ATTRIBUTE = "count";

    public static final String MINERVA_MANAGER_CONFIGURATION_ELEMENT = "manager"; 
    public static final String MINERVA_MANAGER_FACTORY_CONFIGURATION_ELEMENT = "factory";
    public static final String MINERVA_CONFIGURATION_SUBELEMENT = "configuration";
    public static final String MINERVA_NAME_ATTRIBUTE = "name";
    public static final String MINERVA_CLASSNAME_ATTRIBUTE = "class";
    
    
}
