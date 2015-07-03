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
package itrx.chapter2.inspection;

import static org.junit.Assert.assertEquals;

import java.util.Arrays;
import java.util.concurrent.TimeUnit;

import org.junit.Test;

import rx.Observable;
import rx.Subscription;
import rx.observers.TestSubscriber;
import rx.schedulers.Schedulers;
import rx.schedulers.TestScheduler;

public class AllExample {

	public void exampleAll() {
		Observable<Integer> values = Observable.create(o -> {
		    o.onNext(0);
		    o.onNext(10);
		    o.onNext(10);
		    o.onNext(2);
		    o.onCompleted();
		});


		values
		    .all(i -> i % 2 == 0)
		    .subscribe(
		        v -> System.out.println(v),
		        e -> System.out.println("Error: " + e),
		        () -> System.out.println("Completed")
		    );
		
		// true
		// Completed
	}
	
	public void exampleAllEarlyFalse() {
		Observable<Long> values = Observable.interval(150, TimeUnit.MILLISECONDS).take(5);

		Subscription subscription = values
		    .all(i -> i<3)
		    .subscribe(
		        v -> System.out.println("All: " + v),
		        e -> System.out.println("All: Error: " + e),
		        () -> System.out.println("All: Completed")
		    );
		Subscription subscription2 = values
		    .subscribe(
		        v -> System.out.println(v),
		        e -> System.out.println("Error: " + e),
		        () -> System.out.println("Completed")
		    );
		
		subscription.unsubscribe();
		subscription2.unsubscribe();
		
		// 0
		// 1
		// 2
		// All: false
		// All: Completed
		// 3
		// 4
		// Completed
	}
	
	public void exampleAllError() {
		Observable<Integer> values = Observable.create(o -> {
		    o.onNext(0);
		    o.onNext(2);
		    o.onError(new Exception());
		});

		values
		    .all(i -> i % 2 == 0)
		    .subscribe(
		        v -> System.out.println(v),
		        e -> System.out.println("Error: " + e),
		        () -> System.out.println("Completed")
		    );
		
		// Error: java.lang.Exception
	}
	
	public void exampleAllErrorAfterComplete() {
		Observable<Integer> values = Observable.create(o -> {
		    o.onNext(1);
		    o.onNext(2);
		    o.onError(new Exception());
		});

		values
		    .all(i -> i % 2 == 0)
		    .subscribe(
		        v -> System.out.println(v),
		        e -> System.out.println("Error: " + e),
		        () -> System.out.println("Completed")
		    );
		
		// false
		// Completed
	}
	
	
	
	//
	// Tests for examples
	//
	
	@Test
	public void testAll() {
		TestSubscriber<Boolean> tester = new TestSubscriber<Boolean>();
		
		Observable<Integer> values = Observable.create(o -> {
		    o.onNext(0);
		    o.onNext(10);
		    o.onNext(10);
		    o.onNext(2);
		    o.onCompleted();
		});


		values
		    .all(i -> i % 2 == 0)
		    .subscribe(tester);
		
		tester.assertReceivedOnNext(Arrays.asList(true));
		tester.assertTerminalEvent();
		tester.assertNoErrors();
	}
	
	@Test
	public void testAllEarlyFalse() {
		TestSubscriber<Long> testerSrc = new TestSubscriber<Long>();
		TestSubscriber<Boolean> testerAll = new TestSubscriber<Boolean>();
		TestScheduler scheduler = Schedulers.test();
		
		Observable<Long> values = 
				Observable.interval(100, TimeUnit.MILLISECONDS, scheduler)
				.take(5);

		Subscription subscription = values
		    .all(i -> i<3)
		    .subscribe(testerAll);
		Subscription subscription2 = values
		    .subscribe(testerSrc);
		
		scheduler.advanceTimeBy(450, TimeUnit.MILLISECONDS);
		
		testerAll.assertReceivedOnNext(Arrays.asList(false));
		testerAll.assertTerminalEvent();
		testerAll.assertNoErrors();
		testerSrc.assertReceivedOnNext(Arrays.asList(0L, 1L, 2L, 3L));
		
		scheduler.advanceTimeBy(500, TimeUnit.MILLISECONDS);
		
		testerSrc.assertReceivedOnNext(Arrays.asList(0L, 1L, 2L, 3L, 4L));
		testerSrc.assertTerminalEvent();
		testerSrc.assertNoErrors();
		
		subscription.unsubscribe();
		subscription2.unsubscribe();
	}
	
	@Test
	public void testAllError() {
		TestSubscriber<Boolean> tester = new TestSubscriber<>();
		
		Observable<Integer> values = Observable.create(o -> {
		    o.onNext(0);
		    o.onNext(2);
		    o.onError(new Exception());
		});

		values
		    .all(i -> i % 2 == 0)
		    .subscribe(tester);
		
		tester.assertReceivedOnNext(Arrays.asList());
		tester.assertTerminalEvent();
		assertEquals(tester.getOnErrorEvents().size(), 1);
	}
	
	@Test
	public void testAllErrorAfterComplete() {
		TestSubscriber<Boolean> tester = new TestSubscriber<>();
		
		Observable<Integer> values = Observable.create(o -> {
		    o.onNext(1);
		    o.onNext(2);
		    o.onError(new Exception());
		});

		values
		    .all(i -> i % 2 == 0)
		    .subscribe(tester);
		
		tester.assertReceivedOnNext(Arrays.asList(false));
		tester.assertTerminalEvent();
		tester.assertNoErrors();
	}

}
