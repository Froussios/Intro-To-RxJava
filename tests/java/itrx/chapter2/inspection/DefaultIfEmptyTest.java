package itrx.chapter2.inspection;

import static org.junit.Assert.assertEquals;

import java.util.Arrays;

import org.junit.Test;

import rx.Observable;
import rx.observers.TestSubscriber;

public class DefaultIfEmptyTest {

	public void exampleDefaultIfEmpty() {
		Observable<Integer> values = Observable.range(0,10);

		values
		    .take(0)
		    .defaultIfEmpty(2)
		    .subscribe(
		        v -> System.out.println(v),
		        e -> System.out.println("Error: " + e),
		        () -> System.out.println("Completed")
		    );
		
		// 2
		// Completed
	}
	
	public void exampleDefaultIfEmptyError() {
		Observable<Integer> values = Observable.error(new Exception());

		values
		    .defaultIfEmpty(2)
		    .subscribe(
		        v -> System.out.println(v),
		        e -> System.out.println("Error: " + e),
		        () -> System.out.println("Completed")
		    );
		
		// Error: java.lang.Exception
	}
	
	
	//
	// Tests
	//
	
	@Test
	public void testDefaultIfEmpty() {
		TestSubscriber<Integer> tester = new TestSubscriber<Integer>();
		
		Observable<Integer> values = Observable.range(0,10);

		values
		    .take(0)
		    .defaultIfEmpty(2)
		    .subscribe(tester);
		
		tester.assertReceivedOnNext(Arrays.asList(2));
		tester.assertTerminalEvent();
		tester.assertNoErrors();
	}
	
	@Test
	public void testDefaultIfEmptyError() {
		TestSubscriber<Integer> tester = new TestSubscriber<Integer>();
		
		Observable<Integer> values = Observable.error(new Exception());

		values
		    .defaultIfEmpty(2)
		    .subscribe(tester);
		
		tester.assertReceivedOnNext(Arrays.asList());
		tester.assertTerminalEvent();
		assertEquals(tester.getOnErrorEvents().size(), 1);
	}

}
