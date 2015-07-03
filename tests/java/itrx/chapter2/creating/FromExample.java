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
package itrx.chapter2.creating;

import java.util.Arrays;
import java.util.concurrent.FutureTask;
import java.util.concurrent.TimeUnit;

import org.junit.Test;

import rx.Observable;
import rx.observers.TestSubscriber;

public class FromExample {
	
	public void exampleFromFuture() {
		FutureTask<Integer> f = new FutureTask<Integer>(() -> {
		    Thread.sleep(2000);
		    return 21;
		});
		new Thread(f).start();

		Observable<Integer> values = Observable.from(f);

		values.subscribe(
		    v -> System.out.println("Received: " + v),
		    e -> System.out.println("Error: " + e),
		    () -> System.out.println("Completed")
		);
		
		// Received: 21
		// Completed
	}
	
	public void exampleFromFutureTimeout() {
		FutureTask<Integer> f = new FutureTask<Integer>(() -> {
		    Thread.sleep(2000);
		    return 21;
		});
		new Thread(f).start();

		Observable<Integer> values = Observable.from(f, 1000, TimeUnit.MILLISECONDS);

		values.subscribe(
		    v -> System.out.println("Received: " + v),
		    e -> System.out.println("Error: " + e),
		    () -> System.out.println("Completed")
		);
		
		// Error: java.util.concurrent.TimeoutException
	}

	public void exampleFromArray() {
		Integer[] is = {1,2,3};
		Observable<Integer> values = Observable.from(is);
		values.subscribe(
		    v -> System.out.println("Received: " + v),
		    e -> System.out.println("Error: " + e),
		    () -> System.out.println("Completed")
		);
		
		// Received: 1
		// Received: 2
		// Received: 3
		// Completed
	}
	
	public void exampleFromIterable() {
		Iterable<Integer> input = Arrays.asList(1,2,3);
		Observable<Integer> values = Observable.from(input);
		values.subscribe(
		    v -> System.out.println("Received: " + v),
		    e -> System.out.println("Error: " + e),
		    () -> System.out.println("Completed")
		);
		
		// Received: 1
		// Received: 2
		// Received: 3
		// Completed
	}
	
	
	//
	// Tests
	//
	
	@Test
	public void testFromFuture() {
		TestSubscriber<Integer> tester = new TestSubscriber<Integer>();
		
		FutureTask<Integer> f = new FutureTask<Integer>(() -> {
		    return 21;
		});
		new Thread(f).start();

		Observable<Integer> values = Observable.from(f);

		values.subscribe(tester);
		
		tester.assertReceivedOnNext(Arrays.asList(21));
		tester.assertNoErrors();
		tester.assertTerminalEvent();
	}
	
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
