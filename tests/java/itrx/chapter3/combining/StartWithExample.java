package itrx.chapter3.combining;

import java.util.Arrays;

import org.junit.Test;

import rx.Observable;
import rx.observers.TestSubscriber;

public class StartWithExample {

	public void example() {
		Observable<Integer> values = Observable.range(0, 3);

		values.startWith(-1,-2)
		    .subscribe(System.out::println);
		
		// -1
		// -2
		// 0
		// 1
		// 2
	}
	
	
	//
	// Test
	//
	
	@Test
	public void test() {
		TestSubscriber<Integer> tester = new TestSubscriber<>();
		
		Observable<Integer> values = Observable.range(0, 3);

		values.startWith(-1,-2)
		    .subscribe(tester);
		
		tester.assertReceivedOnNext(Arrays.asList(-1,-2,0,1,2));
		tester.assertTerminalEvent();
		tester.assertNoErrors();
	}

}
