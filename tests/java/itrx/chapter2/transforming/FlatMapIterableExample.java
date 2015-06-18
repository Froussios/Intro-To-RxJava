package itrx.chapter2.transforming;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import org.junit.Test;

import rx.Observable;
import rx.observers.TestSubscriber;

public class FlatMapIterableExample {
	
	public static class Range implements Iterable<Integer> {

		private static class RangeIterator implements Iterator<Integer> {
			private int next;
			private final int end;
			 
			RangeIterator(int start, int count) {
				this.next = start;
				this.end = start + count;
			}
			
			@Override
			public boolean hasNext() {
				return next < end;
			}

			@Override
			public Integer next() {
				return next++;
			}
			
		}
		
		private final int start;
		private final int count;
		
		public Range(int start, int count) {
			this.start = start;
			this.count = count;
		}
		
		@Override
		public Iterator<Integer> iterator() {
			return new RangeIterator(start, count);
		}
	}
	
	public static Iterable<Integer> range(int start, int count) {
		List<Integer> list = new ArrayList<>();
		for (int i=start ; i<start+count ; i++) {
			list.add(i);
		}
		return list;
	}
	
	public void exampleFlatMapIterable() {
		Observable.range(1, 3)
			.flatMapIterable(i -> range(1, i))
			.subscribe(System.out::println);
		
		// 1
		// 1
		// 2
		// 1
		// 2
		// 3
	}
	
	public void exampleFlatMapIterableWithSelector() {
		Observable.range(1, 3)
			.flatMapIterable(
				i -> range(1, i),
				(ori, rv) -> ori * rv)
			.subscribe(System.out::println);
		
		// 1
		// 2
		// 4
		// 3
		// 6
		// 9
	}
	
	public void exampleFlatMapLazyIterable() {
		Observable.range(1, 3)
			.flatMapIterable(
				i -> new Range(1, i),
				(ori, rv) -> ori * rv)
			.subscribe(System.out::println);
	
	// 1
	// 2
	// 4
	// 3
	// 6
	// 9
	}
	
	
	//
	// Test
	//
	
	@Test
	public void testFlatMapIterable() {
		TestSubscriber<Integer> tester = new TestSubscriber<>();
		
		Observable.range(1, 3)
			.flatMapIterable(i -> range(1, i))
			.subscribe(tester);
		
		tester.assertReceivedOnNext(Arrays.asList(1,1,2,1,2,3));
		tester.assertTerminalEvent();
		tester.assertNoErrors();
	}
	
	@Test
	public void testFlatMapIterableWithSelector() {
		TestSubscriber<Integer> tester = new TestSubscriber<>();
		
		Observable.range(1, 3)
			.flatMapIterable(
				i -> range(1, i),
				(ori, rv) -> ori * rv)
			.subscribe(tester);
		
		tester.assertReceivedOnNext(Arrays.asList(1,2,4,3,6,9));
		tester.assertTerminalEvent();
		tester.assertNoErrors();
	}
	
	@Test
	public void testFlatMapLazyIterable() {
		TestSubscriber<Integer> tester = new TestSubscriber<>();
		
		Observable.range(1, 3)
			.flatMapIterable(
				i -> new Range(1, i),
				(ori, rv) -> ori * rv)
			.subscribe(tester);
		
		tester.assertReceivedOnNext(Arrays.asList(1,2,4,3,6,9));
		tester.assertTerminalEvent();
		tester.assertNoErrors();
	}

}
