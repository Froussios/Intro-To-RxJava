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

public class DebounceExample {

	public void exampleDebounce() {
		Observable.concat(
		        Observable.interval(100, TimeUnit.MILLISECONDS).take(3),
		        Observable.interval(500, TimeUnit.MILLISECONDS).take(3),
		        Observable.interval(100, TimeUnit.MILLISECONDS).take(3)
		    )
		    .scan(0, (acc, v) -> acc+1)
		    .debounce(150, TimeUnit.MILLISECONDS)
		    .subscribe(System.out::println);
		
		// 3
		// 4
		// 5
		// 9
	}
	
	public void exampleDebounceDynamic() {
		Observable.concat(
		        Observable.interval(100, TimeUnit.MILLISECONDS).take(3),
		        Observable.interval(500, TimeUnit.MILLISECONDS).take(3),
		        Observable.interval(100, TimeUnit.MILLISECONDS).take(3)
		    )
		    .scan(0, (acc, v) -> acc+1)
		    .debounce(i -> Observable.timer(i * 50, TimeUnit.MILLISECONDS))
		    .subscribe(System.out::println);
		
		// 1
		// 3
		// 4
		// 5
		// 9
	}
	
	public void exampleThrottleWithTimeout() {
		Observable.concat(
		        Observable.interval(100, TimeUnit.MILLISECONDS).take(3),
		        Observable.interval(500, TimeUnit.MILLISECONDS).take(3),
		        Observable.interval(100, TimeUnit.MILLISECONDS).take(3)
		    )
		    .scan(0, (acc, v) -> acc+1)
		    .throttleWithTimeout(150, TimeUnit.MILLISECONDS)
		    .subscribe(System.out::println);
		
		// 3
		// 4
		// 5
		// 9
	}
	
	
	//
	// Test
	//
	
	@Test
	public void testDebounce() {
		TestScheduler scheduler = Schedulers.test();
		TestSubscriber<Integer> tester = new TestSubscriber<>();
		
		Observable.concat(
		        Observable.interval(100, TimeUnit.MILLISECONDS, scheduler).take(3),
		        Observable.interval(500, TimeUnit.MILLISECONDS, scheduler).take(3),
		        Observable.interval(100, TimeUnit.MILLISECONDS, scheduler).take(3)
		    )
		    .scan(0, (acc, v) -> acc+1)
		    .debounce(150, TimeUnit.MILLISECONDS, scheduler)
		    .subscribe(tester);
		
		scheduler.advanceTimeBy(2100, TimeUnit.MILLISECONDS);
		tester.assertReceivedOnNext(Arrays.asList(3, 4, 5, 9));
	}
	
	@Test
	public void testDebounceDynamic() {
		TestScheduler scheduler = Schedulers.test();
		TestSubscriber<Integer> tester = new TestSubscriber<>();
		
		Observable.concat(
		        Observable.interval(100, TimeUnit.MILLISECONDS, scheduler).take(3),
		        Observable.interval(500, TimeUnit.MILLISECONDS, scheduler).take(3),
		        Observable.interval(100, TimeUnit.MILLISECONDS, scheduler).take(3)
		    )
		    .scan(0, (acc, v) -> acc+1)
		    .debounce(i -> Observable.timer(i * 50, TimeUnit.MILLISECONDS, scheduler))
		    .subscribe(tester);
		
		scheduler.advanceTimeBy(2100, TimeUnit.MILLISECONDS);
		tester.assertReceivedOnNext(Arrays.asList(1, 2, 3, 4, 5, 9));
	}
	
	@Test
	public void testThrottleWithTimeout() {
		TestScheduler scheduler = Schedulers.test();
		TestSubscriber<Integer> tester = new TestSubscriber<>();
		
		Observable.concat(
		        Observable.interval(100, TimeUnit.MILLISECONDS, scheduler).take(3),
		        Observable.interval(500, TimeUnit.MILLISECONDS, scheduler).take(3),
		        Observable.interval(100, TimeUnit.MILLISECONDS, scheduler).take(3)
		    )
		    .scan(0, (acc, v) -> acc+1)
		    .throttleWithTimeout(150, TimeUnit.MILLISECONDS, scheduler)
		    .subscribe(tester);
		
		scheduler.advanceTimeBy(2100, TimeUnit.MILLISECONDS);
		tester.assertReceivedOnNext(Arrays.asList(3, 4, 5, 9));
	}
}