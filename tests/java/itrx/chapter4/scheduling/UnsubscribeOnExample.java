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
package itrx.chapter4.scheduling;

import static org.junit.Assert.*;

import java.util.Arrays;

import org.junit.Test;

import rx.Observable;
import rx.schedulers.Schedulers;

public class UnsubscribeOnExample {

	public static void example() {
		Observable<Object> source = Observable.using(
			() -> {
				System.out.println("Subscribed on " + Thread.currentThread().getId());
				return Arrays.asList(1,2);
			},
			(ints) -> {
				System.out.println("Producing on " + Thread.currentThread().getId());
				return Observable.from(ints);
			},
			(ints) -> {
				System.out.println("Unubscribed on " + Thread.currentThread().getId());
			}
		);
		
		source
			.unsubscribeOn(Schedulers.newThread())
			.subscribe(System.out::println);
		
		// Subscribed on 1
		// Producing on 1
		// 1
		// 2
		// Unubscribed on 11
	}
	
	
	//
	// Test
	//
	
	@Test
	public void test() {
		long[] threads = {0, 0, 0};
		
		Observable<Object> source = Observable.using(
				() -> {
					threads[0] = Thread.currentThread().getId();
					return Arrays.asList(1,2);
				},
				(ints) -> {
					threads[1] = Thread.currentThread().getId();
					return Observable.from(ints);
				},
				(ints) -> {
					threads[2] = Thread.currentThread().getId();
				}
			);
			
			source
				.unsubscribeOn(Schedulers.newThread())
				.subscribe();
			
			assertEquals(threads[0], threads[1]);
			assertNotEquals(threads[0], threads[2]);
	}

}
