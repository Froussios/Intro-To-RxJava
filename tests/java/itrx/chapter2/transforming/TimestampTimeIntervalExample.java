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
package itrx.chapter2.transforming;

import static org.junit.Assert.assertEquals;

import java.util.concurrent.TimeUnit;

import org.junit.Test;

import rx.Observable;
import rx.Subscriber;
import rx.observers.TestSubscriber;
import rx.schedulers.Schedulers;
import rx.schedulers.TestScheduler;
import rx.schedulers.TimeInterval;
import rx.schedulers.Timestamped;

public class TimestampTimeIntervalExample {
	
	private static class PrintSubscriber extends Subscriber<Object>{
	    private final String name;
	    public PrintSubscriber(String name) {
	        this.name = name;
	    }
	    @Override
	    public void onCompleted() {
	        System.out.println(name + ": Completed");
	    }
	    @Override
	    public void onError(Throwable e) {
	        System.out.println(name + ": Error: " + e);
	    }
	    @Override
	    public void onNext(Object v) {
	        System.out.println(name + ": " + v);
	    }
	}

	public void exampleTimestamp() {
		Observable<Long> values = Observable.interval(100, TimeUnit.MILLISECONDS);

		values.take(3)
		    .timestamp()
		    .subscribe(new PrintSubscriber("Timestamp"));
		
		// Timestamp: Timestamped(timestampMillis = 1428611094943, value = 0)
		// Timestamp: Timestamped(timestampMillis = 1428611095037, value = 1)
		// Timestamp: Timestamped(timestampMillis = 1428611095136, value = 2)
		// Timestamp: Completed
	}
	
	public void exampleTimeInteval() {
		Observable<Long> values = Observable.interval(100, TimeUnit.MILLISECONDS);

		values.take(3)
		    .timeInterval()
		    .subscribe(new PrintSubscriber("TimeInterval"));
		
		// TimeInterval: TimeInterval [intervalInMilliseconds=131, value=0]
		// TimeInterval: TimeInterval [intervalInMilliseconds=75, value=1]
		// TimeInterval: TimeInterval [intervalInMilliseconds=100, value=2]
		// TimeInterval: Completed
	}
	
	
	//
	// Tests
	//
	
	@Test
	public void testTimestamp() {
		TestSubscriber<Timestamped<Long>> tester = new TestSubscriber<>();
		TestScheduler scheduler = Schedulers.test();
		
		Observable<Long> values = Observable.interval(100, TimeUnit.MILLISECONDS, scheduler);

		values.take(3)
		    .timestamp(scheduler)
		    .subscribe(tester);
		
		scheduler.advanceTimeBy(1, TimeUnit.SECONDS);
		
		assertEquals(tester.getOnNextEvents().get(0).getTimestampMillis(), 100);
		assertEquals(tester.getOnNextEvents().get(1).getTimestampMillis(), 200);
		assertEquals(tester.getOnNextEvents().get(2).getTimestampMillis(), 300);
		tester.assertTerminalEvent();
		tester.assertNoErrors();
	}
	
	@Test
	public void testTimeInteval() {
		TestSubscriber<TimeInterval<Long>> tester = new TestSubscriber<>();
		TestScheduler scheduler = Schedulers.test();
		
		Observable<Long> values = Observable.interval(100, TimeUnit.MILLISECONDS, scheduler);

		values.take(3)
		    .timeInterval(scheduler)
		    .subscribe(tester);
		
		scheduler.advanceTimeBy(1, TimeUnit.SECONDS);
		
		assertEquals(tester.getOnNextEvents().get(0).getIntervalInMilliseconds(), 100);
		assertEquals(tester.getOnNextEvents().get(1).getIntervalInMilliseconds(), 100);
		assertEquals(tester.getOnNextEvents().get(2).getIntervalInMilliseconds(), 100);
		tester.assertTerminalEvent();
		tester.assertNoErrors();
	}
	
}
