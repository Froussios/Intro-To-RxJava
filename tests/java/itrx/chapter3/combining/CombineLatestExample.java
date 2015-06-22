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
import java.util.concurrent.TimeUnit;

import org.junit.Test;

import rx.Observable;
import rx.observers.TestSubscriber;
import rx.schedulers.Schedulers;
import rx.schedulers.TestScheduler;

public class CombineLatestExample {

	public void example() {
		Observable.combineLatest(
		        Observable.interval(100, TimeUnit.MILLISECONDS)
		            .doOnNext(i -> System.out.println("Left emits")),
		        Observable.interval(150, TimeUnit.MILLISECONDS)
		            .doOnNext(i -> System.out.println("Right emits")),
		        (i1,i2) -> i1 + " - " + i2
		    )
		    .take(6)
		    .subscribe(System.out::println);
		
		// Left emits
		// Right emits
		// 0 - 0
		// Left emits
		// 1 - 0
		// Left emits
		// 2 - 0
		// Right emits
		// 2 - 1
		// Left emits
		// 3 - 1
		// Right emits
		// 3 - 2
	}
	
	
	//
	// Test
	//
	
	@Test
	public void test() {
		TestScheduler scheduler = Schedulers.test();
		TestSubscriber<String> tester = new TestSubscriber<>();
		
		Observable.combineLatest(
		        Observable.interval(100, TimeUnit.MILLISECONDS, scheduler),
		        Observable.interval(150, TimeUnit.MILLISECONDS, scheduler),
		        (i1,i2) -> i1 + " - " + i2
		    )
		    .subscribe(tester);
		
		scheduler.advanceTimeTo(100, TimeUnit.MILLISECONDS);
		scheduler.advanceTimeTo(150, TimeUnit.MILLISECONDS);
		scheduler.advanceTimeTo(200, TimeUnit.MILLISECONDS);
		scheduler.advanceTimeTo(300, TimeUnit.MILLISECONDS);
		
		tester.assertReceivedOnNext(Arrays.asList(
			"0 - 0",
			"1 - 0",
			"1 - 1",
			"2 - 1"
		));
	}

}
