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

import java.util.Arrays;

import org.junit.Test;

import rx.Observable;
import rx.observers.TestSubscriber;

public class ExistsExample {

	public void exampleFalse() {
		Observable<Integer> values = Observable.range(0, 2);

		values
		    .exists(i -> i > 2)
		    .subscribe(
		        v -> System.out.println(v),
		        e -> System.out.println("Error: " + e),
		        () -> System.out.println("Completed")
		    );
		
		// false
		// Completed
	}
	
	public void exampleTrue() {
		Observable<Integer> values = Observable.range(0, 4);

		values
		    .exists(i -> i > 2)
		    .subscribe(
		        v -> System.out.println(v),
		        e -> System.out.println("Error: " + e),
		        () -> System.out.println("Completed")
		    );
		
		// true
		// Completed
	}
	
	
	//
	// Tests
	//
	
	@Test
	public void testFalse() {
		TestSubscriber<Boolean> tester = new TestSubscriber<Boolean>();
		
		Observable<Integer> values = Observable.range(0, 2);

		values
		    .exists(i -> i > 2)
		    .subscribe(tester);
		
		tester.assertReceivedOnNext(Arrays.asList(false));
		tester.assertTerminalEvent();
		tester.assertNoErrors();
	}
	
	@Test
	public void testTrue() {
		TestSubscriber<Boolean> tester = new TestSubscriber<Boolean>();
		
		Observable<Integer> values = Observable.range(0, 4);

		values
		    .exists(i -> i > 2)
		    .subscribe(tester);
		
		tester.assertReceivedOnNext(Arrays.asList(true));
		tester.assertTerminalEvent();
		tester.assertNoErrors();
	}

}
