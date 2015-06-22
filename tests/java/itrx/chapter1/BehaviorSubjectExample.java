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
import rx.subjects.BehaviorSubject;

public class BehaviorSubjectExample {

	public void exampleLate() {
		BehaviorSubject<Integer> s = BehaviorSubject.create();
		s.onNext(0);
		s.onNext(1);
		s.onNext(2);
		s.subscribe(v -> System.out.println("Late: " + v)); 
		s.onNext(3);
		
		// Late: 2
		// Late: 3
	}
	
	public void exampleCompleted() {
		BehaviorSubject<Integer> s = BehaviorSubject.create();
		s.onNext(0);
		s.onNext(1);
		s.onNext(2);
		s.onCompleted();
		s.subscribe(
		    v -> System.out.println("Late: " + v),
		    e -> System.out.println("Error"),
		    () -> System.out.println("Completed")
		);
	}
	
	public void exampleInitialvalue() {
		BehaviorSubject<Integer> s = BehaviorSubject.create(0);
		s.subscribe(v -> System.out.println(v));
		s.onNext(1);
		
		// 0
		// 1
	}
	
	
	//
	// Tests
	//
	
	@Test
	public void testLate() {
		TestSubscriber<Integer> tester = new TestSubscriber<Integer>();
		
		BehaviorSubject<Integer> s = BehaviorSubject.create();
		s.onNext(0);
		s.onNext(1);
		s.onNext(2);
		s.subscribe(tester); 
		s.onNext(3);
		
		tester.assertReceivedOnNext(Arrays.asList(2,3));
	}
	
	@Test
	public void testCompleted() {
		TestSubscriber<Integer> tester = new TestSubscriber<Integer>();
		
		BehaviorSubject<Integer> s = BehaviorSubject.create();
		s.onNext(0);
		s.onNext(1);
		s.onNext(2);
		s.onCompleted();
		s.subscribe(tester);
		
		tester.assertReceivedOnNext(Arrays.asList());
		tester.assertTerminalEvent();
		tester.assertNoErrors();
	}
	
	@Test
	public void testInitialvalue() {
		TestSubscriber<Integer> tester = new TestSubscriber<Integer>();
		
		BehaviorSubject<Integer> s = BehaviorSubject.create(0);
		s.subscribe(tester);
		s.onNext(1);
		
		tester.assertReceivedOnNext(Arrays.asList(0,1));
		tester.assertNoErrors();
	}

}
