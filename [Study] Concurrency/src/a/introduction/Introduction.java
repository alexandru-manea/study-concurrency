package a.introduction;
import $annotations.NotThreadSafe;
import $annotations.ThreadSafe;


public class Introduction
{
	
	/**
	 * In the past, computers didn't have operating systems - they executed a single program from beginning to end and the
	 * program had direct access to all the resources of the machine => extremely inefficient.
	 * 
	 * Operating systems evolved to allow running individual programs in <processes> - isolated, independently executing
	 * programs to which the OS allocates resources such as memory, file handles and security credentials. Processes can
	 * communicate with one another through a variety of methods: sockets, shared memory, semaphores and files.
	 *    ADVANTAGES OF RUNNING PROCESSES SIMULTANEOUS
	 * 	     - resource utilisation: through timesharing, for example when one process awaits for input, another can run
	 *       - fairness: multiple users & programs have equal claims on the resources
	 * 
	 * <Threads> allow multiple streams of program control flow to coexist within a process. They share process wide resources
	 * such as memory and file handles, but each thread has its own program counter, stack and local variables. Threads are
	 * called lightweight processes and most OSs treat them as the basic units of scheduling. In the absence of explicit
	 * coordination, threads execute simultaneously and asynchronously wrt one another. Since threads share the memory
	 * address space of their owning process, all threads within a process have access to the same variables and allocate 
	 * objects from the same heap. This allows for a finer-grained data sharing and inter-process communication.
	 */
	
	
	/**
	 * BENEFITS OF THREADS
	 * -------------------
	 * 
	 * + Exploiting Multi-CPU Systems: provide a natural decomposition for exploiting hardware parallelism on multi-CPU systems; 
	 *   multiple threads within a process can be scheduled simultaneous on multiple CPUs
	 *   
	 * + Simplicity of Modelling: turn complicated asynchronous code into simpler, synchronous, straight-line code. A program that 
	 *   processes only one type of task sequentially is easier to write, less error-prone and easier to test. Thus, assigning a
	 *   thread to each type of task confers the illusion of sequentiality and insulates domain logic from scheduling issues.
	 * 
	 * + Handling of Asynchronous Events: a server application that accepts socket connections from multiple remote clients is easier
	 *   to develop when each connection is allocated its own thread and allowed to use synchronous I/O.
	 *   
	 *  + More Responsive UIs: In a single threaded GUI, you either frequently poll throughout the code for input events - which is 
	 *    messy and intrusive, or execute code indirectly through a main event loop, which can freeze the interface. Modern GUI 
	 *    frameworks such as AWT or Swing replace the main event loop with an Event Dispatch Thread.
	 *    
	 *  + Shared Memory: Threads share the same memory address and they can share data much easier.
	 */
	
	
	/**
	 * RISKS OF THREADS
	 * ----------------
	 * 1. Safety Hazards
	 * 2. Liveness Hazards
	 * 3. Performance Hazards
	 */
	
	/**
	 * 1. SAFETY HAZARDS
	 * ("nothing bad ever happens")
	 * 
	 * In the absence of sufficient synchronisation, the ordering of operations in multiple threads is unpredictable. Class <UnsageSequence>
	 * provides one such example - a safety hazard called a <race condition>. Allowing multiple threads to access and modify the same vars
	 * introduces an element of non-sequentiality into an otherwise sequential program. Thus, proper coordination is in need. Moreover, 
	 * the compiler is allowed to change the timing and ordering of actions and the runtime can cache variables in registers. A thread-safe
	 * version is presented in class <Sequence>.
	 */
	
	/*
	 * Method is supposed to generate a sequence of unique integer values, but the interleaving of actions in multiple threads can lead
	 * to undesirable results. More exactly, with some unlucky timings, two threads could call the method and get the same value. This
	 * is because the increment operation, this.value++, is not an atomic operation - read, add, write. If two threads read the value
	 * at the same time and add one to it, both will return the same number.
	 */
	@NotThreadSafe
	class UnsafeSequence
	{
		private int value;
		
		public int getNext()
		{
			return this.value++;
		}
	}
	
	/*
	 * Synchronised version.
	 */
	@ThreadSafe
	class Sequence
	{
		private int value;
		
		public synchronized int getNext()
		{
			return this.value++;
		}
	}
	
	/**
	 * 2. LIVENESS HAZARDS
	 * ("something good eventually happens")
	 * 
	 * A liveness failure occurs when an activity gets into a state such that it is permanently unable to make forward progress. Forms of
	 * liveness failures include deadlocks, starvation and livelocks. For example: thread A is waiting for a resource that thread B is 
	 * holding exclusively, and B never releases it, A will wait forever.
	 */
	
	/**
	 * 3. PERFORMANCE HAZARDS
	 * ("something good happens quickly")
	 * 
	 * Broad range of performance issues: poor service time, responsiveness, throughput, resource consumption, etc.
	 * 
	 * Threads always carry some degree of overhead: 
	 *    - <Context switching> occurs when the scheduler suspends the active thread temporarily so another thread can run and has significant 
	 *      costs when saving and restoring execution context.
	 *    - Synchronisation inhibits compiler optimisations and flush or invalidate memory caches.
	 */

	
	/**
	 * THREADS ARE EVERYWHERE
	 * ----------------------
	 * 
	 * Every Java application uses threads. When the JVM starts, it creates threads for housekeeping tasks such as garbage collection
	 * and finalization, and a main tread for running the main method. In addition, frameworks may create threads on your behalf, and 
	 * code called from these threads must be thread-safe.
	 * 
	 * ************************************************************************************************************************************
	 * FRAMEWORKS INTRODUCE CONCURRENCY BY CALLING APPLICATION COMPONENTS FROM FRAMEWORKS THREADS. COMPONENTS INVARIABLY ACCESS APPLICATION 
	 * STATE, THUS REQUITING THAT ALL CODE PATHS ACCESSING THAT STATE BE THREAD-SAFE.
	 * ************************************************************************************************************************************
	 * 
	 * TIMER :: Convenience mechanism for scheluing tasks to run at a later time. Using it can complicate an otherwise sequential program,
	 * because TimerTasks are executed in a thread managed by the Timer, not the application. If a TimerTask accesses data that is also 
	 * accessed by other application threads, then, not only must the TimerTask do so in a thread-safe manner, but <so must any other
	 * classes that access that data>. The easiest way to achieve this is to ensure the shared objects are themselves thread-safe.
	 * 
	 * RMI :: Lets you invoke methods on objects running in another JVM. When calling a remote method, the arguments are marshaled into a
	 * byte stream and shipped over the network into the remote JVM, where they are unmarshalled and passed to the method. The remote 
	 * object must guard against two safety hazards: (1) properly coordinating access to state that may be shared with other objects, and
	 * (2) properly coordinating access to the remote object itself, since it may be called in multiple threads simultaneously.
	 * 
	 * SWING :: GUI applications are inherently asynchronous because the interfaces have to be responsive at all times. This is resolved by
	 * creating a separate thread for handling user-initiated events and updating the view. Swing components, such a JTable, are not 
	 * thread-safe. Instead, Swing programs achieve this propery by confining all access to the components to the event thread. When the user
	 * performs a UI action, an event heandler is called in the event thread to peform the requested operation. If the handler needs to access
	 * application state that is also accessed from other threads (e.g., document being edited), then the event handler, along with any other
	 * code that accesses that state, must do so in a thread-safe manner.
	 * 
	 */
	
}
