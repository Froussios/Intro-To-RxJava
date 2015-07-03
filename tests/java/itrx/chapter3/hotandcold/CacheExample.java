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
import rx.observers.TestSubscriber;
import rx.schedulers.Schedulers;
import rx.schedulers.TestScheduler;

public class CacheExample {

	public void exampleCache() throws InterruptedException {
		Observable<Long> obs = Observable.interval(100, TimeUnit.MILLISECONDS)
			.take(5)
			.cache();

		Thread.sleep(500);
		obs.subscribe(i -> System.out.println("First: " + i));
		Thread.sleep(300);
		obs.subscribe(i -> System.out.println("Second: " + i));
		
		// First: 0
		// First: 1
		// First: 2
		// Second: 0
		// Second: 1
		// Second: 2
		// First: 3
		// Second: 3
		// First: 4
		// Second: 4
	}
	
	public void exampleCacheUnsubscribe() throws InterruptedException {
		Observable<Long> obs = Observable.interval(100, TimeUnit.MILLISECONDS)
			.take(5)
			.doOnNext(System.out::println)
			.cache()
			.doOnSubscribe(() -> System.out.println("Subscribed"))
			.doOnUnsubscribe(() -> System.out.println("Unsubscribed"));

		Subscription subscription = obs.subscribe();
		Thread.sleep(150);
		subscription.unsubscribe();
			
		// Subscribed
		// 0
		// Unsubscribed
		// 1
		// 2
		// 3
		// 4
	}
	
	
	//
	// Tests
	//
	
	@Test
	public void testCache() throws InterruptedException {
		TestSubscriber<Long> tester1 = new TestSubscriber<Long>();
		TestSubscriber<Long> tester2 = new TestSubscriber<Long>();
		TestScheduler scheduler = Schedulers.test();
		
		Observable<Long> obs = Observable.interval(100, TimeUnit.MILLISECONDS, scheduler)
			.take(5)
			.cache();

		tester1.assertReceivedOnNext(Arrays.asList());
		tester2.assertReceivedOnNext(Arrays.asList());
		
		scheduler.advanceTimeBy(500, TimeUnit.MILLISECONDS);
		obs.subscribe(tester1);
		tester1.assertReceivedOnNext(Arrays.asList());
		tester2.assertReceivedOnNext(Arrays.asList());
		
		scheduler.advanceTimeBy(300, TimeUnit.MILLISECONDS);
		tester1.assertReceivedOnNext(Arrays.asList(0L, 1L, 2L));
		tester2.assertReceivedOnNext(Arrays.asList());
		
		obs.subscribe(tester2);
		tester1.assertReceivedOnNext(Arrays.asList(0L, 1L, 2L));
		tester2.assertReceivedOnNext(Arrays.asList(0L, 1L, 2L));
		
		scheduler.advanceTimeBy(200, TimeUnit.MILLISECONDS);
		tester1.assertReceivedOnNext(Arrays.asList(0L, 1L, 2L, 3L, 4L));
		tester2.assertReceivedOnNext(Arrays.asList(0L, 1L, 2L, 3L, 4L));
	}
	
	@Test
	public void testCacheUnsubscribe() throws InterruptedException {
		TestSubscriber<Long> tester = new TestSubscriber<Long>();
		TestScheduler scheduler = Schedulers.test();
		
		Observable<Long> obs = Observable.interval(100, TimeUnit.MILLISECONDS, scheduler)
			.take(5)
			.doOnEach(tester)
			.cache();

		Subscription subscription = obs.subscribe();
		scheduler.advanceTimeBy(150, TimeUnit.MILLISECONDS);
		tester.assertReceivedOnNext(Arrays.asList(0L));
		
		subscription.unsubscribe();
		scheduler.advanceTimeBy(350, TimeUnit.MILLISECONDS);
		tester.assertReceivedOnNext(Arrays.asList(0L, 1L, 2L, 3L, 4L));
	}

}
