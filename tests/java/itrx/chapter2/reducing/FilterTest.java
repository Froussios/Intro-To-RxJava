package itrx.chapter2.reducing;

import java.util.Arrays;

import org.junit.Test;

import rx.Observable;
import rx.observers.TestSubscriber;

public class FilterTest {

	
	public void example() {
		Observable<Integer> values = Observable.range(0,10);
		values
		    .filter(v -> v % 2 == 0)
		    .subscribe(
		        v -> System.out.println(v),
		        e -> System.out.println("Error: " + e),
		        () -> System.out.println("Completed")
		    );
		
		// 0
		// 2
		// 4
		// 6
		// 8
		// Completed
	}
	
	
	//
	// Test
	//
	
	@Test
	public void test() {
		TestSubscriber<Integer> tester = new TestSubscriber<Integer>();
		
		Observable<Integer> values = Observable.range(0,10);
		values
		    .filter(v -> v % 2 == 0)
		    .subscribe(tester);
		
		tester.assertReceivedOnNext(Arrays.asList(0,2,4,6,8));
		tester.assertTerminalEvent();
		tester.assertNoErrors();
	}

}
