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
import rx.observers.TestSubscriber;

public class GroupByExample {
	
	public void exampleGroupBy() {
		Observable<String> values = Observable.just(
		        "first",
		        "second",
		        "third",
		        "forth",
		        "fifth",
		        "sixth"
		);

		values.groupBy(word -> word.charAt(0))
		    .flatMap(group -> 
		        group.last().map(v -> group.getKey() + ": " + v)
		    )
		    .subscribe(v -> System.out.println(v));
		
		// s: sixth
		// t: third
		// f: fifth
	}
	
	
	//
	// Tests
	//
	
	@Test
	public void testGroupBy() {
		TestSubscriber<Object> tester = new TestSubscriber<>();
		
		Observable<String> values = Observable.just(
		        "first",
		        "second",
		        "third",
		        "forth",
		        "fifth",
		        "sixth"
		);

		values.groupBy(word -> word.charAt(0))
		    .flatMap(group -> 
		        group.last().map(v -> group.getKey() + ": " + v)
		    )
		    .subscribe(tester);
		
		tester.assertReceivedOnNext(Arrays.asList("s: sixth", "t: third", "f: fifth"));
		tester.assertTerminalEvent();
		tester.assertNoErrors();
	}
}


