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

import org.w3c.dom.Element;

/**
 * Defines the basic interface for dispatching generated requests. Instances of implementation 
 *	classes of this interface will be created and initialized as the first actions the Bootstrap 
 *	will take.
 *
 * @author framos
 * @version $Id$
 */
public interface RequestDispatcher {

    /**
     * Shares the <code>MinervaManager</code> instance across all threads 
     * 
     * @param _manager the current manager
     */
    public void setManager(MinervaManager _manager);
    
    /**
     * Initializes the dispatcher instace 
     * 
     * @param _configuration the dispatcher configuration, in XML format
     * 
     * @throws ReportInitializationException if, by some reason, the dispatcher could not be initialized
     */
    public void configure(Element _configuration)  throws ReportInitializationException;
    
    /**
     * Dispatches finished requests. This method should be looped as long as the system is
     * 	running.
     */
    public void dispatch();
    
    /**
     * Stop dispatching requests. This might indicate that the system is shutting down.
     */
    public void stop();
    
}
