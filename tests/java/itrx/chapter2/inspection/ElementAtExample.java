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

public class ElementAtExample {

	public void exampleElementAt() {
		Observable<Integer> values = Observable.range(100, 10);

		values
		    .elementAt(2)
		    .subscribe(
		        v -> System.out.println(v),
		        e -> System.out.println("Error: " + e),
		        () -> System.out.println("Completed")
		    );
		
		// 102
		// Completed
	}
	
	public void exampleElementAtOrDefault() {
		Observable<Integer> values = Observable.range(100, 10);

		values
		    .elementAtOrDefault(22, 0)
		    .subscribe(
		        v -> System.out.println(v),
		        e -> System.out.println("Error: " + e),
		        () -> System.out.println("Completed")
		    );
		
		// 0
		// Completed
	}
	
	
	//
	// Tests
	//
	
	@Test
	public void testElementAt() {
		TestSubscriber<Integer> tester = new TestSubscriber<Integer>();
		
		Observable<Integer> values = Observable.range(100, 10);

		values
		    .elementAt(2)
		    .subscribe(tester);
		
		tester.assertReceivedOnNext(Arrays.asList(102));
		tester.assertTerminalEvent();
		tester.assertNoErrors();
	}
	
	@Test
	public void testElementAtOrDefault() {
		TestSubscriber<Integer> tester = new TestSubscriber<Integer>();
		
		Observable<Integer> values = Observable.range(100, 10);

		values
		    .elementAtOrDefault(22, 0)
		    .subscribe(tester);
		
		tester.assertReceivedOnNext(Arrays.asList(0));
		tester.assertTerminalEvent();
		tester.assertNoErrors();
	}

}
