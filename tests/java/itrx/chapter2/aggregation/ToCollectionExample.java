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
package itrx.chapter2.aggregation;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.Test;

import rx.Observable;
import rx.observers.TestSubscriber;

public class ToCollectionExample {
	
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
		
		// [10, 11, 12, 13, 14]
	}
	
	public void exampleToList() {
		Observable<Integer> values = Observable.range(10,5);

		values
		    .toList()
		    .subscribe(v -> System.out.println(v));
		
		// [10, 11, 12, 13, 14]
	}
	
	public void exampleToSortedList() {
		Observable<Integer> values = Observable.range(10,5);

		values
		    .toSortedList((i1,i2) -> i2 - i1)
		    .subscribe(v -> System.out.println(v));
		
		// [14, 13, 12, 11, 10]
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


