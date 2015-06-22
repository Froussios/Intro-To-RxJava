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

import org.junit.Assert;
import org.junit.Test;

import rx.subjects.BehaviorSubject;

public class SingleThreadedExample {

	public void example() {
		final BehaviorSubject<Integer> subject = BehaviorSubject.create();
		subject.subscribe(i -> {
		    System.out.println("Received " + i + " on " + Thread.currentThread().getId());
		});

		int[] i = {1}; // naughty side-effects for examples only ;)
		Runnable r = () -> {
		    synchronized(i) {
		        System.out.println("onNext(" + i[0] + ") on " + Thread.currentThread().getId());
		        subject.onNext(i[0]++);
		    }
		};

		r.run(); // Execute on main thread
		new Thread(r).start();
		new Thread(r).start();
		
		// onNext(1) on 1
		// Received 1 on 1
		// onNext(2) on 11
		// Received 2 on 11
		// onNext(3) on 12
		// Received 3 on 12
	}
	
	
	//
	// Test
	//
	
	@Test
	public void test() throws InterruptedException {
		long[] emitted = {0, 0, 0};
		long[] received = {0, 0, 0};
		
		final BehaviorSubject<Integer> subject = BehaviorSubject.create();
		subject.subscribe(i -> {
			received[i] = Thread.currentThread().getId();
		});

		int[] i = {0}; // naughty side-effects for examples only ;)
		Runnable r = () -> {
		    synchronized(i) {
		    	int value = i[0];
		    	emitted[value] = Thread.currentThread().getId();
		        subject.onNext(i[0]++);
		    }
		};

		r.run(); // Execute on main thread
		Thread t1 = new Thread(r);
		Thread t2 = new Thread(r);
		t1.start();
		t2.start();
		t1.join();
		t2.join();
		
		Assert.assertArrayEquals("onNext and handler executed on the same thread",
				emitted, received);
	}
}
