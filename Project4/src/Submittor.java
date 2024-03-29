import java.util.ArrayList;
import java.util.StringTokenizer;

/**
 * <p>Title: Submittor</p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2015, 2004 by Matt Evett</p>
 * <p>Company: </p>
 * @author Matt Evett
 * @student Sarah Yaw
 * @version 2.0
 * extends Thread: should submit new jobs to a SystemSimulator from time to time.
 * It will run at a higher priority than any Jobs, but lower than the SystemSimulator.
 */

class Submittor extends Thread 
{
    //three items needed to create processes in the system
    private final SystemSimulator mySystem; // operating system
    private final WorkFactory myWorkCreator; // job creator
    private final ArrayList<String> myJobDescs; // list of job descriptions
  
    private StringTokenizer st; // tokenizer used to parse string
  
    /**
     * @param jobDescriptions  = an array of Strings, each a line from the input file.  The syntax of this
     * string will vary depending on the type of scheduler we are implementing.  For every type of scheduler
     * the first token will be the name of a job/process and the second token will be an integer representing
     * the number of msecs before this job should be submitted to the OS.  For a FCFS scheduler the remainder 
     * of the string will consist of a single token, an integer equal to the length of the simulated CPU 
     * burst in msec.
     * @param s = The kernel simulator
     * @param progenitor = a factory for creating JobWorkable objects that will be embedded in the Job.
     */
    public Submittor(ArrayList<String> jobDescriptions, SystemSimulator s, WorkFactory progenitor) 
    {
        // Each element of jobDescriptions is a String consisting of two words: a job number/name and
        // a CPU burst duration in milliseconds.  You can use a StringTokenizer object
        // (import java.util.StringTokenizer;) to access these values separately.
        super("Submittor");
        myJobDescs = jobDescriptions;
        mySystem = s;
        myWorkCreator = progenitor;
    }
  
    /*
     * Sleeps for a bit, creates work to be accomplished in a Job and
     * submits this and a description of the work to its system simulator, and repeats. 
     * See my boilerplate code, Submittor.java.
     */
    public void run() 
    {
        // iterate through jobs strings, parse the string, create jobs and add to operating system.
        for (String jobDesc : myJobDescs) 
        {
            String temparr[] = jobDesc.split(" ");
            String id; // ID/name of the Job (simulated process) 
            int delay; // msec delay until this Job is submitted to the kernel
            String CPUburst, IOburst;  // The description of that Job.  (For FCFS this will be a single integer token)
    	
            //Provide code that will set id, delay, and burstDescription from jobDesc.
            id = "Job#"+temparr[0];
            delay = Integer.parseInt(temparr[1]);
            try 
            {
    		sleep( delay ); // wait until submission
            }
            catch (InterruptedException e) 
            {
    		System.err.println("Submittor should never be interrupted");
    		e.printStackTrace();
            }
            for (int i = 2; i<temparr.length;i++)
            {
                if (i%2 == 0)
                {
                    if(temparr.length>i+2)
                    {
                        CPUburst = temparr[i];
                        mySystem.AddNewProcess(id+"-CPUBurst before IO", CPUburst, myWorkCreator.createWork(id));
                    }
                    else
                    {
                        CPUburst = temparr[i];
                        mySystem.AddNewProcess(id+"-Final CPUBurst", CPUburst, myWorkCreator.createWork(id));
                    }
                }
                else
                {
                    IOburst = temparr[i];
                    try 
                    {
                        mySystem.doIO(Integer.parseInt(IOburst));
                        //System.out.println(id+" is waiting on I/O operations");
                        //sleep( Integer.parseInt(IOburst) ); // wait until IOburst finishes
                    }
                    catch (Exception e) 
                    {
                        System.err.println("Submittor should never be interrupted");
                        e.printStackTrace();
                    }
                }
            }
            // create jobs and add them to the Operating System            
            System.out.println("SUBMI sending "+id);
        }
        mySystem.noMoreJobsToSubmit(); // let system know that no more jobs are coming
    }
}