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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.Test;

import rx.Observable;

public class ReactivePullExample {
	
	public void example() {
		ControlledPullSubscriber<Integer> tester = new ControlledPullSubscriber<Integer>(
				i -> System.out.println("Consumed " + i));
		
		Observable.range(0, 100)
			.subscribe(tester);
		
		System.out.println("Requesting 2 more");
		tester.requestMore(2);
		System.out.println("Requesting 3 more");
		tester.requestMore(3);
		
		// Requesting 2 more
		// Consumed 0
		// Consumed 1
		// Requesting 3 more
		// Consumed 2
		// Consumed 3
		// Consumed 4
	}
	
	
	//
	// Test
	//
	
	@Test
	public void test() {
		List<Integer> received = new ArrayList<>();
		ControlledPullSubscriber<Integer> tester = new ControlledPullSubscriber<Integer>(received::add);
		
		Observable.range(0, 100)
			.subscribe(tester);
		
		assertEquals(Arrays.asList(), received);
		tester.requestMore(2);
		assertEquals(Arrays.asList(0, 1), received);
		tester.requestMore(3);
		assertEquals(Arrays.asList(0, 1, 2, 3, 4), received);
	}
}
