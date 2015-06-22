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

import org.junit.Test;

import rx.Observable;
import rx.Subscriber;
import rx.observers.TestSubscriber;

public class LastExample {
	
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

	public void exampleLast() {
		Observable<Integer> values = Observable.range(0,10);

		values
		    .last()
		    .subscribe(new PrintSubscriber("Last"));
		
		// 9
	}
	
	public void exampleLastWithPredicate() {
		Observable<Integer> values = Observable.range(0,10);

		values
		    .last(v -> v<5)
		    .subscribe(new PrintSubscriber("Last"));
		
		// 4
	}
	
	public void exampleLastOrDefault() {
		Observable<Integer> values = Observable.empty();

		values
		    .lastOrDefault(-1)
		    .subscribe(new PrintSubscriber("Last"));
		
		// -1
	}
	
	public void exampleLastOrDefaultWithPredicate() {
		Observable<Integer> values = Observable.empty();

		values
		    .lastOrDefault(-1, v -> v>5)
		    .subscribe(new PrintSubscriber("Last"));
		
		// -1
	}
	
	
	//
	// Tests
	//

	@Test
	public void testLast() {
		TestSubscriber<Integer> tester = new TestSubscriber<>();
		
		Observable<Integer> values = Observable.range(0,10);

		values
		    .last()
		    .subscribe(tester);
		
		tester.assertReceivedOnNext(Arrays.asList(9));
		tester.assertTerminalEvent();
		tester.assertNoErrors();
	}
	
	@Test
	public void testLastWithPredicate() {
		TestSubscriber<Integer> tester = new TestSubscriber<>();
		
		Observable<Integer> values = Observable.range(0,10);

		values
		    .last(v -> v<5)
		    .subscribe(tester);
		
		tester.assertReceivedOnNext(Arrays.asList(4));
		tester.assertTerminalEvent();
		tester.assertNoErrors();
	}
	
	@Test
	public void testLastOrDefault() {
		TestSubscriber<Integer> tester = new TestSubscriber<>();
		
		Observable<Integer> values = Observable.empty();

		values
		    .lastOrDefault(-1)
		    .subscribe(tester);
		
		tester.assertReceivedOnNext(Arrays.asList(-1));
		tester.assertTerminalEvent();
		tester.assertNoErrors();
	}
	
	@Test
	public void testLastOrDefaultWithPredicate() {
		TestSubscriber<Integer> tester = new TestSubscriber<>();
		
		Observable<Integer> values = Observable.empty();

		values
		    .lastOrDefault(-1, v -> v<5)
		    .subscribe(tester);
		
		tester.assertReceivedOnNext(Arrays.asList(-1));
		tester.assertTerminalEvent();
		tester.assertNoErrors();
	}
	
}


