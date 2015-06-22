/*******************************************************************************
 * Copyright (c) 2015 Christos Froussios
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 *
 *******************************************************************************/
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
