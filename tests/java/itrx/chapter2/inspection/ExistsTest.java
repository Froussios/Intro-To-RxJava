package itrx.chapter2.inspection;

import java.util.Arrays;

import org.junit.Test;

import rx.Observable;
import rx.observers.TestSubscriber;

public class ExistsTest {

	public void exampleFalse() {
		Observable<Integer> values = Observable.range(0, 2);

		values
		    .exists(i -> i > 2)
		    .subscribe(
		        v -> System.out.println(v),
		        e -> System.out.println("Error: " + e),
		        () -> System.out.println("Completed")
		    );
		
//		false
//		Completed
	}
	
	public void exampleTrue() {
		Observable<Integer> values = Observable.range(0, 4);

		values
		    .exists(i -> i > 2)
		    .subscribe(
		        v -> System.out.println(v),
		        e -> System.out.println("Error: " + e),
		        () -> System.out.println("Completed")
		    );
		
//		true
//		Completed
	}
	
	@Test
	public void testFalse() {
		TestSubscriber<Boolean> tester = new TestSubscriber<Boolean>();
		
		Observable<Integer> values = Observable.range(0, 2);

		values
		    .exists(i -> i > 2)
		    .subscribe(tester);
		
		tester.assertReceivedOnNext(Arrays.asList(false));
		tester.assertTerminalEvent();
		tester.assertNoErrors();
	}
	
	@Test
	public void testTrue() {
		TestSubscriber<Boolean> tester = new TestSubscriber<Boolean>();
		
		Observable<Integer> values = Observable.range(0, 4);

		values
		    .exists(i -> i > 2)
		    .subscribe(tester);
		
		tester.assertReceivedOnNext(Arrays.asList(true));
		tester.assertTerminalEvent();
		tester.assertNoErrors();
	}

}
