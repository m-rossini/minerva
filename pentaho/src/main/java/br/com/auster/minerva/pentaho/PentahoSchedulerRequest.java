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
 * Created on 04/01/2006
 */
package br.com.auster.minerva.pentaho;

import java.util.Map;

import br.com.auster.common.util.I18n;

/**
 * This request class allows for report scheduling, using Pentaho BI scheduler component.
 * <p>
 * When scheduling a job, one of the many <code>schedule()</code> methods must be called. Depending on the method used, the 
 * 	schedule type will be set to <strong><code>{@value #TRIGGERTYPE_CRON}</code></strong> or <strong><code>{@value #TRIGGERTYPE_SIMPLE}</code></strong>. 
 * <p>
 * The job name and trigger name attributes are initialized with pre-defined values, but can be changed prior to sending
 * 	the request. These names might be necessary for future suspend/resume/delete operations.
 * <p>
 * Whenever a <code>schedule()</code> method is called, it resets the variables in order to have, at any given time, only
 * 	the really needed attributes set. So, if you schedule using the cron version, all interval attributes are reset to its 
 *  initial value (in this case, zero). Likewise, when scheduling using intervals, the cron expression is set to <code>null</code>.  
 * <p>
 * Remember that when using the {@link #schedule(int, int)} or {@link #schedule(int, int, String, String)} method, the scheduler runs the
 * 	chosen action immediately and another {@value #INTERVALCOUNT_ATTR} times, each with an interval of {@value #INTERVALTIME_ATTR} seconds. On the
 *  other hand, if you donot expect the action to run right after scheduling it, you must use {@link #schedule(String)} or {@link #schedule(String, String, String)}.
 *  
 *  
 * @author framos
 * @version $Id$
 */
public class PentahoSchedulerRequest extends PentahoReportRequest {

	
	
	// ----------------------------
	// Class constants
	// ----------------------------
	
	public static final I18n i18n = I18n.getInstance(PentahoSchedulerRequest.class);
	
	public static final String DEFAULT_TRIGGERNAME = "a-trigger";
	public static final String DEFAULT_JOBNAME     = "a-job";
	
	public static final String[] JOBACTION_TYPES = { "startJob", "suspendJob", "resumeJob", "deleteJob" };
	public static final int JOBACTION_NOTSET  = -1;
	public static final int JOBACTION_START   =  0;
	public static final int JOBACTION_SUSPEND =  1;
	public static final int JOBACTION_RESUME  =  2;
	public static final int JOBACTION_DELETE  =  3;
	
	public static final String TRIGGERNAME_ATTR    = "triggerName";
	public static final String JOBNAME_ATTR        = "jobName";
	public static final String CRONSTRING_ATTR     = "cronString";
	public static final String INTERVALTIME_ATTR   = "repeatInterval";
	public static final String INTERVALCOUNT_ATTR  = "repeatCount";
	public static final String JOBACTIONTYPE_ATTR  = "jobAction";
	public static final String ACTIONNAME_ATTR     = "action";
	public static final String ACTIONPATHNAME_ATTR = "path";
	public static final String ACTIONSOLUTION_ATTR = "solution";
	public static final String TRIGGERTYPE_ATTR    = "triggerType";
	
	public static final String TRIGGERTYPE_CRON    = "cron";
	public static final String TRIGGERTYPE_SIMPLE  = "simple";
	
	public static final String SCHEDULER_PATH      = "scheduler";
	public static final String SCHEDULER_ACTION    = "scheduler.xaction";

	
	
	// ----------------------------
	// Instance variables
	// ----------------------------
	
	private String cronString;
	private int intervalTime;
	private int intervalCount;
	private String jobName;
	private String triggerName;
	private int actionType;
	private String scheduledAction;
	private String scheduledPath;
	
	
	
	// ----------------------------
	// Constructors
	// ----------------------------
	
	public PentahoSchedulerRequest(String _name) {
		super(SCHEDULER_ACTION);
		this.initRequest();
		this.setScheduledAction(_name);
	}
	
	public PentahoSchedulerRequest(String _solution, String _path, String _name) {
		super(_solution, SCHEDULER_PATH, SCHEDULER_ACTION);
		this.initRequest();
		this.setScheduledAction(_name);
		this.setScheduledActionPath(_path);
	}
	
	
	
	// ----------------------------
	// Public methods
	// ----------------------------
	
	public String getCronString() {
		return this.cronString;		
	}

