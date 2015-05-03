package itrx.chapter2.inspection;

import java.util.Arrays;

import org.junit.Test;

import rx.Observable;
import rx.observers.TestSubscriber;

public class ElementAtTest {

	public void exampleElementAt() {
		Observable<Integer> values = Observable.range(100, 10);

		values
		    .elementAt(2)
		    .subscribe(
		        v -> System.out.println(v),
		        e -> System.out.println("Error: " + e),
		        () -> System.out.println("Completed")
		    );
		
//		102
//		Completed
	}
	
	public void exampleElementAtOrDefault() {
		Observable<Integer> values = Observable.range(100, 10);

		values
		    .elementAtOrDefault(22, 0)
		    .subscribe(
		        v -> System.out.println(v),
		        e -> System.out.println("Error: " + e),
		        () -> System.out.println("Completed")
		    );
		
//		0
//		Completed
	}
	
	
	//
	// Tests
	//
	
	@Test
	public void testElementAt() {
		TestSubscriber<Integer> tester = new TestSubscriber<Integer>();
		
		Observable<Integer> values = Observable.range(100, 10);

		values
		    .elementAt(2)
		    .subscribe(tester);
		
		tester.assertReceivedOnNext(Arrays.asList(102));
		tester.assertTerminalEvent();
		tester.assertNoErrors();
	}
	
	@Test
	public void testElementAtOrDefault() {
		TestSubscriber<Integer> tester = new TestSubscriber<Integer>();
		
		Observable<Integer> values = Observable.range(100, 10);

		values
		    .elementAtOrDefault(22, 0)
		    .subscribe(tester);
		
		tester.assertReceivedOnNext(Arrays.asList(0));
		tester.assertTerminalEvent();
		tester.assertNoErrors();
	}

}
