package various;

/**
 * TASK :: CREATE N THREADS AND SIMULATE RUNNERS ON A TRACK. ONCE ONE OF THE RUNNERS HITS THE FINAL DISTANCE, ALL OTHERS
 * SHOULD STOP.
 * 
 * @author Alexandru Manea
 *
 */
public class RunnerThreads {

	private static final int NUMBER_OF_RUNNERS = 10;	
	private static final int FINAL_DISTANCE = 100;

	public static void main(String[] args) {

		for (int i = 0; i < NUMBER_OF_RUNNERS; i++) {

			Thread runner = new Thread(new Runner(i));
			runner.start();
		}
	}


	/**
	 * Runner class implementation
	 * 
	 * 1. [Atomic block with explicit lock] If no one finished, increase the distance and, if the final point is reached,
	 *    set the finished flag and stop.
	 * 2. If someone finished in the meanwhile, stop.
	 * 3. Make the thread sleep a bit to give other threads a chance to run
	 *
	 */
	private static final class Runner implements Runnable {

		// explicit hidden lock
		private static final Object LOCK = new Object();
		
		// flag used to indicate whether runners should continue 
		private static boolean anyoneFinished = false;

		// id of each thread -- for printing purposes
		private int runnerId;

		public Runner(int runnerId) {

			this.runnerId = runnerId;
		} 


		@Override
		public void run() {

			int currentDistance = 0;

			while (true) {

				// atomic block start
				synchronized(LOCK) {

					if (!anyoneFinished) {

						// increase distance
						currentDistance++;
						System.out.println("Runner " + runnerId + " is now at distance " + currentDistance);

						// if destination reached
						if (currentDistance == FINAL_DISTANCE) {
							
							// set the flag
							anyoneFinished = true;
							System.out.println("RUNNER " + runnerId + " FINISHED!");
							
							// stop
							break;
						}
					}
				} // atomic block end
				
				// if someone finished in the meantime, stop
				if (anyoneFinished)
					break;

				// wait a little to give other threads a chance to run
				try {

					Thread.sleep(50);
				} 
				catch (InterruptedException e) {

					e.printStackTrace();
				}
			}
		}
	}
}
