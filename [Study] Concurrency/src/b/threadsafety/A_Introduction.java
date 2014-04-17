package b.threadsafety;

import java.math.BigInteger;
import $annotations.*;
import javax.servlet.*;

public class A_Introduction
{
	
	/**
	 * SCOPE OF THREAD SAFETY
	 * ----------------------
	 * 
	 * Writing thread-safe code is about MANAGING ACCESS TO SHARED, MUTABLE STATE.
	 * 
	 * 		STATE -- Any data that can affect the object's externally visible behaviour. This includes the objects's data, stored in 
	 * 		<state variables> and <static fields>, but also data of other, dependent objects.
	 * 
	 * 		SHARED -- A variable could be accessed by multiple threads
	 * 
	 * 		MUTABLE -- Its value could change during its lifetime
	 * 
	 * 		MANAGE ACCESS -- Whenever more than one thread accesses a given state variable, and one of them might write to it, they all
	 * 		must coordinate access using synchronization.
	 * 
	 * ***********************************************************************************************************************************
	 * IF MULTIPLE THREADS ACCESS THE SAME MUTABLE VARIABLE WITHOUT APPROPRIATE SYNCHRONIZATION, THE PROGRAM IS BROKEN. TO FIX IT, EITHER:
	 * - DON'T SHARE THE STATE VARIABLE ACROSS THREADS;
	 * - MAKE THE STATE VARIABLE IMMUTABLE; OR
	 * - USE SYNCHRONIZATION WHENEVER ACCESSING THE STATE VARIABLE.
	 * ***********************************************************************************************************************************
	 * 
	 */
	
	
	/**
	 * WHAT IS THREAD SAFETY
	 * ---------------------
	 * 
	 * ********************************************************************************************************************************
	 * A CLASS IS THREAD-SAFE IF IT BEHAVES CORRECTLY WHEN ACCESSED FROM MULTIPLE THREADS, REGARDLESS OF THE SCHEDULING OR INTERLEAVING 
	 * OF THE EXECUTION OF THOSE THREADS BY THE RUNTIME ENVIRONMENT, AND WITH NO ADDITIONAL SYNCHORNIZATION OR OTHER COORDINATION ON THE 
	 * PART OF THE CALLING CODE (THREAD-SAFE CLASSES ENCAPSULATE ANY REQUIRED SYNCHRONIZATION).
	 * ********************************************************************************************************************************
	 * 
	 * CORRECTNESS :: when a class <conforms to its specification> - where a good specification defines <invariants> constraining an object's 
	 * state and <postconditions> describing the effects of its operations.
	 * 
	 */
	
	
	/**
	 * EXAMPLE USED IN CHAPTER & STATELESSNESS
	 * ---------------------------------------
	 * 
	 * Bellow is the example we are going to develop in this chapter/package, by slowly extending it to add features while preserving
	 * thread safety. This is an example (like most servlets) of a stateless object: it has no fields and references no fields from
	 * other classes. The transient state for a particular computation exists solely in local variables that are stored on the thread's 
	 * stack and are accessible only in the executing thread. One thread accessing a StatelessFactorizer cannot influence the result of 
	 * another thread accessing the same StatlessFactorizer. Therefore:
	 * 
	 * *****************************************
	 * STATELESS OBJECTS ARE ALWAYS THREAD-SAFE.
	 * *****************************************
	 * 
	 */
	
	/*
	 * Servlet-based factorization service. It unpacks the number to be factored from the servlet request, factors it and packages the
	 * response results into the servlet response.
	 */
	@ThreadSafe
	class StatelessFactorizer implements Servlet
	{
		public void service(ServletRequest request, ServletResponse response)
		{
			BigInteger i = extractFromRequest(request);
			BigInteger[] factors = factor(i);
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
