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

public class ReplayExample {

	public void exampleReplay() throws InterruptedException {
		ConnectableObservable<Long> cold = Observable.interval(200, TimeUnit.MILLISECONDS).replay();
		cold.connect();

		System.out.println("Subscribe first");
		cold.subscribe(i -> System.out.println("First: " + i));
		Thread.sleep(700);
		System.out.println("Subscribe second");
		cold.subscribe(i -> System.out.println("Second: " + i));
		Thread.sleep(500);
		
		// Subscribe first
		// First: 0
		// First: 1
		// First: 2
		// Subscribe second
		// Second: 0
		// Second: 1
		// Second: 2
		// First: 3
		// Second: 3
	}
	
	public void exampleReplayWithBufferSize() throws InterruptedException {
		ConnectableObservable<Long> source = Observable.interval(1000, TimeUnit.MILLISECONDS)
			.take(5)
			.replay(2);

		source.connect();
		Thread.sleep(4500);
		source.subscribe(System.out::println);
		
		// 2
		// 3
		// 4
	}
	
	public void exampleReplayWithTime() throws InterruptedException {
		ConnectableObservable<Long> source = Observable.interval(1000, TimeUnit.MILLISECONDS)
			.take(5)
			.replay(2000, TimeUnit.MILLISECONDS);

		source.connect();
		Thread.sleep(4500);
		source.subscribe(System.out::println);
		
		// 2
		// 3
		// 4
	}
	
	
	//
	// Test
	//
	
	@Test
	public void testReplay() throws InterruptedException {
		TestScheduler scheduler = Schedulers.test();
		TestSubscriber<Long> tester1 = new TestSubscriber<>();
		TestSubscriber<Long> tester2 = new TestSubscriber<>();
		
		ConnectableObservable<Long> cold = 
				Observable
					.interval(200, TimeUnit.MILLISECONDS, scheduler)
					.replay();
		Subscription connection = cold.connect();

		cold.subscribe(tester1);
		scheduler.advanceTimeBy(700, TimeUnit.MILLISECONDS);
		tester1.assertReceivedOnNext(Arrays.asList(0L, 1L, 2L));
		tester2.assertReceivedOnNext(Arrays.asList());
		
		cold.subscribe(tester2);
		tester1.assertReceivedOnNext(Arrays.asList(0L, 1L, 2L));
		tester2.assertReceivedOnNext(Arrays.asList(0L, 1L, 2L));
		scheduler.advanceTimeBy(500, TimeUnit.MILLISECONDS);
		tester1.assertReceivedOnNext(Arrays.asList(0L, 1L, 2L, 3L, 4L, 5L));
		tester2.assertReceivedOnNext(Arrays.asList(0L, 1L, 2L, 3L, 4L, 5L));
		
		connection.unsubscribe();
	}
	
	@Test
	public void testReplayWithBufferSize() {
		TestScheduler scheduler = Schedulers.test();
		TestSubscriber<Long> tester = new TestSubscriber<>();
		
		ConnectableObservable<Long> source = Observable.interval(1000, TimeUnit.MILLISECONDS, scheduler)
			.take(5)
			.replay(2, scheduler);

		source.connect();
		scheduler.advanceTimeBy(4500, TimeUnit.MILLISECONDS);
		source.subscribe(tester);
		scheduler.triggerActions();
		tester.assertReceivedOnNext(Arrays.asList(2L, 3L));
		scheduler.advanceTimeBy(500, TimeUnit.MILLISECONDS);
		tester.assertReceivedOnNext(Arrays.asList(2L, 3L, 4L));
	}
	
	@Test
	public void testReplayWithTime() throws InterruptedException {
		TestScheduler scheduler = Schedulers.test();
		TestSubscriber<Long> tester = new TestSubscriber<>();
		
		ConnectableObservable<Long> source = Observable.interval(1000, TimeUnit.MILLISECONDS, scheduler)
			.take(5)
			.replay(2000, TimeUnit.MILLISECONDS, scheduler);

		source.connect();
		scheduler.advanceTimeBy(4500, TimeUnit.MILLISECONDS);
		source.subscribe(tester);
		tester.assertReceivedOnNext(Arrays.asList(2L, 3L));
		scheduler.advanceTimeBy(500, TimeUnit.MILLISECONDS);
		tester.assertReceivedOnNext(Arrays.asList(2L, 3L, 4L));
		
//		2
//		3
//		4
	}

}
