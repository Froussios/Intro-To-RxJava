package itrx.chapter2.aggregation;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.Test;

import rx.Observable;
import rx.observers.TestSubscriber;

public class CollectTest {

	public void example() {
		Observable<Integer> values = Observable.range(10,5);
		
		values
			.collect(
				() -> new ArrayList<Integer>(),
				(acc, value) -> acc.add(value))
			.subscribe(v -> System.out.println(v));
	}
	
	@Test
	public void test() {
		TestSubscriber<List<Integer>> tester = new TestSubscriber<>();
		
		Observable<Integer> values = Observable.range(10,5);
		
		values
			.collect(
				() -> new ArrayList<Integer>(),
				(acc, value) -> acc.add(value))
			.subscribe(tester);
		
		tester.assertReceivedOnNext(Arrays.asList(
				Arrays.asList(10, 11, 12, 13, 14)
		));
		tester.assertTerminalEvent();
		tester.assertNoErrors();
	}

}
