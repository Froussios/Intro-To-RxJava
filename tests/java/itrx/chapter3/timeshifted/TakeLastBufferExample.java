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
package itrx.chapter3.timeshifted;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.junit.Test;

import rx.Observable;
import rx.observers.TestSubscriber;
import rx.schedulers.Schedulers;
import rx.schedulers.TestScheduler;

public class TakeLastBufferExample {

	public void exampleByCount() {
		Observable.range(0, 5)
			.takeLastBuffer(2)
			.subscribe(System.out::println);
		
		// [3, 4]
	}
	
	public void exampleByTime() {
		Observable.interval(100, TimeUnit.MILLISECONDS)
			.take(5)
			.takeLastBuffer(200, TimeUnit.MILLISECONDS)
			.subscribe(System.out::println);
		
		// [2, 3, 4]
	}
	
	public void exampleByCountAndTime() {
		Observable.interval(100, TimeUnit.MILLISECONDS)
			.take(5)
			.takeLastBuffer(2, 200, TimeUnit.MILLISECONDS)
			.subscribe(System.out::println);
		
		// [3, 4]
	}
	
	
	//
	// Tests
	//
	
	@Test
	public void testByCount() {
		TestSubscriber<List<Integer>> tester = new TestSubscriber<>();
		
		Observable.range(0, 5)
			.takeLastBuffer(2)
			.subscribe(tester);
		
		tester.assertReceivedOnNext(Arrays.asList(
			Arrays.asList(3, 4)
		));
		tester.assertTerminalEvent();
		tester.assertNoErrors();
	}
	
	@Test
	public void testByTime() {
		TestSubscriber<List<Long>> tester = new TestSubscriber<>();
		TestScheduler scheduler = Schedulers.test();
		
		Observable.interval(100, TimeUnit.MILLISECONDS, scheduler)
			.take(5)
			.takeLastBuffer(200, TimeUnit.MILLISECONDS, scheduler)
			.subscribe(tester);
		
		scheduler.advanceTimeBy(500, TimeUnit.MILLISECONDS);
		tester.assertReceivedOnNext(Arrays.asList(
			Arrays.asList(2L, 3L, 4L)
		));
		tester.assertTerminalEvent();
		tester.assertNoErrors();
	}
	
	@Test
	public void testByCountAndTime() {
		TestSubscriber<List<Long>> tester = new TestSubscriber<>();
		TestScheduler scheduler = Schedulers.test();
		
		Observable.interval(100, TimeUnit.MILLISECONDS, scheduler)
			.take(5)
			.takeLastBuffer(2, 200, TimeUnit.MILLISECONDS, scheduler)
			.subscribe(tester);
		
		scheduler.advanceTimeBy(500, TimeUnit.MILLISECONDS);
		tester.assertReceivedOnNext(Arrays.asList(
			Arrays.asList(3L, 4L)
		));
		tester.assertTerminalEvent();
		tester.assertNoErrors();
	}
}
