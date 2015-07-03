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
package itrx.chapter4.backpressure;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.Test;

import rx.Observable;

public class OnRequestExample {
	
	public void exampleOnRequest() {
		Observable.range(0, 3)
			.doOnRequest(i -> System.out.println("Requested " + i))
			.subscribe(System.out::println);
		
		// Requested 9223372036854775807
		// 0
		// 1
		// 2
	}
	
	public void exampleOnRequestZip() {
		Observable.range(0, 300)
			.doOnRequest(i -> System.out.println("Requested " + i))
			.zipWith(
					Observable.range(10, 300),
					(i1, i2) -> i1 + " - " + i2)
			.take(300)
			.subscribe();
		
		// Requested 128
		// Requested 90
		// Requested 90
		// Requested 90
		
	}
	
	public void exampleOnRequestManual() {
		ControlledPullSubscriber<Integer> puller = 
				new ControlledPullSubscriber<Integer>(System.out::println);
		
		Observable.range(0, 3)
			.doOnRequest(i -> System.out.println("Requested " + i))
			.subscribe(puller);
		
		puller.requestMore(2);
		puller.requestMore(1);
		
		// Requested 0
		// Requested 2
		// 0
		// 1
		// Requested 1
		// 2
	}
	
	
	//
	// Tests
	//
	
	@Test
	public void testOnRequest() {
		List<Long> requests = new ArrayList<Long>();
		
		Observable.range(0, 3)
			.doOnRequest(requests::add)
			.subscribe();
		
		assertEquals(Arrays.asList(Long.MAX_VALUE), requests);
	}
	
	@Test
	public void testOnRequestZip() {
		List<Long> requests = new ArrayList<Long>();
		
		Observable.range(0, 300)
			.doOnRequest(requests::add)
			.zipWith(
					Observable.range(10, 300),
					(i1, i2) -> i1 + " - " + i2)
			.take(300)
			.subscribe();
		
		assertTrue("zip makes subsequent requests",
				requests.size() > 1);
		assertEquals("zip uses a buffer of 128", 
				requests.get(0), new Long(128));
	}
	
	@Test
	public void testOnRequestManual() {
		List<Integer> received = new ArrayList<Integer>();
		List<Long> requests = new ArrayList<Long>();
		
		ControlledPullSubscriber<Integer> puller = 
				new ControlledPullSubscriber<Integer>(received::add);
		
		Observable.range(0, 3)
			.doOnRequest(requests::add)
			.subscribe(puller);
		
		assertEquals(Arrays.asList(0L), requests);
		assertEquals(Arrays.asList(), received);
		puller.requestMore(2);
		assertEquals(Arrays.asList(0L, 2L), requests);
		assertEquals(Arrays.asList(0, 1), received);
		puller.requestMore(1);
		assertEquals(Arrays.asList(0L, 2L, 1L), requests);
		assertEquals(Arrays.asList(0, 1, 2), received);
	}

}
