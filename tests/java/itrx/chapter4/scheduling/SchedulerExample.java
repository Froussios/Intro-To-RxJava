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

import java.lang.Thread.State;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.junit.Test;

import rx.Scheduler;
import rx.schedulers.Schedulers;
import rx.schedulers.TestScheduler;

public class SchedulerExample {

	public void exampleSchedule() {
		Scheduler scheduler = Schedulers.immediate();
		
		Scheduler.Worker worker = scheduler.createWorker();
		worker.schedule(
		    () -> System.out.println("Action"));
	}
	
	public void exampleScheduleFuture() {
		Scheduler scheduler = Schedulers.newThread();
		long start = System.currentTimeMillis();
		Scheduler.Worker worker = scheduler.createWorker();
		worker.schedule(
		    () -> System.out.println(System.currentTimeMillis()-start),
		    5, TimeUnit.SECONDS);
		worker.schedule(
		    () -> System.out.println(System.currentTimeMillis()-start),
		    5, TimeUnit.SECONDS);
		
		// 5033
		// 5035
	}
	
	public void exampleCancelWork() {
		Scheduler scheduler = Schedulers.newThread();
		long start = System.currentTimeMillis();
		Scheduler.Worker worker = scheduler.createWorker();
		worker.schedule(
		    () -> {
		        System.out.println(System.currentTimeMillis()-start);
		        worker.unsubscribe();
		    },
		    5, TimeUnit.SECONDS);
		worker.schedule(
		    () -> System.out.println(System.currentTimeMillis()-start),
		    5, TimeUnit.SECONDS);
		
		// 5032
	}
	
	public void exampleCancelWithInterrupt() throws InterruptedException {
		Scheduler scheduler = Schedulers.newThread();
		Scheduler.Worker worker = scheduler.createWorker();
		worker.schedule(() -> {
		    try {
		        Thread.sleep(2000);
		        System.out.println("Action completed");
		    } catch (InterruptedException e) {
		        System.out.println("Action interrupted");
		    }
		});
		Thread.sleep(500);
		worker.unsubscribe();
		
		// Action interrupted
	}
	
	
	//
	// Test
	//
	
	@Test
	public void testSchedule() {
		List<Boolean> executed = new ArrayList<>();
		
		Scheduler scheduler = Schedulers.immediate();
		Scheduler.Worker worker = scheduler.createWorker();
		worker.schedule(
		    () -> executed.add(true));
		
		assertEquals(Arrays.asList(new Boolean(true)), executed);
	}
	
	@Test
	public void testScheduleFuture() {
		long[] executionTimes = {0, 0};
		
		TestScheduler scheduler = Schedulers.test();
		Scheduler.Worker worker = scheduler.createWorker();
		worker.schedule(
		    () -> executionTimes[0] = scheduler.now(),
		    5, TimeUnit.SECONDS);
		worker.schedule(
		    () -> executionTimes[1] = scheduler.now(),
		    5, TimeUnit.SECONDS);
		
		scheduler.advanceTimeTo(5000, TimeUnit.MILLISECONDS);
		assertEquals("First task executed on time", 5000, executionTimes[0]);
		assertEquals("Second task executed on time", 5000, executionTimes[1]);
	}
	
	@Test
	public void testCancelWork() {
		long[] executionTimes = {0, 0};
		
		TestScheduler scheduler = Schedulers.test();
		Scheduler.Worker worker = scheduler.createWorker();
		worker.schedule(
		    () -> {
		        executionTimes[0] = scheduler.now();
		        worker.unsubscribe();
		    },
		    5, TimeUnit.SECONDS);
		worker.schedule(
		    () -> executionTimes[1] = scheduler.now(),
		    5, TimeUnit.SECONDS);
		
		scheduler.advanceTimeTo(5000, TimeUnit.MILLISECONDS);		
		assertEquals("First task executed on time", 5000, executionTimes[0]);
		assertEquals("Second task never executed", 0, executionTimes[1]);
	}
	
	@Test
	public void testCancelWithInterrupt() throws InterruptedException {
		Scheduler scheduler = Schedulers.newThread();
		Scheduler.Worker worker = scheduler.createWorker();
		Thread[] workerThread = {null};
		Boolean[] interrupted = {false};
		worker.schedule(() -> {
		    try {
		    	workerThread[0] = Thread.currentThread();
		        Thread.sleep(100);
		    } catch (InterruptedException e) {
		    	interrupted[0] = true;
		    }
		});
		
		while (workerThread[0] == null || 
				workerThread[0].getState() != State.TIMED_WAITING)
			Thread.sleep(1); // Wait for task to sleep
		worker.unsubscribe();
		workerThread[0].join();
		assertTrue("Task must be interrupted before completing", interrupted[0]);
	}

}
