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

import static org.junit.Assert.assertTrue;

import java.util.concurrent.TimeUnit;

import org.junit.Test;

import rx.Observable;
import rx.Subscription;
import rx.observers.TestSubscriber;
import rx.schedulers.Schedulers;
import rx.schedulers.TestScheduler;

public class MergeExample {
	
	public void example() {
		Observable.merge(
		        Observable.interval(250, TimeUnit.MILLISECONDS).map(i -> "First"),
		        Observable.interval(150, TimeUnit.MILLISECONDS).map(i -> "Second"))
		    .take(10)
		    .subscribe(System.out::println);
		
		// Second
		// First
		// Second
		// Second
		// First
		// Second
		// Second
		// First
		// Second
		// First
	}
	
	public void exampleMergeWith() {
		Observable.interval(250, TimeUnit.MILLISECONDS).map(i -> "First")
			.mergeWith(Observable.interval(150, TimeUnit.MILLISECONDS).map(i -> "Second"))
			.take(10)
		    .subscribe(System.out::println);
		
		// Second
		// First
		// Second
		// Second
		// First
		// Second
		// First
		// Second
		// Second
		// First
	}
	
	
	//
	// Test
	//
	
	@Test
	public void test() {
		TestScheduler scheduler = Schedulers.test();
		TestSubscriber<String> tester = new TestSubscriber<>();
		
		Subscription subscription = Observable.merge(
		        Observable.interval(250, TimeUnit.MILLISECONDS, scheduler).map(i -> "First"),
		        Observable.interval(150, TimeUnit.MILLISECONDS, scheduler).map(i -> "Second"))
		    .take(10)
			.distinctUntilChanged()
			.subscribe(tester);
		
		// Each time that merge switches between the two sources, 
		// distinctUntilChanged allows one more value through.
		// If more that 2 values comes through, merge is going back and forth
		scheduler.advanceTimeBy(1000, TimeUnit.MILLISECONDS);
		assertTrue(tester.getOnNextEvents().size() > 2);
		
		subscription.unsubscribe();
	}
	
	@Test
	public void testMergeWith() {
		TestScheduler scheduler = Schedulers.test();
		TestSubscriber<String> tester = new TestSubscriber<>();
		
		Subscription subscription = Observable.interval(250, TimeUnit.MILLISECONDS, scheduler).map(i -> "First")
			.mergeWith(Observable.interval(150, TimeUnit.MILLISECONDS, scheduler).map(i -> "Second"))
			.distinctUntilChanged()
		    .subscribe(tester);
		
		// Each time that merge switches between the two sources, 
		// distinctUntilChanged allows one more value through.
		// If more that 2 values comes through, merge is going back and forth
		scheduler.advanceTimeBy(1000, TimeUnit.MILLISECONDS);
		assertTrue(tester.getOnNextEvents().size() > 2);
		
		subscription.unsubscribe();
	}

}
