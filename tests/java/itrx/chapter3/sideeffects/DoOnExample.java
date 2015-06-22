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
package itrx.chapter3.sideeffects;

import static org.junit.Assert.*;

import java.util.Arrays;

import org.junit.Test;

import rx.Observable;
import rx.Subscriber;
import rx.Subscription;
import rx.functions.Func0;
import rx.observers.TestSubscriber;
import rx.subjects.ReplaySubject;

public class DoOnExample {
	
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
	
	public void exampleDoOnEach() {
		Observable<String> values = Observable.just("side", "effects");

		values
		    .doOnEach(new PrintSubscriber("Log"))
		    .map(s -> s.toUpperCase())
		    .subscribe(new PrintSubscriber("Process"));
		
		// Log: side
		// Process: SIDE
		// Log: effects
		// Process: EFFECTS
		// Log: Completed
		// Process: Completed
	}
	
	public void exampleDoOnEachEncapsulation() {
		Func0<Observable<String>> service = () ->
			Observable
				.just("First", "Second", "Third")
    			.doOnEach(new PrintSubscriber("Log"));
		
		service.call()
		    .map(s -> s.toUpperCase())
		    .filter(s -> s.length() > 5)
		    .subscribe(new PrintSubscriber("Process"));
		
		// Log: First
		// Log: Second
		// Process: SECOND
		// Log: Third
		// Log: Completed
		// Process: Completed
	}
	
	public void exampleOnSubscriber() {
		ReplaySubject<Integer> subject = ReplaySubject.create();
		Observable<Integer> values = subject
		    .doOnSubscribe(() -> System.out.println("New subscription"))
		    .doOnUnsubscribe(() -> System.out.println("Subscription over"));

		Subscription s1 = values.subscribe(new PrintSubscriber("1st"));
		subject.onNext(0);
		values.subscribe(new PrintSubscriber("2st"));
		subject.onNext(1);
		s1.unsubscribe();
		subject.onNext(2);
		subject.onNext(3);
		subject.onCompleted();
		
		// New subscription
		// 1st: 0
		// New subscription
		// 2st: 0
		// 1st: 1
		// 2st: 1
		// Subscription over
		// 2st: 2
		// 2st: 3
		// 2st: Completed
		// Subscription over
	}
	
	
	//
	// Tests
	//
	
	@Test
	public void testDoOnEach() {
		TestSubscriber<String> testerLog = new TestSubscriber<>();
		TestSubscriber<String> testerFinal = new TestSubscriber<>();
		
		Observable<String> values = Observable.just("side", "effects");

		values
		    .doOnEach(testerLog)
		    .map(s -> s.toUpperCase())
		    .subscribe(testerFinal);
		
		testerLog.assertReceivedOnNext(Arrays.asList("side", "effects"));
		testerLog.assertTerminalEvent();
		testerLog.assertNoErrors();
		testerFinal.assertReceivedOnNext(Arrays.asList("SIDE", "EFFECTS"));
		testerFinal.assertTerminalEvent();
		testerFinal.assertNoErrors();
	}
	
	@Test
	public void testDoOnEachEncapsulation() {
		TestSubscriber<String> testerLog = new TestSubscriber<>();
		TestSubscriber<String> testerFinal = new TestSubscriber<>();
		
		Func0<Observable<String>> service = () ->
			Observable
				.just("First", "Second", "Third")
    			.doOnEach(testerLog);
		
		service.call()
		    .map(s -> s.toUpperCase())
		    .filter(s -> s.length() > 5)
		    .subscribe(testerFinal);
		
		testerLog.assertReceivedOnNext(Arrays.asList("First", "Second", "Third"));
		testerLog.assertTerminalEvent();
		testerLog.assertNoErrors();
		testerFinal.assertReceivedOnNext(Arrays.asList("SECOND"));
		testerFinal.assertTerminalEvent();
		testerFinal.assertNoErrors();
	}
	
	@Test
	public void testOnSubscriber() {
		int[] counts = {0, 0};
		
		ReplaySubject<Integer> subject = ReplaySubject.create();
		Observable<Integer> values = subject
		    .doOnSubscribe(() -> counts[0]++)
		    .doOnUnsubscribe(() -> counts[1]++);

		assertArrayEquals(counts, new int[]{0, 0});
		Subscription s1 = values.subscribe();
		assertArrayEquals(counts, new int[]{1, 0});
		subject.onNext(0);
		values.subscribe();
		assertArrayEquals(counts, new int[]{2, 0});
		subject.onNext(1);
		s1.unsubscribe();
		assertArrayEquals(counts, new int[]{2, 1});
		subject.onNext(2);
		subject.onNext(3);
		subject.onCompleted();
		assertArrayEquals(counts, new int[]{2, 2});
	}
}
