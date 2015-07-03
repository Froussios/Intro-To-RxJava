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

import java.util.Arrays;
import java.util.concurrent.TimeUnit;

import org.junit.Test;

import rx.Observable;
import rx.Subscriber;
import rx.observers.TestSubscriber;
import rx.schedulers.Schedulers;
import rx.schedulers.TestScheduler;

public class FirstExample {
	
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

	public void exampleFirst() {
		Observable<Long> values = Observable.interval(100, TimeUnit.MILLISECONDS);

		values
		    .first()
		    .subscribe(new PrintSubscriber("First"));
		
		// 0
	}
	
	public void exampleFirstWithPredicate() {
		Observable<Long> values = Observable.interval(100, TimeUnit.MILLISECONDS);

		values
		    .first(v -> v>5)
		    .subscribe(new PrintSubscriber("First"));
		
		// 6
	}
	
	public void exampleFirstOrDefault() {
		Observable<Long> values = Observable.empty();

		values
		    .firstOrDefault(-1L)
		    .subscribe(new PrintSubscriber("First"));
		
		// -1
	}
	
	public void exampleFirstOrDefaultWithPredicate() {
		Observable<Long> values = Observable.empty();

		values
		    .firstOrDefault(-1L, v -> v>5)
		    .subscribe(new PrintSubscriber("First"));
		
		// -1
	}
	
	
	//
	// Tests
	//

	@Test
	public void testFirst() {
		TestSubscriber<Long> tester = new TestSubscriber<>();
		TestScheduler scheduler = Schedulers.test();
		
		Observable<Long> values = Observable.interval(100, TimeUnit.MILLISECONDS, scheduler);

		values
		    .first()
		    .subscribe(tester);
		
		scheduler.advanceTimeBy(1, TimeUnit.SECONDS);
		
		tester.assertReceivedOnNext(Arrays.asList(0L));
		tester.assertTerminalEvent();
		tester.assertNoErrors();
	}
	
	@Test
	public void testFirstWithPredicate() {
		TestSubscriber<Long> tester = new TestSubscriber<>();
		TestScheduler scheduler = Schedulers.test();
		
		Observable<Long> values = Observable.interval(100, TimeUnit.MILLISECONDS, scheduler);

		values
		    .first(v -> v>5)
		    .subscribe(tester);
		
		scheduler.advanceTimeBy(1, TimeUnit.SECONDS);
		
		tester.assertReceivedOnNext(Arrays.asList(6L));
		tester.assertTerminalEvent();
		tester.assertNoErrors();
	}
	
	@Test
	public void testFirstOrDefault() {
		TestSubscriber<Long> tester = new TestSubscriber<>();
		
		Observable<Long> values = Observable.empty();

		values
		    .firstOrDefault(-1L)
		    .subscribe(tester);
		
		tester.assertReceivedOnNext(Arrays.asList(-1L));
		tester.assertTerminalEvent();
		tester.assertNoErrors();
	}
	
	@Test
	public void testFirstOrDefaultWithPredicate() {
		TestSubscriber<Long> tester = new TestSubscriber<>();
		
		Observable<Long> values = Observable.empty();

		values
		    .firstOrDefault(-1L, v -> v>5)
		    .subscribe(tester);
		
		tester.assertReceivedOnNext(Arrays.asList(-1L));
		tester.assertTerminalEvent();
		tester.assertNoErrors();
	}
	
}


