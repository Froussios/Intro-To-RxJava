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
package itrx.chapter3.combining;

import java.util.Arrays;

import org.junit.Test;

import rx.Observable;
import rx.observers.TestSubscriber;

public class ConcatExample {

	public void exampleConcat() {
		Observable<Integer> seq1 = Observable.range(0, 3);
		Observable<Integer> seq2 = Observable.range(10, 3);

		Observable.concat(seq1, seq2)
		    .subscribe(System.out::println);
		
		// 0
		// 1
		// 2
		// 10
		// 11
		// 12
	}
	
	public void exampleConcatDynamic() {
		Observable<String> words = Observable.just(
			    "First",
			    "Second",
			    "Third",
			    "Fourth",
			    "Fifth",
			    "Sixth"
			);

		Observable.concat(words.groupBy(v -> v.charAt(0)))
		    .subscribe(System.out::println);
		
		// First
		// Fourth
		// Fifth
		// Second
		// Sixth
		// Third
	}
	
	public void exampleConcatWith() {
		Observable<Integer> seq1 = Observable.range(0, 3);
		Observable<Integer> seq2 = Observable.range(10, 3);
		Observable<Integer> seq3 = Observable.just(20);
		
		seq1.concatWith(seq2)
			.concatWith(seq3)
			.subscribe(System.out::println);
		
		// 0
		// 1
		// 2
		// 10
		// 11
		// 12
		// 20
	}
	
	
	//
	// Tests
	//
	
	@Test
	public void testConcat() {
		TestSubscriber<Integer> tester = new TestSubscriber<>();
		
		Observable<Integer> seq1 = Observable.range(0, 3);
		Observable<Integer> seq2 = Observable.range(10, 3);

		Observable.concat(seq1, seq2)
		    .subscribe(tester);
		
		tester.assertReceivedOnNext(Arrays.asList(0,1,2,10,11,12));
		tester.assertTerminalEvent();
		tester.assertNoErrors();
	}
	
	@Test
	public void testConcatDynamic() {
		TestSubscriber<String> tester = new TestSubscriber<>();
		
		Observable<String> words = Observable.just(
			    "First",
			    "Second",
			    "Third",
			    "Fourth",
			    "Fifth",
			    "Sixth"
			);

		Observable.concat(words.groupBy(v -> v.charAt(0)))
		    .subscribe(tester);
		
		tester.assertReceivedOnNext(Arrays.asList(
				"First",
				"Fourth",
				"Fifth",
				"Second",
				"Sixth",
				"Third"));
		tester.assertTerminalEvent();
		tester.assertNoErrors();
	}
	
	@Test
	public void testConcatWith() {
		TestSubscriber<Integer> tester = new TestSubscriber<>();
		
		Observable<Integer> seq1 = Observable.range(0, 3);
		Observable<Integer> seq2 = Observable.range(10, 3);
		Observable<Integer> seq3 = Observable.just(20);
		
		seq1.concatWith(seq2)
			.concatWith(seq3)
			.subscribe(tester);
		
		tester.assertReceivedOnNext(Arrays.asList(0,1,2,10,11,12,20));
		tester.assertTerminalEvent();
		tester.assertNoErrors();
	}
}
