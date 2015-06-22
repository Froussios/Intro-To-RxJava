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

public class DelayExample {

	public void exampleDelay() {
		Observable.interval(100, TimeUnit.MILLISECONDS)
		    .delay(i -> Observable.timer(i * 100, TimeUnit.MILLISECONDS))
		    .timeInterval()
		    .take(5)
		    .subscribe(System.out::println);
		
		// TimeInterval [intervalInMilliseconds=152, value=0]
		// TimeInterval [intervalInMilliseconds=173, value=1]
		// TimeInterval [intervalInMilliseconds=199, value=2]
		// TimeInterval [intervalInMilliseconds=201, value=3]
		// TimeInterval [intervalInMilliseconds=199, value=4]
	}
	
	public void exampleDelaySubscription() {
		Observable.interval(100, TimeUnit.MILLISECONDS)
		    .delaySubscription(1000, TimeUnit.MILLISECONDS)
		    .timeInterval()
		    .take(5)
		    .subscribe(System.out::println);
		
		// TimeInterval [intervalInMilliseconds=1114, value=0]
		// TimeInterval [intervalInMilliseconds=92, value=1]
		// TimeInterval [intervalInMilliseconds=101, value=2]
		// TimeInterval [intervalInMilliseconds=100, value=3]
		// TimeInterval [intervalInMilliseconds=99, value=4]
	}
	
	public void exampleDelaySubscriptionWithSignal() {
		Observable.interval(100, TimeUnit.MILLISECONDS)
		    .delaySubscription(() -> Observable.timer(1000, TimeUnit.MILLISECONDS))
		    .timeInterval()
		    .take(5)
		    .subscribe(System.out::println);
		
		// TimeInterval [intervalInMilliseconds=1114, value=0]
		// TimeInterval [intervalInMilliseconds=92, value=1]
		// TimeInterval [intervalInMilliseconds=101, value=2]
		// TimeInterval [intervalInMilliseconds=100, value=3]
		// TimeInterval [intervalInMilliseconds=99, value=4]
	}
	
	
	//
	// Tests
	//
	
	@Test
	public void testDelay() {
		TestScheduler scheduler = Schedulers.test();
		TestSubscriber<Long> tester = new TestSubscriber<>();
		
		Observable.interval(100, TimeUnit.MILLISECONDS, scheduler)
		    .delay(i -> Observable.timer(i * 100, TimeUnit.MILLISECONDS, scheduler))
		    .timeInterval(scheduler)
		    .map(i -> i.getIntervalInMilliseconds())
		    .take(5)
		    .subscribe(tester);
		
		scheduler.advanceTimeBy(1000, TimeUnit.MILLISECONDS);
		tester.assertReceivedOnNext(Arrays.asList(100L, 200L, 200L, 200L, 200L));
		tester.assertTerminalEvent();
		tester.assertNoErrors();
	}
	
	@Test
	public void testDelaySubscription() {
		TestScheduler scheduler = Schedulers.test();
		TestSubscriber<Long> tester = new TestSubscriber<>();
		
		Observable.interval(100, TimeUnit.MILLISECONDS, scheduler)
		    .delaySubscription(1000, TimeUnit.MILLISECONDS, scheduler)
		    .timeInterval(scheduler)
		    .take(5)
		    .map(i -> i.getIntervalInMilliseconds())
		    .subscribe(tester);
		
		scheduler.advanceTimeBy(1500, TimeUnit.MILLISECONDS);
		tester.assertReceivedOnNext(Arrays.asList(1100L, 100L, 100L, 100L, 100L));
		tester.assertTerminalEvent();
		tester.assertNoErrors();
	}
	
	@Test
	public void testDelaySubscriptionWithSignal() {
		TestScheduler scheduler = Schedulers.test();
		TestSubscriber<Long> tester = new TestSubscriber<>();
		
		Observable.interval(100, TimeUnit.MILLISECONDS, scheduler)
		    .delaySubscription(() -> Observable.timer(1000, TimeUnit.MILLISECONDS, scheduler))
		    .timeInterval(scheduler)
		    .take(5)
		    .map(i -> i.getIntervalInMilliseconds())
		    .subscribe(tester);
		
		scheduler.advanceTimeBy(1500, TimeUnit.MILLISECONDS);
		tester.assertReceivedOnNext(Arrays.asList(1100L, 100L, 100L, 100L, 100L));
		tester.assertTerminalEvent();
		tester.assertNoErrors();
	}
}
