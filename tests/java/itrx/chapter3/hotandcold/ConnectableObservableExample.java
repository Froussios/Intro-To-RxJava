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
package itrx.chapter3.hotandcold;

import java.util.Arrays;
import java.util.concurrent.TimeUnit;

import org.junit.Test;

import rx.Observable;
import rx.Subscription;
import rx.observables.ConnectableObservable;
import rx.observers.TestSubscriber;
import rx.schedulers.Schedulers;
import rx.schedulers.TestScheduler;

public class ConnectableObservableExample {

	public void exampleConnect() throws InterruptedException {
		ConnectableObservable<Long> cold = Observable.interval(200, TimeUnit.MILLISECONDS).publish();
		cold.connect();

		cold.subscribe(i -> System.out.println("First: " + i));
		Thread.sleep(500);
		cold.subscribe(i -> System.out.println("Second: " + i));
		
		// First: 0
		// First: 1
		// First: 2
		// Second: 2
		// First: 3
		// Second: 3
		// First: 4
		// Second: 4
		// First: 5
		// Second: 5
	}
	
	public void exampleDisconnect() throws InterruptedException {
		ConnectableObservable<Long> connectable = Observable.interval(200, TimeUnit.MILLISECONDS).publish();
		Subscription s = connectable.connect();

		connectable.subscribe(i -> System.out.println(i));

		Thread.sleep(1000);
		System.out.println("Closing connection");
		s.unsubscribe();

		Thread.sleep(1000);
		System.out.println("Reconnecting");
		s = connectable.connect();
		
		// 0
		// 1
		// 2
		// 3
		// 4
		// Closing connection
		// Reconnecting
		// 0
		// 1
		// 2
	}
	
	public void exampleUnsubscribe() throws InterruptedException {
		ConnectableObservable<Long> connectable = Observable.interval(200, TimeUnit.MILLISECONDS).publish();
		connectable.connect();

		connectable.subscribe(i -> System.out.println("First: " + i));
		Thread.sleep(500);
		Subscription s2 = connectable.subscribe(i -> System.out.println("Seconds: " + i));

		Thread.sleep(500);
		System.out.println("Unsubscribing second");
		s2.unsubscribe();
		
		// First: 0
		// First: 1
		// First: 2
		// Seconds: 2
		// First: 3
		// Seconds: 3
		// First: 4
		// Seconds: 4
		// Unsubscribing second
		// First: 5
		// First: 6
	}
	
	public void exampleRefcount() throws InterruptedException {
		Observable<Long> cold = Observable.interval(200, TimeUnit.MILLISECONDS).publish().refCount();

		Subscription s1 = cold.subscribe(i -> System.out.println("First: " + i));
		Thread.sleep(500);
		Subscription s2 = cold.subscribe(i -> System.out.println("Second: " + i));
		Thread.sleep(500);
		System.out.println("Unsubscribe first");
		s2.unsubscribe();
		Thread.sleep(500);
		System.out.println("Unsubscribe first");
		s1.unsubscribe();

		System.out.println("First connection again");
		Thread.sleep(500);
		s1 = cold.subscribe(i -> System.out.println("First: " + i));
		
		// First: 0
		// First: 1
		// First: 2
		// Second: 2
		// First: 3
		// Second: 3
		// Unsubscribe first
		// First: 4
		// First: 5
		// First: 6
		// Unsubscribe first
		// First connection again
		// First: 0
		// First: 1
		// First: 2
		// First: 3
		// First: 4
	}
	
	
	//
	// Test
	//
	
	@Test
	public void testConnect() throws InterruptedException {
		TestScheduler scheduler = Schedulers.test();
		TestSubscriber<Long> tester1 = new TestSubscriber<Long>();
		TestSubscriber<Long> tester2 = new TestSubscriber<Long>();
		
		ConnectableObservable<Long> cold = 
				Observable
					.interval(200, TimeUnit.MILLISECONDS, scheduler)
					.publish();
		Subscription connection = cold.connect();

		cold.subscribe(tester1);
		scheduler.advanceTimeTo(500, TimeUnit.MILLISECONDS);
		tester1.assertReceivedOnNext(Arrays.asList(0L, 1L));
		tester2.assertReceivedOnNext(Arrays.asList());
		
		cold.subscribe(tester2);
		scheduler.advanceTimeTo(1000, TimeUnit.MILLISECONDS);
		tester1.assertReceivedOnNext(Arrays.asList(0L, 1L, 2L, 3L, 4L));
		tester2.assertReceivedOnNext(Arrays.asList(2L, 3L, 4L));
		
		connection.unsubscribe();
	}
	
