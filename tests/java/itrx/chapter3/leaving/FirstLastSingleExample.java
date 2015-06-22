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
package itrx.chapter3.leaving;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.junit.Test;

import rx.Observable;

public class FirstLastSingleExample {

	public void exampleFirst() {
		Observable<Long> values = Observable.interval(100, TimeUnit.MILLISECONDS);

		long value = values
		    .take(5)
		    .toBlocking()
		    .first(i -> i>2);
		System.out.println(value);
		
		// 3
	}
	
	public void exampleSingleError() {
		Observable<Long> values = Observable.interval(100, TimeUnit.MILLISECONDS);

		try {
		    long value = values
		        .take(5)
		        .toBlocking()
		        .single(i -> i>2);
		    System.out.println(value);
		}
		catch (Exception e) {
		    System.out.println("Caught: " + e);
		}
		
		// Caught: java.lang.IllegalArgumentException: Sequence contains too many elements
	}
	
	
	//
	// Tests
	//
	
	@Test
	public void testFirst() throws InterruptedException {
		List<Integer> received = new ArrayList<>();
		
		Observable<Integer> values = Observable.range(0,5);

		int value = values
		    .take(5)
		    .toBlocking()
		    .first(i -> i>2);
		received.add(value);

		assertEquals(received, Arrays.asList(3));
	}
	
	@Test(expected = IllegalArgumentException.class)
	public void testSingleError() {
		Observable<Integer> values = Observable.range(0, 5);

	    long value = values
	        .take(5)
	        .toBlocking()
	        .single(i -> i>2);
	    System.out.println(value);
	}

}
