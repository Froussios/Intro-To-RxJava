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

import org.junit.Assert;
import org.junit.Test;

import rx.Observable;
import rx.schedulers.Schedulers;

public class ObserveOnExample {

	public void exampleObserveOn() {
		Observable.create(o -> {
		        System.out.println("Created on " + Thread.currentThread().getId());
		        o.onNext(1);
		        o.onNext(2);
		        o.onCompleted();
		    })
		    .observeOn(Schedulers.newThread())
		    .subscribe(i ->
		        System.out.println("Received " + i + " on " + Thread.currentThread().getId()));
		
		// Created on 1
		// Received 1 on 13
		// Received 2 on 13
	}

	public void exampleObserveOnBeforeAfter() {
		Observable.create(o -> {
		        System.out.println("Created on " + Thread.currentThread().getId());
		        o.onNext(1);
		        o.onNext(2);
		        o.onCompleted();
		    })
		    .doOnNext(i -> 
		        System.out.println("Before " + i + " on " + Thread.currentThread().getId()))
		    .observeOn(Schedulers.newThread())
		    .doOnNext(i -> 
		        System.out.println("After " + i + " on " + Thread.currentThread().getId()))
		    .subscribe();
		
		// Created on 1
		// Before 1 on 1
		// Before 2 on 1
		// After 1 on 13
		// After 2 on 13
	}
	
	
	//
	// Test
	//
	
	@Test
	public void testObserveOn() {
		long[] threads = {0, 0};
		
		Observable.create(o -> {
		        threads[0] = Thread.currentThread().getId();
		        o.onNext(1);
		        o.onNext(2);
		        o.onCompleted();
		    })
		    .observeOn(Schedulers.newThread())
		    .subscribe(i -> threads[1] = Thread.currentThread().getId());
		
		Assert.assertNotEquals("Create and receive on different threads", threads[0], threads[1]);
	}

	@Test
	public void testObserveOnBeforeAfter() {
		long[] threads = {0, 0, 0, 0, 0};
		
		threads[0] = Thread.currentThread().getId();
		
		Observable.create(o -> {
				threads[1] = Thread.currentThread().getId();
		        o.onNext(1);
		        o.onNext(2);
		        o.onCompleted();
		    })
		    .doOnNext(i -> threads[2] = Thread.currentThread().getId())
		    .observeOn(Schedulers.newThread())
		    .doOnNext(i -> threads[3] = Thread.currentThread().getId())
		    .subscribe(i -> threads[4] = Thread.currentThread().getId());
		
		assertEquals("Create on main thread", threads[0], threads[1]);
		assertEquals("Synchronous before observeOn", threads[1], threads[2]);
		assertEquals("Synchronous after observeOn", threads[3], threads[4]);
		assertNotEquals("Before and after observeOn on different threads", threads[2], threads[3]);
	}
	
}
