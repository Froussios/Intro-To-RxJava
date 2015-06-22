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
package itrx.chapter1;

import java.util.Arrays;

import org.junit.Test;

import rx.observers.TestSubscriber;
import rx.subjects.ReplaySubject;
import rx.subjects.Subject;

public class RxContractExample {

	public void example() {
		Subject<Integer, Integer> s = ReplaySubject.create();
		s.subscribe(v -> System.out.println(v));
		s.onNext(0);
		s.onCompleted();
		s.onNext(1);
		s.onNext(2);

		// 0
	}
	
	public void examplePrintCompletion() {
		Subject<Integer, Integer>  values = ReplaySubject.create();
		values.subscribe(
		    v -> System.out.println(v),
		    e -> System.out.println(e),
		    () -> System.out.println("Completed")
		);
		values.onNext(0);
		values.onNext(1);
		values.onCompleted();
		values.onNext(2);
		
		// 0
		// 1
	}

	
	//
	// Test
	//

	@Test
	public void test() {
		TestSubscriber<Integer> tester = new TestSubscriber<Integer>();

		Subject<Integer, Integer> s = ReplaySubject.create();
		s.subscribe(tester);
		s.onNext(0);
		s.onCompleted();
		s.onNext(1);
		s.onNext(2);

		tester.assertReceivedOnNext(Arrays.asList(0));
		tester.assertTerminalEvent();
		tester.assertNoErrors();
	}
	
	@Test
	public void testPrintCompletion() {
		TestSubscriber<Integer> tester = new TestSubscriber<Integer>();
		
		Subject<Integer, Integer>  values = ReplaySubject.create();
		values.subscribe(tester);
		values.onNext(0);
		values.onNext(1);
		values.onCompleted();
		values.onNext(2);
		
		tester.assertReceivedOnNext(Arrays.asList(0,1));
		tester.assertTerminalEvent();
		tester.assertNoErrors();
	}

}
