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
package itrx.chapter3.error;

import static org.junit.Assert.*;

import java.util.Random;

import org.junit.Test;

import rx.Observable;
import rx.observers.TestSubscriber;

public class RetryExample {

	public void exampleRetry() {
		Random random = new Random();
		Observable<Integer> values = Observable.create(o -> {
		    o.onNext(random.nextInt() % 20);
		    o.onNext(random.nextInt() % 20);
		    o.onError(new Exception());
		});

		values
		    .retry(1)
		    .subscribe(v -> System.out.println(v));
		
		// 0
		// 13
		// 9
		// 15
		// java.lang.Exception
	}
	
	
	//
	// Test
	//
	
	@Test
	public void testRetry() {
		TestSubscriber<Integer> tester = new TestSubscriber<>();
		Random random = new Random();
		Observable<Integer> values = Observable.create(o -> {
		    o.onNext(random.nextInt() % 20);
		    o.onNext(random.nextInt() % 20);
		    o.onError(new Exception());
		});

		values
		    .retry(1)
		    .subscribe(tester);
		
		assertEquals(tester.getOnNextEvents().size(), 4);
		tester.assertTerminalEvent();
		assertEquals(tester.getOnErrorEvents().size(), 1);
	}

}