	public int getIntervalTime() {
		return this.intervalTime;
	}
	
	public int getIntervalCount() {
		return this.intervalCount;
	}
	
	public String getJobName() {
		return this.jobName;
	}
	
	public void setJobName(String _job) {
		this.jobName = _job;
	}
	
	public String getTriggerName() {
		return this.triggerName;
	}
	
	public void setTriggerName(String _name) {
		this.triggerName = _name;
	}

	public String getActionType() {
		if (this.actionType == JOBACTION_NOTSET) {
			throw new IllegalStateException(i18n.getString("request.scheduler.notscheduled"));
		}
		return JOBACTION_TYPES[this.actionType];
	}

	public String getScheduledAction() {
		return this.scheduledAction;
	}

	public void setScheduledAction(String _action) {
		this.scheduledAction = _action;
	}

	public String getScheduledActionPath() {
		return this.scheduledPath;
	}
	
	public void setScheduledActionPath(String _path) {
		this.scheduledPath = _path;
	}
	
	/**
	 * @see br.com.auster.minerva.pentaho.PentahoReportRequest#setReportPath(String)
	 */
	public void setReportPath(String _path) {
		throw new UnsupportedOperationException(i18n.getString("request.scheduler.cannotsetpath"));
	}

	/**
	 * @see br.com.auster.minerva.interfaces.ReportRequest#getAttributes()
	 */
	public Map getAttributes() {
		if ((this.getScheduledActionPath() == null) || (this.getSolution() == null)) {
			throw new IllegalStateException(i18n.getString("request.scheduler.nopathdefined"));
		}
		this.addAttribute(JOBNAME_ATTR, this.getJobName());
		this.addAttribute(TRIGGERNAME_ATTR, this.getTriggerName());
		this.addAttribute(ACTIONNAME_ATTR, this.getScheduledAction());
		this.addAttribute(ACTIONPATHNAME_ATTR, this.getScheduledActionPath());
		this.addAttribute(ACTIONSOLUTION_ATTR, this.getSolution());
		this.addAttribute(JOBACTIONTYPE_ATTR, this.getActionType());
		if (this.isCronTrigger()) {
			this.addAttribute(CRONSTRING_ATTR, this.getCronString());
			this.addAttribute(TRIGGERTYPE_ATTR, TRIGGERTYPE_CRON);
		} else {
			this.addAttribute(INTERVALTIME_ATTR, String.valueOf(this.getIntervalTime()));
			this.addAttribute(INTERVALCOUNT_ATTR, String.valueOf(this.getIntervalCount()));
			this.addAttribute(TRIGGERTYPE_ATTR, TRIGGERTYPE_SIMPLE);
		}
		return super.getAttributes();
	}
	
	// ----------------------------
	// Scheduler-specific methods
	// ----------------------------
	
	public void schedule(String _cronString) {
		this.initInterval();
		this.cronString = _cronString;
		this.actionType = JOBACTION_START;
	}
	
	public void schedule(String _cronString, String _jobName, String _triggerName) {
		this.schedule(_cronString);
		this.setJobName(_jobName);
		this.setTriggerName(_triggerName);
	}

	public void schedule(int _intervalCount, int _intervalTime) {
		this.initCron();
		this.actionType = JOBACTION_START;
		this.intervalCount = _intervalCount;
		this.intervalTime = _intervalTime;
	}

	public void schedule(int _intervalCount, int _intervalTime, String _jobName, String _triggerName) {
		this.schedule(_intervalCount, _intervalTime);
		this.setJobName(_jobName);
		this.setTriggerName(_triggerName);
	}
	
	public boolean isScheduled() {
		return (isCronTrigger() || isSimpleTrigger());
	}
	
	public boolean isCronTrigger() {
		return this.cronString != null;
	}
	
	public boolean isSimpleTrigger() {
		return !isCronTrigger() && (this.intervalCount * this.intervalTime > 0);
	}
	
	
	
	// ----------------------------
	// Private methods
	// ----------------------------
	
	private void initRequest() {
		this.scheduledPath=null;
		this.jobName=DEFAULT_JOBNAME;
		this.triggerName=DEFAULT_TRIGGERNAME;
		this.actionType=JOBACTION_NOTSET;
		this.initCron();
		this.initInterval();
	}

	public void initCron() {
		this.cronString = null;
	}
	
	private void initInterval() {
		this.intervalCount = 0;
		this.intervalTime = 0;
	}
}
