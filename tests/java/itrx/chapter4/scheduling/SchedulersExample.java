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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.Semaphore;

import org.junit.Test;

import rx.Scheduler;
import rx.schedulers.Schedulers;

public class SchedulersExample {
	
	public static void printThread(String message) {
	    System.out.println(message + " on " + Thread.currentThread().getId());
	}

	public void exampleImmediate() {
		Scheduler scheduler = Schedulers.immediate();
		Scheduler.Worker worker = scheduler.createWorker();
		worker.schedule(() -> {
		    System.out.println("Start");
		    worker.schedule(() -> System.out.println("Inner"));
		    System.out.println("End");
		});
		
		// Start
		// Inner
		// End
	}
	
	public void exampleTrampoline() {
		Scheduler scheduler = Schedulers.trampoline();
		Scheduler.Worker worker = scheduler.createWorker();
		worker.schedule(() -> {
		    System.out.println("Start");
		    worker.schedule(() -> System.out.println("Inner"));
		    System.out.println("End");
		});
		
		// Start
		// End
		// Inner
	}
	
	public void exampleNewThread() throws InterruptedException {
		printThread("Main");
		Scheduler scheduler = Schedulers.newThread();
		Scheduler.Worker worker = scheduler.createWorker();
		worker.schedule(() -> {
		    printThread("Start");
		    worker.schedule(() -> printThread("Inner"));
		    printThread("End");
		});
		Thread.sleep(500);
		worker.schedule(() -> printThread("Again"));
		
		// Main on 1
		// Start on 11
		// End on 11
		// Inner on 11
		// Again on 11
	}
	
	
	//
	// Test
	//
	
	@Test
	public void testImmediate() {
		List<String> execution = new ArrayList<>();
		
		Scheduler scheduler = Schedulers.immediate();
		Scheduler.Worker worker = scheduler.createWorker();
		worker.schedule(() -> {
		    execution.add("Start");
		    worker.schedule(() -> execution.add("Inner"));
		    execution.add("End");
		});
		
		assertEquals(Arrays.asList("Start", "Inner", "End"), execution);
	}
	
	@Test
	public void testTrampoline() {
		List<String> execution = new ArrayList<>();
		
		Scheduler scheduler = Schedulers.trampoline();
		Scheduler.Worker worker = scheduler.createWorker();
		worker.schedule(() -> {
			execution.add("Start");
		    worker.schedule(() -> execution.add("Inner"));
		    execution.add("End");
		});
		
		assertEquals(Arrays.asList("Start", "End", "Inner"), execution);
	}
	
	@Test
	public void testNewThread() throws InterruptedException {
		List<String> execution = new ArrayList<>();
		List<Thread> threads = new ArrayList<>();
		Semaphore workfinished = new Semaphore(-2);
		
		Scheduler scheduler = Schedulers.newThread();
		Scheduler.Worker worker = scheduler.createWorker();
		worker.schedule(() -> {
			threads.add(Thread.currentThread());
			execution.add("Start");
		    worker.schedule(() -> {
		    	execution.add("Inner");
		    	workfinished.release();
	    	});
		    execution.add("End");
		    workfinished.release();
		});
		worker.schedule(() -> {
			threads.add(Thread.currentThread());
			workfinished.release();
		});
		
		workfinished.acquire();
		
		assertEquals("Same worker schedules on the same thread",
				threads.get(0),
				threads.get(1));
		assertEquals("New thread used as trampoline", 
				Arrays.asList("Start", "End", "Inner"), 
				execution);
	}

}
