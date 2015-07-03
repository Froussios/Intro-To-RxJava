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

import static org.junit.Assert.*;

import java.util.Arrays;
import java.util.concurrent.TimeUnit;

import org.junit.Test;

import rx.Observable;
import rx.observers.TestSubscriber;
import rx.schedulers.Schedulers;
import rx.schedulers.TestScheduler;

public class SwitchOnNextExample {

	public void example() {
		Observable.switchOnNext(
		    Observable.interval(100, TimeUnit.MILLISECONDS)
		        .map(i -> 
		            Observable.interval(30, TimeUnit.MILLISECONDS)
		                .map(i2 -> i)
		        )
		    )
		    .take(9)
		    .subscribe(System.out::println);
		
		// 0
		// 0
		// 0
		// 1
		// 1
		// 1
		// 2
		// 2
		// 2
	}
	
	
	//
	// Test
	//
	
	@Test
	public void test() {
		TestSubscriber<Object> tester = new TestSubscriber<>();
		TestScheduler scheduler = Schedulers.test();
		
		Observable.switchOnNext(
		    Observable.interval(100, TimeUnit.MILLISECONDS, scheduler)
		        .map(i -> 
		            Observable.interval(30, TimeUnit.MILLISECONDS, scheduler)
		                .map(i2 -> i)
		        )
		    )
		    .distinctUntilChanged()
		    .subscribe(tester);
		
		scheduler.advanceTimeBy(500, TimeUnit.MILLISECONDS);
		tester.assertReceivedOnNext(Arrays.asList(0L,1L,2L,3L));
		tester.assertNoErrors();
		assertEquals(tester.getOnCompletedEvents().size(), 0);
	}

}
