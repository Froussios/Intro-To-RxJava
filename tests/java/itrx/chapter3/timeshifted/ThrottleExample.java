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
package itrx.chapter3.timeshifted;

import java.util.Arrays;
import java.util.concurrent.TimeUnit;

import org.junit.Test;

import rx.Observable;
import rx.observers.TestSubscriber;
import rx.schedulers.Schedulers;
import rx.schedulers.TestScheduler;

public class ThrottleExample {

	public void exampleThrottleFirst() {
		Observable.interval(150, TimeUnit.MILLISECONDS)
		    .throttleFirst(1, TimeUnit.SECONDS)
		    .take(3)
		    .subscribe(System.out::println);
		
		// 0
		// 7
		// 14
	}
	
	public void exampleThrottleLast() {
		Observable.interval(150, TimeUnit.MILLISECONDS)
		    .throttleLast(1, TimeUnit.SECONDS)
		    .take(3)
		    .subscribe(System.out::println);
		
		// 5
		// 12
		// 18
	}
	
	
	//
	// Test
	//
	
	@Test
	public void testThrottleFirst() {
		TestScheduler scheduler = Schedulers.test();
		TestSubscriber<Long> tester = new TestSubscriber<>();

		Observable.interval(150, TimeUnit.MILLISECONDS, scheduler)
		    .throttleFirst(1, TimeUnit.SECONDS, scheduler)
		    .take(3)
		    .subscribe(tester);
		
		scheduler.advanceTimeBy(3, TimeUnit.SECONDS);
		tester.assertReceivedOnNext(Arrays.asList(0L, 7L, 14L));
	}
	
	@Test
	public void testThrottleLast() {
		TestScheduler scheduler = Schedulers.test();
		TestSubscriber<Long> tester = new TestSubscriber<>();
		
		Observable.interval(150, TimeUnit.MILLISECONDS, scheduler)
		    .throttleLast(1, TimeUnit.SECONDS, scheduler)
		    .take(3)
		    .subscribe(tester);
		
		scheduler.advanceTimeBy(3, TimeUnit.SECONDS);
		tester.assertReceivedOnNext(Arrays.asList(5L, 12L, 18L));
	}
}
