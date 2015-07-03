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
package itrx.chapter4.backpressure;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.junit.Test;

import rx.Observable;
import rx.observers.TestSubscriber;
import rx.schedulers.Schedulers;
import rx.schedulers.TestScheduler;

public class ConsumerSideExample {

	public void exampleSample() {
		Observable.interval(1, TimeUnit.MILLISECONDS)
	    .observeOn(Schedulers.newThread())
	    .sample(100, TimeUnit.MILLISECONDS)
	    .take(3)
	    .subscribe(
	        i -> {
	            System.out.println(i);
	            try {
	                Thread.sleep(100);
	            } catch (Exception e) { }
	        },
	        System.out::println);
		
		// 82
		// 182
		// 283
	}
	
	public void exampleBuffer() {
		Observable.interval(10, TimeUnit.MILLISECONDS)
	    .observeOn(Schedulers.newThread())
	    .buffer(100, TimeUnit.MILLISECONDS)
	    .take(3)
	    .subscribe(
	        i -> {
	            System.out.println(i);
	            try {
	                Thread.sleep(100);
	            } catch (Exception e) { }
	        },
	        System.out::println);
		
		// [0, 1, 2, 3, 4, 5, 6, 7]
		// [8, 9, 10, 11, 12, 13, 14, 15, 16, 17]
		// [18, 19, 20, 21, 22, 23, 24, 25, 26, 27]
	}
	
	
	//
	// Test
	//
	
	@Test
	public void testSample() {
		TestScheduler scheduler = Schedulers.test();
		TestSubscriber<Long> tester = new TestSubscriber<Long>() {
			@Override
			public void onNext(Long t) {
				scheduler.advanceTimeBy(100, TimeUnit.MILLISECONDS);
				super.onNext(t);
			}
		};
		
		Observable.interval(1, TimeUnit.MILLISECONDS, scheduler)
		    .observeOn(scheduler)
		    .sample(100, TimeUnit.MILLISECONDS, scheduler)
		    .take(3)
		    .subscribe(tester);
		
		scheduler.advanceTimeBy(300, TimeUnit.MILLISECONDS);
		tester.assertReceivedOnNext(Arrays.asList(98L, 199L, 299L));
		tester.assertNoErrors();
	}
	
	@Test
	public void testBuffer() {
		TestScheduler scheduler = Schedulers.test();
		TestSubscriber<List<Long>> tester = new TestSubscriber<List<Long>>() {
			@Override
			public void onNext(List<Long> t) {
				scheduler.advanceTimeBy(100, TimeUnit.MILLISECONDS);
				super.onNext(t);
			}
		};
		
		Observable.interval(10, TimeUnit.MILLISECONDS, scheduler)
		    .observeOn(scheduler)
		    .buffer(100, TimeUnit.MILLISECONDS, scheduler)
		    .take(3)
		    .subscribe(tester);
		
		scheduler.advanceTimeBy(300, TimeUnit.MILLISECONDS);
		tester.assertReceivedOnNext(Arrays.asList(
			Arrays.asList( 0L,  1L,  2L,  3L,  4L,  5L,  6L,  7L,  8L),
			Arrays.asList( 9L, 10L, 11L, 12L, 13L, 14L, 15L, 16L, 17L, 18L, 19L),
			Arrays.asList(20L, 21L, 22L, 23L, 24L, 25L, 26L, 27L, 28L, 29L)
		));
		tester.assertNoErrors();
	}
}
