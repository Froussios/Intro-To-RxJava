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

public class CountExample {
	
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
	

	public void example() {
		Observable<Integer> values = Observable.range(0, 3);

		values
		    .subscribe(new PrintSubscriber("Values"));
		values
		    .count()
		    .subscribe(new PrintSubscriber("Count"));
		
		// Values: 0
		// Values: 1
		// Values: 2
		// Values: Completed
		// Count: 3
		// Count: Completed
	}
	
	public void exampleCountLong() {
		Observable<Integer> values = Observable.range(0, 3);

		values
		    .subscribe(new PrintSubscriber("Values"));
		values
		    .countLong()
		    .subscribe(new PrintSubscriber("Count"));
		
		// Values: 0
		// Values: 1
		// Values: 2
		// Values: Completed
		// Count: 3
		// Count: Completed
	}
	
	
	//
	// Tests
	//
	
	@Test
	public void test() {
		TestSubscriber<Integer> testerSource = new TestSubscriber<>();
		TestSubscriber<Integer> testerCount = new TestSubscriber<>();
		
		Observable<Integer> values = Observable.range(0, 3);

		values
		    .subscribe(testerSource);
		values
		    .count()
		    .subscribe(testerCount);
		
		testerSource.assertReceivedOnNext(Arrays.asList(0,1,2));
		testerSource.assertTerminalEvent();
		testerSource.assertNoErrors();
		
		testerCount.assertReceivedOnNext(Arrays.asList(3));
		testerCount.assertTerminalEvent();
		testerCount.assertNoErrors();
	}
	
	@Test
	public void testCountLong() {
		TestSubscriber<Integer> testerSource = new TestSubscriber<>();
		TestSubscriber<Long> testerCount = new TestSubscriber<>();
		
		Observable<Integer> values = Observable.range(0, 3);

		values
		    .subscribe(testerSource);
		values
		    .countLong()
		    .subscribe(testerCount);
		
		testerSource.assertReceivedOnNext(Arrays.asList(0,1,2));
		testerSource.assertTerminalEvent();
		testerSource.assertNoErrors();
		
		testerCount.assertReceivedOnNext(Arrays.asList(3L));
		testerCount.assertTerminalEvent();
		testerCount.assertNoErrors();
	}
	
	

}
