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

import static org.junit.Assert.*;

import java.util.Arrays;

import org.junit.Test;

import rx.observers.TestSubscriber;
import rx.subjects.AsyncSubject;

public class AsyncSubjectExample {

	public void exampleLastValue() {
		AsyncSubject<Integer> s = AsyncSubject.create();
		s.subscribe(v -> System.out.println(v));
		s.onNext(0);
		s.onNext(1);
		s.onNext(2);
		s.onCompleted();

		// 2
	}

	public void exampleNoCompletion() {
		AsyncSubject<Integer> s = AsyncSubject.create();
		s.subscribe(v -> System.out.println(v));
		s.onNext(0);
		s.onNext(1);
		s.onNext(2);
	}

	//
	// Tests
	//

	@Test
	public void testLastValue() {
		TestSubscriber<Integer> tester = new TestSubscriber<Integer>();

		AsyncSubject<Integer> s = AsyncSubject.create();
		s.subscribe(tester);
		s.onNext(0);
		s.onNext(1);
		s.onNext(2);
		s.onCompleted();

		tester.assertReceivedOnNext(Arrays.asList(2));
		tester.assertTerminalEvent();
		tester.assertNoErrors();
	}

	@Test
	public void testNoCompletion() {
		TestSubscriber<Integer> tester = new TestSubscriber<Integer>();

		AsyncSubject<Integer> s = AsyncSubject.create();
		s.subscribe(tester);
		s.onNext(0);
		s.onNext(1);
		s.onNext(2);

		tester.assertReceivedOnNext(Arrays.asList());
		assertTrue(tester.getOnCompletedEvents().size() == 0);
		tester.assertNoErrors();
	}

}
