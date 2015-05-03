package itrx.chapter2.aggregation;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.Test;

import rx.Observable;
import rx.observers.TestSubscriber;

public class ToCollectionTest {
	
	public void exampleCustom() {
		Observable<Integer> values = Observable.range(10,5);

		values
		    .reduce(
		        new ArrayList<Integer>(),
		        (acc, value) -> {
		            acc.add(value);
		            return acc;
		        })
		    .subscribe(v -> System.out.println(v));
		
//		[10, 11, 12, 13, 14]
	}
	
	public void exampleToList() {
		Observable<Integer> values = Observable.range(10,5);

		values
		    .toList()
		    .subscribe(v -> System.out.println(v));
		
//		[10, 11, 12, 13, 14]
	}
	
	public void exampleToSortedList() {
		Observable<Integer> values = Observable.range(10,5);

		values
		    .toSortedList((i1,i2) -> i2 - i1)
		    .subscribe(v -> System.out.println(v));
		
//		[14, 13, 12, 11, 10]
	}
	
	
	//
	// Tests
	//
	
	@Test
	public void testCustom() {
		TestSubscriber<List<Integer>> tester = new TestSubscriber<>();
		
		Observable<Integer> values = Observable.range(10,5);

		values
		    .reduce(
		        new ArrayList<Integer>(),
		        (acc, value) -> {
		            acc.add(value);
		            return acc;
		        })
		    .subscribe(tester);
		
		tester.assertReceivedOnNext(Arrays.asList(Arrays.asList(10, 11, 12, 13, 14)));
		tester.assertTerminalEvent();
		tester.assertNoErrors();
	}
	
	@Test
	public void testToList() {
		TestSubscriber<List<Integer>> tester = new TestSubscriber<>();
		
		Observable<Integer> values = Observable.range(10,5);

		values
		    .toList()
		    .subscribe(tester);
		
		tester.assertReceivedOnNext(Arrays.asList(Arrays.asList(10, 11, 12, 13, 14)));
		tester.assertTerminalEvent();
		tester.assertNoErrors();
	}
	
	@Test
	public void testToSortedList() {
		TestSubscriber<List<Integer>> tester = new TestSubscriber<>();
		
		Observable<Integer> values = Observable.range(10,5);

		values
		    .toSortedList((i1,i2) -> i2 - i1)
		    .subscribe(tester);
		
		tester.assertReceivedOnNext(Arrays.asList(Arrays.asList(14, 13, 12, 11, 10)));
		tester.assertTerminalEvent();
		tester.assertNoErrors();
	}
	
}


