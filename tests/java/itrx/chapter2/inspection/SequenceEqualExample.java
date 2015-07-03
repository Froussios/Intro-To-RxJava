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
package itrx.chapter2.inspection;

import static org.junit.Assert.*;

import java.util.Arrays;

import org.junit.Test;

import rx.Observable;
import rx.observers.TestSubscriber;

public class SequenceEqualExample {

	public void exampleSequenceEqualTrue() {
		Observable<String> strings = Observable.just("1", "2", "3");
		Observable<Integer> ints = Observable.just(1, 2, 3);

		Observable.sequenceEqual(strings, ints, (s,i) -> s.equals(i.toString()))
		    .subscribe(
		        v -> System.out.println(v),
		        e -> System.out.println("Error: " + e),
		        () -> System.out.println("Completed")
		    );
		
		// true
		// Completed
	}
	
	public void exampleSequenceEqualFalse() {
		Observable<String> strings = Observable.just("1", "2", "3");
		Observable<Integer> ints = Observable.just(1, 2, 3);

		Observable.sequenceEqual(strings, ints)
		    .subscribe(
		        v -> System.out.println(v),
		        e -> System.out.println("Error: " + e),
		        () -> System.out.println("Completed")
		    );
		
		// false
		// Completed
	}
	
	public void exampleSequenceEqualError() {
		Observable<Integer> values = Observable.create(o -> {
		    o.onNext(1);
		    o.onNext(2);
		    o.onError(new Exception());
		});

		Observable.sequenceEqual(values, values)
		    .subscribe(
		        v -> System.out.println(v),
		        e -> System.out.println("Error: " + e),
		        () -> System.out.println("Completed")
		    );
		
		// Error: java.lang.Exception
	}
	
	
	//
	// Tests
	//
	
	@Test
	public void testSequenceEqualTrue() {
		TestSubscriber<Boolean> tester = new TestSubscriber<Boolean>();
		
		Observable<String> strings = Observable.just("1", "2", "3");
		Observable<Integer> ints = Observable.just(1, 2, 3);

		Observable.sequenceEqual(strings, ints, (s,i) -> s.equals(i.toString()))
		    .subscribe(tester);
		
		tester.assertReceivedOnNext(Arrays.asList(true));
		tester.assertTerminalEvent();
		tester.assertNoErrors();
	}
	
	@Test
	public void testSequenceEqualFalse() {
		TestSubscriber<Boolean> tester = new TestSubscriber<Boolean>();
		
		Observable<String> strings = Observable.just("1", "2", "3");
		Observable<Integer> ints = Observable.just(1, 2, 3);

		Observable.sequenceEqual(strings, ints)
		    .subscribe(tester);
		
		tester.assertReceivedOnNext(Arrays.asList(false));
		tester.assertTerminalEvent();
		tester.assertNoErrors();
	}
	
	@Test
	public void testSequenceEqualError() {
		TestSubscriber<Boolean> tester = new TestSubscriber<Boolean>();
		
		Observable<Integer> values = Observable.create(o -> {
		    o.onNext(1);
		    o.onNext(2);
		    o.onError(new Exception());
		});

		Observable.sequenceEqual(values, values)
		    .subscribe(tester);
		
		tester.assertReceivedOnNext(Arrays.asList());
		tester.assertTerminalEvent();
		assertEquals(tester.getOnErrorEvents().size(), 1);
	}

}
