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
package itrx.chapter3.error;

import java.util.Arrays;
import java.util.concurrent.TimeUnit;

import org.junit.Test;

import rx.Observable;
import rx.observers.TestSubscriber;
import rx.schedulers.Schedulers;
import rx.schedulers.TestScheduler;

public class RetryWhenExample {
	
	public void example() {
		Observable<Integer> source = Observable.create(o -> {
			o.onNext(1);
			o.onNext(2);
			o.onError(new Exception("Failed"));
		});
		
		source.retryWhen((o) -> o
				.take(2)
				.delay(100, TimeUnit.MILLISECONDS))
			.timeInterval()
			.subscribe(
				System.out::println,
				System.out::println);
		
		// TimeInterval [intervalInMilliseconds=17, value=1]
		// TimeInterval [intervalInMilliseconds=0, value=2]
		// TimeInterval [intervalInMilliseconds=102, value=1]
		// TimeInterval [intervalInMilliseconds=0, value=2]
		// TimeInterval [intervalInMilliseconds=102, value=1]
		// TimeInterval [intervalInMilliseconds=0, value=2]
	}
	
	
	//
	// Test
	//
	
	@Test
	public void test() {
		TestScheduler scheduler = Schedulers.test();
		TestSubscriber<Long> intervals = new TestSubscriber<>();
		
		Observable<Integer> source = Observable.create(o -> {
			o.onNext(1);
			o.onNext(2);
			o.onError(new Exception("Failed"));
		});
		source.retryWhen((o) -> o
				.take(2)
				.delay(100, TimeUnit.MILLISECONDS, scheduler)
				, scheduler)
			.timeInterval(scheduler)
			.map(i -> i.getIntervalInMilliseconds())
			.subscribe(intervals);
		
		scheduler.advanceTimeBy(200, TimeUnit.MILLISECONDS);
		intervals.assertReceivedOnNext(Arrays.asList(0L, 0L, 100L, 0L, 100L, 0L));
		intervals.assertTerminalEvent();
		intervals.assertNoErrors();
	}
}
