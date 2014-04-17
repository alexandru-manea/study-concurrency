package b.threadsafety;

import java.math.BigInteger;
import java.util.concurrent.atomic.AtomicLong;
import javax.servlet.*;
import $annotations.*;


public class B_Atomicity
{
	
	/**
	 * ATOMIC OPERATIONS
	 * -----------------
	 * 
	 * **********************************************************************************************************************************
	 * OPERATIONS A AND B ARE <ATOMIC WRT EACH OTHER> IF, FROM THE PERSPECTIVE OF A THREAD EXECUTING A, WHEN ANOTHER THREAD EXECUTES B, 
	 * EITHER ALL OF B HAS EXECUTED, OR NONE OF IT HAS. AN <ATOMIC OPERATION> IS ONE THAT IS ATOMIC WRT ALL OPERATIONS, INCLUDING ITSELF, 
	 * THAT OPERATE ON THE SAME STATE.
	 * **********************************************************************************************************************************
	 * 
	 * COMPOUND ACTIONS :: Sequences of operations that must be executed atomically in order to remain thread-safe. Examples include:
	 * 
	 * 		# READ-MODIFY-WRITE :: sequence of three discrete operations: fetch the current value, modify it, and write the new value back
	 * 							:: e.g. if one thread is interrupted in the middle of this operation, another may read an incorrect value
	 * 
	 *      # CHECK-THEN-ACT    :: a potentially stale observation is used to make a decision on what to do next
	 * 							:: e.g. a thread is interrupted after the check, another one changes the boolean
	 * 
	 */

	
	/**
	 * RACE CONDITIONS
	 * ---------------
	 * 
	 * **********************************************************************************************************************************
	 * A <RACE CONDITION> OCCURS WHEN THE CORRECTNESS OF A COMPUTATION DEPENDS ON THE RELATIVE TIMING OR INTEARLIVING OF MULTIPLE THREADS
	 * BY THE RUNTIME. RACE CONDITIONS CAN OCCUR WHEN EXECUTING COMPOUND ACTIONS.
	 * **********************************************************************************************************************************
	 * 
	 */
	
	
	/*
	 * EXAMPLE OF READ-MODIFY-WRITE AND OF A RACE CONDITION WITH OUR EXAMPLE
	 * 
	 * Same factorization servlet, BUT WITH <ONE> STATE VARIABLE.
	 * 
	 * It is NOT THREAD-SAFE because it is SUSCEPTIBLE TO LOST UPDATES. The increment operation is an example of a compound action,
	 * more exactly a READ-MODIFY-WRITE operation. Because it does not execute as a single, indivisible unit, a thread performing it
	 * can be interrupted just after reading value 'x'. Another one reads 'x' as well, and both set it to 'x+1', instead of ending up
	 * being 'x+2'. This is an example of a RACE CONDITION.
	 */
	@NotThreadSafe
	class UnsafeCountingFactorizer implements Servlet
	{
		private long count = 0; // NEW
		
		public long getCount() {return count;} // NEW
		
		public void service(ServletRequest request, ServletResponse response)
		{
			BigInteger i = extractFromRequest(request);
			BigInteger[] factors = factor(i);
			++count; // NEW
			encodeIntoResponse(response, factors);
		}

		
		// mock methods
		public BigInteger extractFromRequest(ServletRequest request){return null;}
		public void encodeIntoResponse(ServletResponse response, BigInteger[] factors){}
		public BigInteger[] factor(BigInteger i){return null;}
		
		// unimplemented methods from Servlet
		public void destroy(){}
		public ServletConfig getServletConfig(){return null;}
		public String getServletInfo(){return null;}
		public void init(ServletConfig arg0) throws ServletException{}
	}
	
	
	/**
	 * ONE SOLUTION :: JAVA.UTIL.CONCURRENT ATOMIC VARIABLE CLASSES
	 * 
	 * IMPORTANT --> CAN MAKE THE CLASS THREAD-SAFE JUST BY USING THREAD-SAFE OBJECTS, LIKE ATOMIC LONG, ONLY WHEN WE HAVE 
	 * INDEPENDENT STATE VARIABLES AND NO RELATIONSHIPS BETWEEN THEM ARE SPECIFIED IN THE INVARIANTS.
	 * 
	 */
	
	/*
	 * By replacing the long counter with an <AtomicLong>, we ensure that all actions that access the counter state are atomic.
	 * Then the class becomes THREAD-SAFE, but only because WE HAVE ONE STATE VARIABLE.
	 */
	@ThreadSafe
	class CountingFactorizer implements Servlet
	{
		private final AtomicLong count = new AtomicLong(0);
		
		public long getCount()
		{
			return count.get(); // NEW
		}
		
		public void service(ServletRequest request, ServletResponse response)
		{
			BigInteger i = extractFromRequest(request);
			BigInteger[] factors = factor(i);
			count.incrementAndGet(); // NEW
			encodeIntoResponse(response, factors);
		}

		// mock methods
		public BigInteger extractFromRequest(ServletRequest request){return null;}
		public void encodeIntoResponse(ServletResponse response, BigInteger[] factors){}
		public BigInteger[] factor(BigInteger i){return null;}

		// unimplemented methods from Servlet
		public void destroy(){}
		public ServletConfig getServletConfig(){return null;}
		public String getServletInfo(){return null;}
		public void init(ServletConfig arg0) throws ServletException{}
	}


	/*
	 * EXAMPLE OF CHECK-THEN-ACT AND OF A RACE CONDITION
	 * 
	 * LAZY INITIALIZATION: defer initializing an object until it is actually needed while at the same time ensuring that it is 
	 * initialized only once.
	 * 
	 * It is NOT THREAD-SAFE because, in the presence of some unlucky timing, the value of instance might be changed while another
	 * thread is in mid-update, thus resulting in two different objects being created and returned. This happens because, as before,
	 * the action has to be atomic, because it deals with a shared, mutable state variable.
	 */
	@NotThreadSafe
	class LazyInitRace
	{
		private ExpensiveObject instance = null;
		
		public ExpensiveObject getInstance()
		{
			if (instance == null)
				instance = new ExpensiveObject();
			
			return instance;
		}
		
		class ExpensiveObject {}
	}
}
