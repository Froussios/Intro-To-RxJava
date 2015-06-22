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

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import org.junit.Test;

import rx.Observable;
import rx.schedulers.Schedulers;

public class SubscribeOnExample {

	public void exampleBlocking() {
		System.out.println("Main: " + Thread.currentThread().getId());
		
		Observable.create(o -> {
		        System.out.println("Created on " + Thread.currentThread().getId());
		        o.onNext(1);
		        o.onNext(2);
		        o.onCompleted();
		    })
		    .subscribe(i -> {
		        System.out.println("Received " + i + " on " + Thread.currentThread().getId());
		    });
		
		System.out.println("Finished main: " + Thread.currentThread().getId());
		
		// Main: 1
		// Created on 1
		// Received 1 on 1
		// Received 2 on 1
		// Finished main: 1
	}
	
	public void exampleSubscribeOn() {
		System.out.println("Main: " + Thread.currentThread().getId());
		
		Observable.create(o -> {
		        System.out.println("Created on " + Thread.currentThread().getId());
		        o.onNext(1);
		        o.onNext(2);
		        o.onCompleted();
		    })
		    .subscribeOn(Schedulers.newThread())
		    .subscribe(i -> {
		        System.out.println("Received " + i + " on " + Thread.currentThread().getId());
		    });
		
		System.out.println("Finished main: " + Thread.currentThread().getId());
		
		// Main: 1
		// Created on 1
		// Received 1 on 11
		// Received 2 on 11
		// Finished main: 11
	}
	
	public void exampleIntervalThread() {
		System.out.println("Main: " + Thread.currentThread().getId());

		Observable.interval(100, TimeUnit.MILLISECONDS)
		    .subscribe(i -> {
		        System.out.println("Received " + i + " on " + Thread.currentThread().getId());
		    });

		System.out.println("Finished main: " + Thread.currentThread().getId());
		
		// Main: 1
		// Finished main: 1
		// Received 0 on 11
		// Received 1 on 11
		// Received 2 on 11
	}
	
	
	//
	// Test
	//
	
	@Test
	public void testBlocking() {
		Map<String, Long> threads = new HashMap<>();
		
		threads.put("main", Thread.currentThread().getId());
		
		Observable.create(o -> {
				threads.put("create", Thread.currentThread().getId());
		        o.onNext(1);
		        o.onNext(2);
		        o.onCompleted();
		    })
		    .subscribe(i -> {
		        threads.put("receive", Thread.currentThread().getId());
		    });
		
		assertEquals(Thread.currentThread().getId(), threads.get("main").longValue());
		assertEquals(Thread.currentThread().getId(), threads.get("create").longValue());
		assertEquals(Thread.currentThread().getId(), threads.get("receive").longValue());
	}
	
	@Test
	public void testSubscribeOn() {
		Map<String, Long> threads = new HashMap<>();
		
		threads.put("main", Thread.currentThread().getId());
		
		Observable.create(o -> {
				threads.put("create", Thread.currentThread().getId());
		        o.onNext(1);
		        o.onNext(2);
		        o.onCompleted();
		    })
		    .subscribeOn(Schedulers.newThread())
		    .subscribe(i -> {
		    	threads.put("receive", Thread.currentThread().getId());
		    });
		
		assertEquals("Emitting and receiving on the same thread",
				threads.get("receive"),
				threads.get("create"));
		assertNotEquals("Emitting and receiving not on the main thread",
				threads.get("main"),
				threads.get("receive"));
	}
	
	@Test
	public void testIntervalThread() {
		long[] threads = {0, 0};

		threads[0] = Thread.currentThread().getId();
		
		Observable.interval(100, TimeUnit.MILLISECONDS)
		    .subscribe(i -> {
		        threads[1] = Thread.currentThread().getId();
		    });

		assertNotEquals("interval not executing on main thread", threads[0], threads[1]);
	}

}