	@Test
	public void testDisconnect() throws InterruptedException {
		TestScheduler scheduler = Schedulers.test();
		TestSubscriber<Long> tester = new TestSubscriber<Long>();
		
		ConnectableObservable<Long> connectable = 
				Observable
					.interval(200, TimeUnit.MILLISECONDS, scheduler)
					.publish();
		Subscription connection = connectable.connect();
		connectable.subscribe(tester);

		scheduler.advanceTimeBy(1000, TimeUnit.MILLISECONDS);
		tester.assertReceivedOnNext(Arrays.asList(0L, 1L, 2L, 3L, 4L));
		
		connection.unsubscribe();
		scheduler.advanceTimeBy(1000, TimeUnit.MILLISECONDS);
		tester.assertReceivedOnNext(Arrays.asList(0L, 1L, 2L, 3L, 4L));
		
		connection = connectable.connect();
		scheduler.advanceTimeBy(1000, TimeUnit.MILLISECONDS);
		tester.assertReceivedOnNext(Arrays.asList(0L, 1L, 2L, 3L, 4L, 0L, 1L, 2L, 3L, 4L));
		
		connection.unsubscribe();
	}
	
	@Test
	public void testUnsubscribe() throws InterruptedException {
		TestScheduler scheduler = Schedulers.test();
		TestSubscriber<Long> tester1 = new TestSubscriber<Long>();
		TestSubscriber<Long> tester2 = new TestSubscriber<Long>();
		
		ConnectableObservable<Long> connectable = 
				Observable
					.interval(200, TimeUnit.MILLISECONDS, scheduler)
					.publish();
		Subscription conSubscription = connectable.connect();

		connectable.subscribe(tester1);
		scheduler.advanceTimeBy(500, TimeUnit.MILLISECONDS);
		tester1.assertReceivedOnNext(Arrays.asList(0L, 1L));
		tester2.assertReceivedOnNext(Arrays.asList());
		
		Subscription s2 = connectable.subscribe(tester2);
		scheduler.advanceTimeBy(500, TimeUnit.MILLISECONDS);
		tester1.assertReceivedOnNext(Arrays.asList(0L, 1L, 2L, 3L, 4L));
		tester2.assertReceivedOnNext(Arrays.asList(2L, 3L, 4L));
		
		s2.unsubscribe();
		scheduler.advanceTimeBy(500, TimeUnit.MILLISECONDS);
		tester1.assertReceivedOnNext(Arrays.asList(0L, 1L, 2L, 3L, 4L, 5L, 6L));
		tester2.assertReceivedOnNext(Arrays.asList(2L, 3L, 4L));
		
		conSubscription.unsubscribe();
	}
	
	@Test
	public void testRefcount() throws InterruptedException {
		TestScheduler scheduler = Schedulers.test();
		TestSubscriber<Long> tester1 = new TestSubscriber<Long>();
		TestSubscriber<Long> tester2 = new TestSubscriber<Long>();
		TestSubscriber<Long> tester3 = new TestSubscriber<Long>();
		
		Observable<Long> cold = 
				Observable
					.interval(200, TimeUnit.MILLISECONDS, scheduler)
					.publish()
					.refCount();

		Subscription s1 = cold.subscribe(tester1);
		scheduler.advanceTimeBy(500, TimeUnit.MILLISECONDS);
		tester1.assertReceivedOnNext(Arrays.asList(0L, 1L));
		tester2.assertReceivedOnNext(Arrays.asList());
		tester3.assertReceivedOnNext(Arrays.asList());
		
		Subscription s2 = cold.subscribe(tester2);
		scheduler.advanceTimeBy(500, TimeUnit.MILLISECONDS);
		tester1.assertReceivedOnNext(Arrays.asList(0L, 1L, 2L, 3L, 4L));
		tester2.assertReceivedOnNext(Arrays.asList(2L, 3L, 4L));
		tester3.assertReceivedOnNext(Arrays.asList());
		
		s2.unsubscribe();
		scheduler.advanceTimeBy(500, TimeUnit.MILLISECONDS);
		tester1.assertReceivedOnNext(Arrays.asList(0L, 1L, 2L, 3L, 4L, 5L, 6L));
		tester2.assertReceivedOnNext(Arrays.asList(2L, 3L, 4L));
		tester3.assertReceivedOnNext(Arrays.asList());
		
		s1.unsubscribe();
		Subscription s3 = cold.subscribe(tester3);
		scheduler.advanceTimeBy(500, TimeUnit.MILLISECONDS);
		tester1.assertReceivedOnNext(Arrays.asList(0L, 1L, 2L, 3L, 4L, 5L, 6L));
		tester2.assertReceivedOnNext(Arrays.asList(2L, 3L, 4L));
		tester3.assertReceivedOnNext(Arrays.asList(0L, 1L));
		
		s3.unsubscribe();
	}

}
