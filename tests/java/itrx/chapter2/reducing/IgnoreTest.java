package itrx.chapter2.reducing;

import java.util.Arrays;

import org.junit.Test;

import rx.Observable;
import rx.observers.TestSubscriber;

public class IgnoreTest {

	public void exampleIgnoreElements() {
		Observable<Integer> values = Observable.range(0, 10);

		values
		    .ignoreElements()
		    .subscribe(
		        v -> System.out.println(v),
		        e -> System.out.println("Error: " + e),
		        () -> System.out.println("Completed")
		    );
		
		// Completed
	}
	
	
	//
	// Tests
	//
	
	@Test
	public void testIgnoreElements() {
		TestSubscriber<Integer> tester = new TestSubscriber<Integer>();
		
		Observable<Integer> values = Observable.range(0, 10);

		values
		    .ignoreElements()
		    .subscribe(tester);
		
		tester.assertReceivedOnNext(Arrays.asList());
		tester.assertTerminalEvent();
		tester.assertNoErrors();
	}

}
