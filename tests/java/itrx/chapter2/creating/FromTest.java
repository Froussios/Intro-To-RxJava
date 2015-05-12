package itrx.chapter2.creating;

import java.util.Arrays;
import java.util.concurrent.FutureTask;

import org.junit.Test;

import rx.Observable;
import rx.observers.TestSubscriber;

public class FromTest {
	
	public void exampleFromFuture() {
		FutureTask<Integer> f = new FutureTask<Integer>(() -> {
		    Thread.sleep(2000);
		    return 21;
		});
		new Thread(f).start();

		Observable<Integer> values = Observable.from(f);

		values.subscribe(
		    v -> System.out.println("Received: " + v),
		    e -> System.out.println("Error: " + e),
		    () -> System.out.println("Completed")
		);
		
		// Received: 21
		// Completed
	}

	public void exampleFromArray() {
		Integer[] is = {1,2,3};
		Observable<Integer> values = Observable.from(is);
		values.subscribe(
		    v -> System.out.println("Received: " + v),
		    e -> System.out.println("Error: " + e),
		    () -> System.out.println("Completed")
		);
		
		// Received: 1
		// Received: 2
		// Received: 3
		// Completed
	}
	
	public void exampleFromIterable() {
		Iterable<Integer> input = Arrays.asList(1,2,3);
		Observable<Integer> values = Observable.from(input);
		values.subscribe(
		    v -> System.out.println("Received: " + v),
		    e -> System.out.println("Error: " + e),
		    () -> System.out.println("Completed")
		);
		
		// Received: 1
		// Received: 2
		// Received: 3
		// Completed
	}
	
	
	//
	// Tests
	//
	
	@Test
	public void testFromArray() {
		TestSubscriber<Integer> tester = new TestSubscriber<Integer>();
		
		Integer[] input = {1,2,3};
		Observable<Integer> values = Observable.from(input);
		values.subscribe(tester);
		
		tester.assertReceivedOnNext(Arrays.asList(input));
		tester.assertNoErrors();
		tester.assertTerminalEvent();
	}
	
	@Test
	public void testFromIterable() {
		TestSubscriber<Integer> tester = new TestSubscriber<Integer>();
		
		Iterable<Integer> input = Arrays.asList(1,2,3);
		Observable<Integer> values = Observable.from(input);
		values.subscribe(tester);
		
		tester.assertReceivedOnNext(Arrays.asList(1,2,3));
		tester.assertNoErrors();
		tester.assertTerminalEvent();
	}

}
