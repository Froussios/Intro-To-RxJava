package itrx.chapter2.creating;

import static org.junit.Assert.*;

import java.util.Arrays;
import java.util.concurrent.FutureTask;

import org.junit.Test;

import rx.Observable;
import rx.Subscription;
import rx.observers.TestSubscriber;
import rx.schedulers.TestScheduler;

public class FromTest {

	@Test
	public void testFromArray() {
		TestSubscriber<Integer> tester = new TestSubscriber<Integer>();
		
		Integer[] input = {1,2,3};
		Observable<Integer> values = Observable.from(input);
		Subscription subscription = values.subscribe(tester);
		
		tester.assertReceivedOnNext(Arrays.asList(input));
		tester.assertNoErrors();
		tester.assertTerminalEvent();
	}
	
	@Test
	public void testFromIterable() {
		TestSubscriber<Integer> tester = new TestSubscriber<Integer>();
		
		Iterable<Integer> input = Arrays.asList(1,2,3);
		Observable<Integer> values = Observable.from(input);
		Subscription subscription = values.subscribe(tester);
		
		tester.assertReceivedOnNext(Arrays.asList(1,2,3));
		tester.assertNoErrors();
		tester.assertTerminalEvent();
	}

}
