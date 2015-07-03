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

import java.util.Arrays;
import java.util.concurrent.TimeUnit;

import org.junit.Test;

import rx.Observable;
import rx.observers.TestSubscriber;
import rx.schedulers.Schedulers;
import rx.schedulers.TestScheduler;

public class ConcatMapExample {
	
	public void exampleConcatMap() {
		Observable.just(100, 150)
		    .concatMap(i ->
		        Observable.interval(i, TimeUnit.MILLISECONDS)
		            .map(v -> i)
		            .take(3))
		    .subscribe(
	    		System.out::println,
	    		System.out::println,
	    		() -> System.out.println("Completed"));
		
		// 100
		// 100
		// 100
		// 150
		// 150
		// 150
		// Completed
	}
	
	
	//
	// Test
	//
	
	@Test
	public void testConcatMap() {
		TestSubscriber<Integer> tester = new TestSubscriber<>();
		TestScheduler scheduler = Schedulers.test();
		
		Observable.just(100, 150)
		    .concatMap(i ->
		        Observable.interval(i, TimeUnit.MILLISECONDS, scheduler)
		            .map(v -> i)
		            .take(3)
		    )
		    .subscribe(tester);
		
		scheduler.advanceTimeBy(750, TimeUnit.MILLISECONDS);		
		tester.assertReceivedOnNext(Arrays.asList(100, 100, 100, 150, 150, 150));
		tester.assertTerminalEvent();
		tester.assertNoErrors();
	}

}
