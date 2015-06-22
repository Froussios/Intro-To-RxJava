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
package itrx.chapter2.aggregation;

import static org.junit.Assert.*;

import java.util.Arrays;
import java.util.concurrent.TimeUnit;

import org.junit.Test;

import rx.Observable;
import rx.Subscriber;
import rx.Subscription;
import rx.observers.TestSubscriber;
import rx.schedulers.Schedulers;
import rx.schedulers.TestScheduler;

public class SingleExample {
	
	private class PrintSubscriber extends Subscriber<Object>{
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

	public void exampleSingle() {
		Observable<Long> values = Observable.interval(100, TimeUnit.MILLISECONDS);

		values.take(10)
		    .single(v -> v == 5L) // Emits a result
		    .subscribe(new PrintSubscriber("Single1"));
		values
		    .single(v -> v == 5L) // Never emits
		    .subscribe(new PrintSubscriber("Single2"));
		
		// Single1: 5
		// Single1: Completed
	}
	
	public void exampleSingleOrDefault() {
		Observable<Integer> values = Observable.empty();
		
		values
			.singleOrDefault(-1)
			.subscribe(new PrintSubscriber("SingleOrDefault"));
		
		// SingleOrDefault: -1
		// SingleOrDefault: Completed
	}
	
	
	//
	// Tests
	//
	
	@Test
	public void testSingle() {
		TestSubscriber<Long> tester1 = new TestSubscriber<>();
		TestSubscriber<Long> tester2 = new TestSubscriber<>();
		TestScheduler scheduler = Schedulers.test();
		
		Observable<Long> values = Observable.interval(100, TimeUnit.MILLISECONDS, scheduler);

		Subscription s1 = values.take(10)
		    .single(v -> v == 5L) // Emits a result
		    .subscribe(tester1);
		Subscription s2 = values
		    .single(v -> v == 5L) // Never emits
		    .subscribe(tester2);
		
		scheduler.advanceTimeBy(2, TimeUnit.SECONDS);
		
		tester1.assertReceivedOnNext(Arrays.asList(5L));
		tester1.assertTerminalEvent();
		tester1.assertNoErrors();
		tester2.assertReceivedOnNext(Arrays.asList());
		assertEquals(tester2.getOnCompletedEvents().size(), 0);
		tester2.assertNoErrors();
		
		s1.unsubscribe();
		s2.unsubscribe();
	}
	
	@Test
	public void testSingleOrDefault() {
		TestSubscriber<Integer> tester = new TestSubscriber<>();
		
		Observable<Integer> values = Observable.empty();
		
		values
			.singleOrDefault(-1)
			.subscribe(tester);
		
		tester.assertReceivedOnNext(Arrays.asList(-1));
		tester.assertTerminalEvent();
		tester.assertNoErrors();
	}
}


