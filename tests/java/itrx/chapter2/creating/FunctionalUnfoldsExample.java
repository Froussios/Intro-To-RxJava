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

import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.util.Arrays;
import java.util.concurrent.TimeUnit;

import org.junit.Test;

import rx.Observable;
import rx.Subscription;
import rx.observers.TestSubscriber;
import rx.schedulers.Schedulers;
import rx.schedulers.TestScheduler;

public class FunctionalUnfoldsExample {
	
	public void exampleRange() {
		Observable<Integer> values = Observable.range(10, 15);
		values.subscribe(System.out::println);
		
		// 10
		// ...
		// 24
	}
	
	public void exampleInterval() throws IOException {
		Observable<Long> values = Observable.interval(1000, TimeUnit.MILLISECONDS);
		values.subscribe(
		    v -> System.out.println("Received: " + v),
		    e -> System.out.println("Error: " + e),
		    () -> System.out.println("Completed")
		);
		System.in.read();
		
		// Received: 0
		// Received: 1
		// Received: 2
		// Received: 3
		// ...
	}
	
	public void exampleTimer() throws IOException {
		Observable<Long> values = Observable.timer(1, TimeUnit.SECONDS);
		values.subscribe(
		    v -> System.out.println("Received: " + v),
		    e -> System.out.println("Error: " + e),
		    () -> System.out.println("Completed")
		);
		System.in.read();
		
		// Received: 0
		// Completed
	}
	
	public void exampleTimerWithRepeat() throws IOException {
		Observable<Long> values = Observable.timer(2, 1, TimeUnit.SECONDS);
		values.subscribe(
		    v -> System.out.println("Received: " + v),
		    e -> System.out.println("Error: " + e),
		    () -> System.out.println("Completed")
		);
		System.in.read();
		
		// Received: 0
		// Received: 1
		// Received: 2
		// ...
	}
	
	
	//
	// Tests
	//

	@Test
	public void testRange() {
		TestSubscriber<Integer> tester = new TestSubscriber<Integer>();
		
		Observable<Integer> values = Observable.range(10, 15);
		values.subscribe(tester);
		
		tester.assertReceivedOnNext(Arrays.asList(10,11,12,13,14,15,16,17,18,19,20,21,22,23,24));
		tester.assertTerminalEvent();
		tester.assertNoErrors();
	}
	
	@Test
	public void testInterval() {
		TestSubscriber<Long> tester = new TestSubscriber<Long>();
		TestScheduler scheduler = Schedulers.test();
		
		Observable<Long> values = Observable.interval(1000, TimeUnit.MILLISECONDS, scheduler);
		Subscription subscription = values.subscribe(tester);
		scheduler.advanceTimeBy(4500, TimeUnit.MILLISECONDS);
		
		tester.assertReceivedOnNext(Arrays.asList(0L, 1L, 2L, 3L));
		tester.assertNoErrors();
		assertEquals(tester.getOnCompletedEvents().size(), 0);
		
		subscription.unsubscribe();
	}
	
	@Test
	public void testTimer() {
		TestSubscriber<Long> tester = new TestSubscriber<Long>();
		TestScheduler scheduler = Schedulers.test();
		
		Observable<Long> values = Observable.timer(1, TimeUnit.SECONDS, scheduler);
		Subscription subscription = values.subscribe(tester);
		scheduler.advanceTimeBy(1, TimeUnit.SECONDS);
		
		tester.assertReceivedOnNext(Arrays.asList(0L));
		tester.assertNoErrors();
		tester.assertTerminalEvent();
		
		subscription.unsubscribe();
	}
	
	@Test
	public void testTimerWithRepeat() {
		TestSubscriber<Long> tester = new TestSubscriber<Long>();
		TestScheduler scheduler = Schedulers.test();
		
		Observable<Long> values = Observable.timer(2, 1, TimeUnit.SECONDS, scheduler);
		Subscription subscription = values.subscribe(tester);
		
		scheduler.advanceTimeBy(6, TimeUnit.SECONDS);
		
		tester.assertReceivedOnNext(Arrays.asList(0L,1L,2L,3L,4L));
		tester.assertNoErrors();
		assertEquals(tester.getOnCompletedEvents().size(), 0); // Hasn't terminated
		
		subscription.unsubscribe();
	}

}
