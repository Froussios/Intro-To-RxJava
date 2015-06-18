package itrx.chapter2.aggregation;

import java.util.Arrays;

import org.junit.Test;

import rx.Observable;
import rx.observers.TestSubscriber;

public class NestExample {

	public void example() {
		Observable.range(0, 3)
			.nest()
			.subscribe(ob -> ob.subscribe(System.out::println));
		
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
		
		Observable.range(0, 3)
			.nest()
			.subscribe(ob -> ob.subscribe(tester));
		
		tester.assertReceivedOnNext(Arrays.asList(0,1,2));
		tester.assertTerminalEvent();
		tester.assertNoErrors();
	}

}
