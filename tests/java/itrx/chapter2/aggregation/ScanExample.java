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
import rx.subjects.ReplaySubject;
import rx.subjects.Subject;

public class ScanExample {
	
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

	
	public void exampleRunningSum() {
		Observable<Integer> values = Observable.range(0,5);

		values
		    .scan((i1,i2) -> i1+i2)
		    .subscribe(new PrintSubscriber("Sum"));
		
		// Sum: 0
		// Sum: 1
		// Sum: 3
		// Sum: 6
		// Sum: 10
		// Sum: Completed
	}
	
	public void exampleRunningMin() {
		Subject<Integer, Integer> values = ReplaySubject.create();

		values
		    .subscribe(new PrintSubscriber("Values"));
		values
		    .scan((i1,i2) -> (i1<i2) ? i1 : i2)
		    .distinctUntilChanged()
		    .subscribe(new PrintSubscriber("Min"));

		values.onNext(2);
		values.onNext(3);
		values.onNext(1);
		values.onNext(4);
		values.onCompleted();
		
		// Values: 2
		// Min: 2
		// Values: 3
		// Values: 1
		// Min: 1
		// Values: 4
		// Values: Completed
		// Min: Completed
	}
	
	
	//
	// Tests
	//
	
	@Test
	public void testRunningSum() {
		TestSubscriber<Integer> tester = new TestSubscriber<>();
		
		Observable<Integer> values = Observable.range(0,5);

		values
		    .scan((i1,i2) -> i1+i2)
		    .subscribe(tester);
		
		tester.assertReceivedOnNext(Arrays.asList(0,1,3,6,10));
		tester.assertTerminalEvent();
		tester.assertNoErrors();
	}
	
	@Test
	public void testRunningMin() {
		TestSubscriber<Integer> testerSource = new TestSubscriber<>();
		TestSubscriber<Integer> testerScan = new TestSubscriber<>();
		
		Subject<Integer, Integer> values = ReplaySubject.create();

		values
		    .subscribe(testerSource);
		values
		    .scan((i1,i2) -> (i1<i2) ? i1 : i2)
		    .distinctUntilChanged()
		    .subscribe(testerScan);

		values.onNext(2);
		values.onNext(3);
		values.onNext(1);
		values.onNext(4);
		values.onCompleted();
		
		testerSource.assertReceivedOnNext(Arrays.asList(2,3,1,4));
		testerSource.assertTerminalEvent();
		testerSource.assertNoErrors();
		testerScan.assertReceivedOnNext(Arrays.asList(2,1));
		testerScan.assertTerminalEvent();
		testerScan.assertNoErrors();
	}
}


