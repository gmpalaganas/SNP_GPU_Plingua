package org.gcn.plinguacore.simulator;

import java.io.PrintStream;
import java.util.Date;
import java.util.Iterator;

import org.gcn.plinguacore.util.PlinguaCoreException;
import org.gcn.plinguacore.util.psystem.Configuration;
import org.gcn.plinguacore.util.psystem.Psystem;
import org.gcn.plinguacore.util.psystem.membrane.ChangeableMembrane;
import org.gcn.plinguacore.util.psystem.membrane.Membrane;
import org.gcn.plinguacore.util.psystem.rule.IRule;

public abstract class AbstractSimulator implements ISimulator {

	/**
	 * 
	 */
	private transient Thread thread;
	private static final long serialVersionUID = -144569770489973320L;
    private boolean time = false;
	private transient PrintStream infoChannel = System.out;
	private int verbosity = 3;
	private Date date = null;
	protected Configuration currentConfig = null;
	private transient Psystem psystem;


	public AbstractSimulator(Psystem psystem)
	{
		this.psystem=psystem;
		currentConfig=psystem.getFirstConfiguration();
		this.thread = Thread.currentThread();
	}
	
	protected static Configuration initializePsystemConfiguration(Psystem psystem,
			Configuration config) {
		if(config==null){
			Configuration configuration =  psystem.getFirstConfiguration();
			return configuration;
		}
		// TODO Auto-generated method stub
		
		return config;
	}
	
	@Override
	public boolean alternateStep() throws UnsupportedOperationException {
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException();
	}

	@Override
	public void cleanPreviousConfigurations() {
		// TODO Auto-generated method stub

	}

	@Override
	public int countAlternatives() throws UnsupportedOperationException {
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException();
	}

	@Override
	public Configuration getCurrentConfig() {
		// TODO Auto-generated method stub
		return currentConfig;
	}

	@Override
	public PrintStream getInfoChannel() {
		// TODO Auto-generated method stub
		return infoChannel;
	}

	@Override
	public Psystem getPsystem() {
		// TODO Auto-generated method stub
		return psystem;
	}

	@Override
	public final double getTime() {

		if (date != null)
			return (((double) ((new Date()).getTime()) - (double) (date
					.getTime())) / 1000);
		return 0;
	}

	@Override
	public int getVerbosity() {
		// TODO Auto-generated method stub
		return verbosity;
	}

	@Override
	public boolean isFinished() {

		boolean finished = true;
		Iterator<? extends Membrane> it = getCurrentConfig().getMembraneStructure().getAllMembranes()
				.iterator();

		while (it.hasNext() && finished) {
			ChangeableMembrane m =  (ChangeableMembrane)it.next();
			Iterator<IRule> itRules = getPsystem().getRules().iterator(m.getLabel(),
					m.getCharge());
			while (itRules.hasNext() && finished) {
				if (itRules.next().countExecutions(m) > 0)
					finished = false;
			}
		}

		return finished;
	}
	

	@Override
	public boolean isTimed() {
		// TODO Auto-generated method stub
		return time;
	}

	@Override
	public void reset() {
		// TODO Auto-generated method stub
		date = null;
		currentConfig=psystem.getFirstConfiguration();
	}



	/**
	 * Takes steps until reaching a halting configuration
	 */

	@Override
	public void run() throws PlinguaCoreException {
		/*Before running the simulation, we need to set the current thread*/
		setThread(Thread.currentThread());
		while ((!Thread.currentThread().isInterrupted())&&step());	
	}

	


	private final void runTemporized(ThreadSimulator threadSim, long timeOut) {
		/*
		 * To run a temporized simulator, we delegate on the ThreadSimulator
		 * instance its execution and we monitor the elapsed time
		 */
		synchronized (threadSim) {
			try {
				setThread(threadSim);
				/*We want the thread no to interrupt others when its execution time has elapsed*/
				threadSim.setDaemon(true);
				/*Start the simulator*/
				threadSim.start();
				/*Wait until the thread ends or the time out has elapsed*/

				
				threadSim.wait(timeOut);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				return;
			}
		}
		if (threadSim.isAlive()){
			/*
			 * Once the time has elapsed, the simulation needs to be interrupted
			 * in case it hasn't finished
			 */
			threadSim.tellMonitor();
			threadSim.interrupt();
		}
		/*Stop the thread*/

		/*Wait until the thread dies*/
		//while(!threadSim.isInterrupted());
		/*The current thread is the simulator one again*/
		setThread(Thread.currentThread());

	}

	/**
	 * @see org.gcn.plinguacore.simulator.ISimulator#runSteps(int)
	 */
	@Override
	public void runSteps(int steps) throws PlinguaCoreException {
		setThread(Thread.currentThread());
		for (int stepCounter = 0; (!Thread.currentThread().isInterrupted())&&(stepCounter < steps); stepCounter++)
			step();
	}


	/**
	 * @see org.gcn.plinguacore.simulator.ISimulator#runUntilTimeOut(int)
	 */
	public final void runUntilTimeOut(long timeOut) {
		runTemporized(new ThreadSimulator(this), timeOut);

	}
	

	/**
	 * @see org.gcn.plinguacore.simulator.ISimulator#runUntilTimeOutorSteps(int, int)
	 */
	public final void runUntilTimeOutorSteps(long timeOut, int steps) {
		runTemporized(new StepLimitedThreadSimulator(this, steps), timeOut);
	}

	

	@Override
	public void setCurrentConfig(Configuration configuration) {
		// TODO Auto-generated method stub
		if (configuration == null)
			throw new NullPointerException(
					"CurrentConfig argument shouldn't be null");
		this.currentConfig=configuration;
	}

	@Override
	public void setInfoChannel(PrintStream infoChannel) {
		// TODO Auto-generated method stub
		this.infoChannel=infoChannel;
	}

	@Override
	public void setPsystem(Psystem psystem) {
		// TODO Auto-generated method stub
		if (psystem == null)
			throw new NullPointerException("Psystem argument shouldn't be null");
		currentConfig= initializePsystemConfiguration(psystem, currentConfig);
		this.psystem = psystem;
	}

	@Override
	public void setTimed(boolean time) {
		// TODO Auto-generated method stub
		this.time=time;
	}

	@Override
	public void setVerbosity(int verbosity) {
		// TODO Auto-generated method stub
		this.verbosity=verbosity;
	}
	
	protected final void initDate() {
		if (date == null)
			date = new Date();
	}

	

	@Override
	public boolean stepBack() throws UnsupportedOperationException {
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException();
	}

	@Override
	public boolean stepsBackAvailable() {
		// TODO Auto-generated method stub
		return false;
	}


	/**
	 * @see org.gcn.plinguacore.simulator.ISimulator#stopThread()
	 */
	@Override
	public void stopThread(){
		if(thread!=null &&thread.isAlive())
			thread.interrupt();
	}
	
	protected void setThread(Thread thread){
		if (thread == null)
			throw new NullPointerException("Thread argument shouldn't be null");
		this.thread = thread;
	}
	

	@Override
	public boolean supportsAlternateSteps() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean supportsStepBack() {
		// TODO Auto-generated method stub
		return false;
	}
	public boolean step() throws PlinguaCoreException
	{
		return specificStep();
	}
	
	protected abstract boolean specificStep() throws PlinguaCoreException;

}
