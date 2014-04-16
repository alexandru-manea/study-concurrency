package callable;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

/**
 * Example of how to construct a callable task to be run by a thread, which you can poll to see if it has finished or even cancel it.
 * Done with the help of ExecutorService and Future.
 * 
 * @author Alexandru Manea
 *
 */
public class CallableTask {

	public static void main(String[] args) {

		ExecutorService executor = Executors.newSingleThreadExecutor();

		Future<?> future = executor.submit(new Callable<Object>() {

			public Object call() throws Exception {

				System.out.println("Starting things...");

				try {
					Thread.sleep(5000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}

				System.out.println("5 seconds passed...");

				try {
					Thread.sleep(2000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}

				System.out.println("Another 2 seconds passed. Cleaning things up...");
				System.out.println("===============================================");

				return null;
			}});

		try {
			Thread.sleep(5000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

		if (!future.isDone()) {
			System.out.println("Not done ba boule!");
		}
		
		try {
			Thread.sleep(2000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

		if (future.isDone()) {
			System.out.println("\n!!! FINALLY DONE !!!");
		}

	}
}
