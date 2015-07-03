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

import static org.junit.Assert.*;

import java.lang.Thread.State;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;

import org.junit.Test;

import rx.Observable;
import rx.schedulers.Schedulers;
import rx.schedulers.TestScheduler;
import rx.subjects.PublishSubject;
import rx.subjects.Subject;

public class IterablesExample {

	public void exampleToIterable() {
		Observable<Long> values = Observable.interval(500, TimeUnit.MILLISECONDS);

		Iterable<Long> iterable = values.take(5).toBlocking().toIterable();
		for (long l : iterable) {
		    System.out.println(l);
		}
		
		// 0
		// 1
		// 2
		// 3
		// 4
	}
	
	public void exampleNext() throws InterruptedException {
		Observable<Long> values = Observable.interval(500, TimeUnit.MILLISECONDS);

		values.take(5)
		    .subscribe(v -> System.out.println("Emitted: " + v));

		Iterable<Long> iterable = values.take(5).toBlocking().next();
		for (long l : iterable) {
		    System.out.println(l);
		    Thread.sleep(750);
		}
		
		// Emitted: 0
		// 0
		// Emitted: 1
		// Emitted: 2
		// 2
		// Emitted: 3
		// Emitted: 4
		// 4
	}
	
	public void exampleLatest() throws InterruptedException {
		Observable<Long> values = Observable.interval(500, TimeUnit.MILLISECONDS);

		values.take(5)
		    .subscribe(v -> System.out.println("Emitted: " + v));

		Iterable<Long> iterable = values.take(5).toBlocking().latest();
		for (long l : iterable) {
		    System.out.println(l);
		    Thread.sleep(750);
		}
		
		// Emitted: 0
		// 0
		// Emitted: 1
		// 1
		// Emitted: 2
		// Emitted: 3
		// 3
		// Emitted: 4
	}
	
	public void exampleMostRecent() throws InterruptedException {
		Observable<Long> values = Observable.interval(500, TimeUnit.MILLISECONDS);

		values.take(5)
		    .subscribe(v -> System.out.println("Emitted: " + v));

		Iterable<Long> iterable = values.take(5).toBlocking().mostRecent(-1L);
		for (long l : iterable) {
		    System.out.println(l);
		    Thread.sleep(400);
		}
		
		// -1
		// -1
		// Emitted: 0
		// 0
		// Emitted: 1
		// 1
		// Emitted: 2
		// 2
		// Emitted: 3
		// 3
		// 3
		// Emitted: 4
	}
	
	
	//
	// Tests
	//
	
	@Test
	public void testToIterable() throws InterruptedException {
		TestScheduler scheduler = Schedulers.test();
		List<Long> received = new ArrayList<>();
		
		Observable<Long> values = Observable.interval(500, TimeUnit.MILLISECONDS, scheduler);

		Thread thread = new Thread(() -> {
			Iterable<Long> iterable = 
				values
					.take(5)
					.toBlocking()
					.toIterable();
			for (long l : iterable) {
			    received.add(l);
			}
		});
		thread.start();
		
		assertEquals(received, Arrays.asList());
		// Wait for blocking call to block before producing values
		while (thread.getState() != State.WAITING)
			Thread.sleep(1);
		scheduler.advanceTimeBy(3, TimeUnit.SECONDS);
		thread.join(50);
		assertEquals(received, Arrays.asList(0L, 1L, 2L, 3L, 4L));
	}
	
	@Test
	public void testNext() throws InterruptedException {
		Subject<Integer, Integer> subject = PublishSubject.create();
		List<Integer> received = new ArrayList<>();
		Semaphore produce = new Semaphore(0);
		Semaphore consume = new Semaphore(0);
		
		Thread thread = new Thread(() -> {
			Iterable<Integer> iterable = subject.toBlocking().next();
			Iterator<Integer> iterator = iterable.iterator();
			try {
				while (true) {
					consume.acquire();
					produce.release();
					if (!iterator.hasNext()) {
						produce.release();
						break;
					}
					received.add(iterator.next());
					produce.release();
				}
			} catch (InterruptedException e) {
			}
		});
		thread.start();
		
		consume.release();
		produce.acquire();
		while (thread.getState() != State.WAITING) Thread.sleep(1);
		subject.onNext(0);
		produce.acquire();
		assertEquals(Arrays.asList(0), received);
		
		subject.onNext(1);
		consume.release();
		produce.acquire();
		while (thread.getState() != State.WAITING) Thread.sleep(1);
		subject.onNext(2);
		produce.acquire();
		assertEquals(Arrays.asList(0, 2), received);
		
		subject.onNext(3);
		consume.release();
		produce.acquire();
		while (thread.getState() != State.WAITING) Thread.sleep(1);
		subject.onNext(4);
		produce.acquire();
		assertEquals(Arrays.asList(0, 2, 4), received);
		
		consume.release();
		subject.onCompleted();
		
		thread.join();
	}
	
