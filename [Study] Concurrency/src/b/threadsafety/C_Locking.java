package b.threadsafety;

import java.math.BigInteger;
import java.util.concurrent.atomic.AtomicReference;
import javax.servlet.*;
import $annotations.*;


public class C_Locking
{

	/**
	 * Going from one state variable to more than one is not necessarily as simple as going from zero to one.
	 * 
	 */
	
	/*
	 * EXAMPLE EXTENDED WITH MORE THAN ONE STATE VARIABLE
	 * 
	 * Extented factorization service. To improve the performance of the servlet, we cache the most recently computed result, just in case 
	 * two consecutive clients request the factorization of the same number. [Forget about the counter for now]. As before, we used 
	 * AtomicReference (thread-safe holder class for an object reference) to manage the number and its factors.
	 */
	@NotThreadSafe
	class UnsafeCachingFactorizer implements Servlet
	{
		private final AtomicReference<BigInteger> lastNumber = new AtomicReference<BigInteger>(); // NEW
		private final AtomicReference<BigInteger[]> lastFactors = new AtomicReference<BigInteger[]>(); // NEW
		
		public void service(ServletRequest request, ServletResponse response)
		{
			BigInteger i = extractFromRequest(request);
			
			// NEW
			if (i.equals(lastNumber.get()))
			{
				encodeIntoResponse(response, lastFactors.get());
			}
			
			else
			{
				BigInteger[] factors = factor(i);
				encodeIntoResponse(response, factors);
				
				// NEW
				lastNumber.set(i);
				lastFactors.set(factors);
			}
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
	 * It is NOT THREAD-SAFE because, even though the atomic references are individually thread-safe, the class still has RACE CONDITIONS.
	 * The definition of thread-safety is that it preserves the invariants, regardless of timing or interleaving of operations in multiple
	 * threads. One invariant in this case is that the product of the factors cached in lastfactors equal the value cached in lastNumber.
	 * Thus, when updating one, the other has to be updated in the same atomic operation.
	 */
	
	
	/**
	 * ********************************************************************************************************
	 * IMPORTANT :: TO PRESERVE STATE CONSISTENCY, UPDATE RELATED STATE VARIABLES IN A SINGLE ATOMIC OPERATION.
	 * ********************************************************************************************************
	 * 
	 */
	
	
	/**
	 * INTRINSIC LOCKS
	 * ---------------
	 * 
	 * Java provides a built-in locking mechanism for enforcing atomicity: the SYNCHRONIZED block.
	 * 
	 *    synchornized(lock)
	 *    {
	 *        // access or modify shared state guarded by lock
	 *    }
	 * 
	 * [A synchronized method is a shorthand for a sunchronized block that spans the entire method body and is guarded by the object
	 * on which the method is being invoked]
	 * 
	 * INTRINSIC LOCKS --> Every Java object can act as a lock for purpose of synchornization
	 * 				   --> Intrinsic locks act as MUTEXES --> at most one thread can hold the lock
	 * 
	 */
	
	
	/*
	 * EXAMPLE NOW USING SYNCHORNIZATION, THREAD-SAFE, BUT INEFFICIENT
	 * 
	 * Make the service() method synchronized. But -- pretty extreme as it inhibits mulotiple clients from using the factoring servlet
	 * simultaneously at all => unacceptable poor responsiveness.
	 */
	@ThreadSafe
	class SynchonizedFactorizer implements Servlet
	{
		@GuardedBy("this") private BigInteger lastNumber;
		@GuardedBy("this") private BigInteger[] lastFactors;
		
		public synchronized /* NEW */ void service(ServletRequest request, ServletResponse response)
		{
			BigInteger i = extractFromRequest(request);
			
			if (i.equals(lastNumber))
			{
				encodeIntoResponse(response, lastFactors);
			}
			
			else
			{
				BigInteger[] factors = factor(i);
				encodeIntoResponse(response, factors);
				
				lastNumber = i;
				lastFactors = factors;
			}
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
	 * REENTRANCY
	 * ----------
	 * 
	 * ************************************************************************************************
	 * REENTRANCY :: PROPERTY THAT A LOCK IS ACQUIRED ON A PER-THREAD RATHER THAN PER-INVOCATION BASIS.
	 * ************************************************************************************************
	 * 
	 * If a thread tries to acquire a lock that it already holds, the request succeeds. Reentrancy is implemented by associating with
	 * each lock an owning thread and an acquisition count. Reentrancy facilitates encapsulation of locking behaviour.
	 * 
	 */
	
	
	/**
	 * GUARDING STATE WITH LOCKS
	 * -------------------------
	 * 
	 * *******************************************************************************************************************************
	 * FOR EACH MUTABLE STATE VARIABLE THAT MAY BE ACCESSED BY MORE THAN ONE THREAD, <ALL> ACCESSES TO THAT VARIABLE MUST BE PERFORMED 
	 * WITH <THE SAME LOCK HELD>. IN THIS CASE, THE VARIABLE IS <GUARDED BY> THAT LOCK.
	 * *******************************************************************************************************************************
	 * 
	 * Holding a lock for the entire duration of a compound action can make the action atomic. But just wrapping the compound action 
	 * with a synchonized block is not sufficient. Synchonization has to be used everywhere that variable is accessed and using the
	 * same lock.
	 * 
	 * 
	 * ****************************************************************************************************************************
	 * EVERY SHARED, MUTABLE VARIABLE SHOULD BE GUARDED BY EXACTLY ONE LOCK AND IT HAS TO BE CLEAR TO MAINTAINERS WHICH LOCK IT IS.
	 * ****************************************************************************************************************************
	 * 
	 * A common locking convention is to encapsulate all mutable state within an object and to pretect it from concurrent access by
	 * synchonizing any code path that accesses mutable state using the object's intrinsic lock.
	 * 
	 * 
	 * *****************************************************************************************************************************
	 * FOR EVERY INVARIANT THAT INVOLVES MORE THAN ONE VARIABLE, ALL THE VARIABLES INVOLVED IN THAT INVARIANT MUST BE GUARDED BY THE 
	 * SAME LOCK.
	 * *****************************************************************************************************************************
	 * 
	 */
	
	
	/**
	 * LIVENESS AND PERFORMANCE
	 * ------------------------
	 * 
	 * *****************************************************************
	 * THERE IS FREQUENTLY A TENSION BETWEEN SIMPLICITY AND PERFORMANCE.
	 * *****************************************************************
	 * 
	 * Simply making a method synchronized or surronding a big chunk of code in a synchonized block may make the class thread-safe,
	 * but it might result in POOR CONCURRENCY, and thus undermine the whole goal of using multiple threads in the first place.
	 * 
	 * Therefore, it is important to narrow the scope of the synchonized block as much as possible, as long as an atomic operation 
	 * is not divided. Long-running operations that do not affect shared state have to be taken out. 
	 * 
	 */
	
	
	/*
	 * EXAMPLE STILL THREAD-SAFE, BUT NOW WITH INCREASED CONCURRENCY PERFORMANCE
	 * [Hits variable reintroduced, toghether with cacheHits, a new one]
	 * 
	 * Before, when the entire service() method was synchronized, only one thread may execute it at one. This subverts the intended
	 * use of the servlet framework -- that servlets be able to handle multiple requests simultaneously. If the servlet is busy 
	 * factoring a big number, other clients have to wait.
	 * 
	 * Now, we use two separate synchronized blocks, each limited to a short section of code. One guards the check-then-act sequence
	 * that tests whether we can just return the cached result, and the other guards updating the cached number and its factors. Also,
	 * the hit counter was reintroduced and a cache hit counter was added as well. Both constitue shared mutable state, so we have to
	 * use synchronization everywhere they are accessed. The code that is outside of the synchronized blocks operates exclusively on
	 * local variables.
	 */
	@ThreadSafe
	class CachedFactorizer implements Servlet
	{
		@GuardedBy("this") private BigInteger lastNumber;
		@GuardedBy("this") private BigInteger[] lastFactors;
		@GuardedBy("this") private long hits; // NEW
		@GuardedBy("this") private long cacheHits; // NEW
		
		
		public void service(ServletRequest request, ServletResponse response)
		{
			BigInteger i = extractFromRequest(request);
			BigInteger[] factors = null;
			
			synchronized(this) // NEW
			{
				hits++;
				
				// check-then-act
				if (i.equals(lastNumber))
				{
					cacheHits++;
					factors = lastFactors.clone();
				}
			}
			
			if (factors == null)
			{
				factors = factor(i); // not in synchronized block, expensive

				synchronized(this) // NEW
				{
					lastNumber = i;
					lastFactors = factors.clone();
				}
			}

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
	
}
