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

import java.util.Arrays;
import java.util.concurrent.TimeUnit;

import org.junit.Test;

import rx.Observable;
import rx.observers.TestSubscriber;
import rx.schedulers.Schedulers;
import rx.schedulers.TestScheduler;

public class AmbExample {

	public void exampleAmb() {
		Observable.amb(
		        Observable.timer(100, TimeUnit.MILLISECONDS).map(i -> "First"),
		        Observable.timer(50, TimeUnit.MILLISECONDS).map(i -> "Second"))
		    .subscribe(System.out::println);
		
		// Second
	}
	
	public void exampleAmbWith() {
		Observable.timer(100, TimeUnit.MILLISECONDS).map(i -> "First")
			.ambWith(Observable.timer(50, TimeUnit.MILLISECONDS).map(i -> "Second"))
			.ambWith(Observable.timer(70, TimeUnit.MILLISECONDS).map(i -> "Third"))
			.subscribe(System.out::println);
        
		// Second
	}
	
	
	//
	// Test
	//
	
	@Test
	public void testAmb() {
		TestSubscriber<String> tester = new TestSubscriber<>();
		TestScheduler scheduler = Schedulers.test();
		
		Observable.amb(
		        Observable.timer(100, TimeUnit.MILLISECONDS, scheduler).map(i -> "First"),
		        Observable.timer(50, TimeUnit.MILLISECONDS, scheduler).map(i -> "Second"))
		    .subscribe(tester);
	    
	    scheduler.advanceTimeBy(100, TimeUnit.MILLISECONDS);
	    tester.assertReceivedOnNext(Arrays.asList("Second"));
	    tester.assertTerminalEvent();
	    tester.assertNoErrors();
	}
	
	@Test
	public void testAmbWith() {
		TestSubscriber<String> tester = new TestSubscriber<>();
		TestScheduler scheduler = Schedulers.test();
		
		Observable.timer(100, TimeUnit.MILLISECONDS, scheduler).map(i -> "First")
			.ambWith(Observable.timer(50, TimeUnit.MILLISECONDS, scheduler).map(i -> "Second"))
			.ambWith(Observable.timer(70, TimeUnit.MILLISECONDS, scheduler).map(i -> "Third"))
			.subscribe(tester);
        
		scheduler.advanceTimeBy(100, TimeUnit.MILLISECONDS);
	    tester.assertReceivedOnNext(Arrays.asList("Second"));
	    tester.assertTerminalEvent();
	    tester.assertNoErrors();
	}

}