	@Test
	public void testLatest() throws InterruptedException {
		Subject<Integer, Integer> subject = PublishSubject.create();
		List<Integer> received = new ArrayList<>();
		Semaphore produce = new Semaphore(0);
		Semaphore consume = new Semaphore(0);
		
		Thread thread = new Thread(() -> {
			Iterable<Integer> iterable = subject.toBlocking().latest();
			Iterator<Integer> iterator = iterable.iterator();
			try {
				while (true) {
					consume.acquire();
					produce.release();
					if (!iterator.hasNext()) {
						produce.release();
						break;
					}
					received.add(iterator.next());
					produce.release();
				}
			} catch (InterruptedException e) {
			}
		});
		thread.start();
		
		consume.release();
		produce.acquire();
		while (thread.getState() != State.WAITING) Thread.sleep(1);
		assertEquals(Arrays.asList(), received);
		
		subject.onNext(0);
		produce.acquire();
		assertEquals(Arrays.asList(0), received);
		
		consume.release();
		produce.acquire();
		while (thread.getState() != State.WAITING) Thread.sleep(1);
		subject.onNext(1);
		produce.acquire();
		assertEquals(Arrays.asList(0, 1), received);
		
		subject.onNext(2);
		subject.onNext(3);
		consume.release();
		produce.acquire();
		produce.acquire();
		assertEquals(Arrays.asList(0, 1, 3), received);
		
		subject.onNext(4);
		subject.onCompleted();
		consume.release();
		produce.acquire();
		produce.acquire();
		assertEquals(Arrays.asList(0, 1, 3), received);
		
		thread.join();
	}
	
	@Test
	public void testMostRecent() throws InterruptedException {
		Subject<Integer, Integer> subject = PublishSubject.create();
		List<Integer> received = new ArrayList<>();
		Semaphore produce = new Semaphore(0);
		Semaphore consume = new Semaphore(0);
		
		Thread thread = new Thread(() -> {
			Iterable<Integer> iterable = subject.toBlocking().mostRecent(-1);
			Iterator<Integer> iterator = iterable.iterator();
			try {
				while (true) {
					consume.acquire();
					produce.release();
					if (!iterator.hasNext()) {
						produce.release();
						break;
					}
					received.add(iterator.next());
					produce.release();
				}
			} catch (InterruptedException e) {
			}
		});
		thread.start();
		
		consume.release();
		produce.acquire();
		produce.acquire();
		assertEquals(Arrays.asList(-1), received);
		
		consume.release();
		produce.acquire();
		produce.acquire();
		assertEquals(Arrays.asList(-1, -1), received);
		
		subject.onNext(0);
		consume.release();
		produce.acquire();
		produce.acquire();
		assertEquals(Arrays.asList(-1, -1, 0), received);
		
		subject.onNext(1);
		subject.onNext(2);
		consume.release();
		produce.acquire();
		produce.acquire();
		assertEquals(Arrays.asList(-1, -1, 0, 2), received);
		
		subject.onNext(3);
		consume.release();
		produce.acquire();
		produce.acquire();
		assertEquals(Arrays.asList(-1, -1, 0, 2, 3), received);
		consume.release();
		produce.acquire();
		produce.acquire();
		assertEquals(Arrays.asList(-1, -1, 0, 2, 3, 3), received);
		
		subject.onNext(4);
		subject.onCompleted();
		consume.release();
		produce.acquire();
		produce.acquire();
		assertEquals(Arrays.asList(-1, -1, 0, 2, 3, 3), received);

		thread.join();
	}
}
