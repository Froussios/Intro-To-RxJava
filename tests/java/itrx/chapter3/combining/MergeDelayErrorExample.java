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
package itrx.chapter3.combining;

import static org.hamcrest.CoreMatchers.instanceOf;
import static org.junit.Assert.assertThat;

import java.util.Arrays;
import java.util.concurrent.TimeUnit;

import org.junit.Test;

import rx.Observable;
import rx.exceptions.CompositeException;
import rx.observers.TestSubscriber;
import rx.schedulers.Schedulers;
import rx.schedulers.TestScheduler;

public class MergeDelayErrorExample {

	public void example1Error() {
		Observable<Long> failAt200 = 
			Observable.concat(
					Observable.interval(100, TimeUnit.MILLISECONDS).take(2),
					Observable.error(new Exception("Failed")));
		Observable<Long> completeAt400 = 
			Observable.interval(100, TimeUnit.MILLISECONDS)
				.take(4);
		
		Observable.mergeDelayError(failAt200, completeAt400)
			.subscribe(
					System.out::println,
					System.out::println);
		
		// 0
		// 0
		// 1
		// 1
		// 2
		// 3
		// java.lang.Exception: Failed
	}
	
	public void example2Errors() {
		Observable<Long> failAt200 = 
			Observable.concat(
					Observable.interval(100, TimeUnit.MILLISECONDS).take(2),
					Observable.error(new Exception("Failed")));
		Observable<Long> failAt300 = 
				Observable.concat(
						Observable.interval(100, TimeUnit.MILLISECONDS).take(3),
						Observable.error(new Exception("Failed")));
		Observable<Long> completeAt400 = 
			Observable.interval(100, TimeUnit.MILLISECONDS)
				.take(4);
		
		Observable.mergeDelayError(failAt200, failAt300, completeAt400)
			.subscribe(
					System.out::println,
					System.out::println);
		
		// 0
		// 0
		// 0
		// 1
		// 1
		// 1
		// 2
		// 2
		// 3
		// rx.exceptions.CompositeException: 2 exceptions occurred. 
	}
	
	
	//
	// Tests
	//
	
	@Test
	public void test1Error() {
		TestSubscriber<Long> tester = new TestSubscriber<>();
		TestScheduler scheduler = Schedulers.test();
		
		Observable<Long> failAt200 = 
			Observable.concat(
					Observable.interval(100, TimeUnit.MILLISECONDS, scheduler).take(2),
					Observable.error(new Exception("Failed")));
		Observable<Long> completeAt400 = 
			Observable.interval(100, TimeUnit.MILLISECONDS, scheduler)
				.take(4);
		
		Observable.mergeDelayError(failAt200, completeAt400)
			.subscribe(tester);
		
		scheduler.advanceTimeBy(400, TimeUnit.MILLISECONDS);
		tester.assertReceivedOnNext(Arrays.asList(0L, 0L, 1L, 1L, 2L, 3L));
		assertThat(tester.getOnErrorEvents().get(0), instanceOf(Exception.class));
	}
	
	@Test
	public void test2Errors() {
		TestSubscriber<Long> tester = new TestSubscriber<>();
		TestScheduler scheduler = Schedulers.test();
		
		Observable<Long> failAt200 = 
			Observable.concat(
					Observable.interval(100, TimeUnit.MILLISECONDS, scheduler).take(2),
					Observable.error(new Exception("Failed")));
		Observable<Long> failAt300 = 
				Observable.concat(
						Observable.interval(100, TimeUnit.MILLISECONDS, scheduler).take(3),
						Observable.error(new Exception("Failed")));
		Observable<Long> completeAt400 = 
			Observable.interval(100, TimeUnit.MILLISECONDS, scheduler)
				.take(4);
		
		Observable.mergeDelayError(failAt200, failAt300, completeAt400)
			.subscribe(tester);
		
		scheduler.advanceTimeBy(400, TimeUnit.MILLISECONDS);
		tester.assertReceivedOnNext(Arrays.asList(0L, 0L, 0L, 1L, 1L, 1L, 2L, 2L, 3L));
		assertThat(tester.getOnErrorEvents().get(0), instanceOf(CompositeException.class));
	}

}
