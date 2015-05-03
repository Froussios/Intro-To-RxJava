package itrx.chapter2.creating;

import java.util.Arrays;

import org.junit.Test;

import rx.Observable;
import rx.observers.TestSubscriber;

public class FromTest {

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
