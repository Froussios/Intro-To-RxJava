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
package itrx.chapter4.testing;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.junit.Test;

import rx.schedulers.Schedulers;
import rx.schedulers.TestScheduler;

public class TestSchedulerExample {

	public void exampleAdvanceTo() {
		TestScheduler s = Schedulers.test();

		s.createWorker().schedule(
		        () -> System.out.println("Immediate"));
		s.createWorker().schedule(
		        () -> System.out.println("20s"),
		        20, TimeUnit.SECONDS);
		s.createWorker().schedule(
		        () -> System.out.println("40s"),
		        40, TimeUnit.SECONDS);

		System.out.println("Advancing to 1ms");
		s.advanceTimeTo(1, TimeUnit.MILLISECONDS);
		System.out.println("Virtual time: " + s.now());

		System.out.println("Advancing to 10s");
		s.advanceTimeTo(10, TimeUnit.SECONDS);
		System.out.println("Virtual time: " + s.now());

		System.out.println("Advancing to 40s");
		s.advanceTimeTo(40, TimeUnit.SECONDS);
		System.out.println("Virtual time: " + s.now());
		
		// Advancing to 1ms
		// Immediate
		// Virtual time: 1
		// Advancing to 10s
		// Virtual time: 10000
		// Advancing to 40s
		// 20s
		// 40s
		// Virtual time: 40000
	}
	
	public void exampleTimeBy() {
		TestScheduler s = Schedulers.test();

		s.createWorker().schedule(
		        () -> System.out.println("Immediate"));
		s.createWorker().schedule(
		        () -> System.out.println("20s"),
		        20, TimeUnit.SECONDS);
		s.createWorker().schedule(
		        () -> System.out.println("40s"),
		        40, TimeUnit.SECONDS);

		System.out.println("Advancing by 1ms");
		s.advanceTimeBy(1, TimeUnit.MILLISECONDS);
		System.out.println("Virtual time: " + s.now());

		System.out.println("Advancing by 10s");
		s.advanceTimeBy(10, TimeUnit.SECONDS);
		System.out.println("Virtual time: " + s.now());

		System.out.println("Advancing by 40s");
		s.advanceTimeBy(40, TimeUnit.SECONDS);
		System.out.println("Virtual time: " + s.now());
		
		// Advancing by 1ms
		// Immediate
		// Virtual time: 1
		// Advancing by 10s
		// Virtual time: 10001
		// Advancing by 40s
		// 20s
		// 40s
		// Virtual time: 50001
	}

	public void exampleTriggerActions() {
		TestScheduler s = Schedulers.test();

		s.createWorker().schedule(
		        () -> System.out.println("Immediate"));
		s.createWorker().schedule(
		        () -> System.out.println("20s"),
		        20, TimeUnit.SECONDS);

		s.triggerActions();
		System.out.println("Virtual time: " + s.now());
		
		// Immediate
		// Virtual time: 0
	}
	
	public void exampleCollision() {
		TestScheduler s = Schedulers.test();

		s.createWorker().schedule(
		        () -> System.out.println("First"),
		        20, TimeUnit.SECONDS);
		s.createWorker().schedule(
		        () -> System.out.println("Second"),
		        20, TimeUnit.SECONDS);
		s.createWorker().schedule(
		        () -> System.out.println("Third"),
		        20, TimeUnit.SECONDS);

		s.advanceTimeTo(20, TimeUnit.SECONDS);
		
		// First
		// Second
		// Third
	}
	
	
	//
	// Test
	//
	
	@Test
	public void testAdvanceTo() {
		List<String> execution = new ArrayList<>();
		TestScheduler scheduler = Schedulers.test();

		scheduler.createWorker().schedule(
		        () -> execution.add("Immediate"));
		scheduler.createWorker().schedule(
		        () -> execution.add("20s"),
		        20, TimeUnit.SECONDS);
		scheduler.createWorker().schedule(
		        () -> execution.add("40s"),
		        40, TimeUnit.SECONDS);

		
		assertEquals(0, scheduler.now());
		assertEquals(Arrays.asList(), execution);
		
		scheduler.advanceTimeTo(1, TimeUnit.MILLISECONDS);
		assertEquals(1, scheduler.now());
		assertEquals(Arrays.asList("Immediate"), execution);
		
		scheduler.advanceTimeTo(10, TimeUnit.SECONDS);
		assertEquals(10000, scheduler.now());
		assertEquals(Arrays.asList("Immediate"), execution);
		
		scheduler.advanceTimeTo(40, TimeUnit.SECONDS);
		assertEquals(40000, scheduler.now());
		assertEquals(Arrays.asList("Immediate", "20s", "40s"), execution);
	}

	@Test
	public void testTimeBy() {
		List<String> execution = new ArrayList<>();
		TestScheduler scheduler = Schedulers.test();

		scheduler.createWorker().schedule(
		        () -> execution.add("Immediate"));
		scheduler.createWorker().schedule(
		        () -> execution.add("20s"),
		        20, TimeUnit.SECONDS);
		scheduler.createWorker().schedule(
		        () -> execution.add("40s"),
		        40, TimeUnit.SECONDS);

		assertEquals(0, scheduler.now());
		assertEquals(Arrays.asList(), execution);
		
		scheduler.advanceTimeBy(1, TimeUnit.MILLISECONDS);
		assertEquals(1, scheduler.now());
		assertEquals(Arrays.asList("Immediate"), execution);

		scheduler.advanceTimeBy(10, TimeUnit.SECONDS);
		assertEquals(10001, scheduler.now());
		assertEquals(Arrays.asList("Immediate"), execution);

		scheduler.advanceTimeBy(40, TimeUnit.SECONDS);
		assertEquals(50001, scheduler.now());
		assertEquals(Arrays.asList("Immediate", "20s", "40s"), execution);
	}

	@Test
	public void testTriggerActions() {
		List<String> execution = new ArrayList<>();
		TestScheduler scheduler = Schedulers.test();

		scheduler.createWorker().schedule(
		        () -> execution.add("Immediate"));
		scheduler.createWorker().schedule(
		        () -> execution.add("20s"),
		        20, TimeUnit.SECONDS);

		assertEquals(0, scheduler.now());
		assertEquals(Arrays.asList(), execution);
		scheduler.triggerActions();
		assertEquals(0, scheduler.now());
		assertEquals(Arrays.asList("Immediate"), execution);
	}
	
	@Test
	public void testCollision() {
		List<String> execution = new ArrayList<>();
		TestScheduler scheduler = Schedulers.test();

		scheduler.createWorker().schedule(
		        () -> execution.add("First"),
		        20, TimeUnit.SECONDS);
		scheduler.createWorker().schedule(
		        () -> execution.add("Second"),
		        20, TimeUnit.SECONDS);
		scheduler.createWorker().schedule(
		        () -> execution.add("Third"),
		        20, TimeUnit.SECONDS);

		assertEquals(Arrays.asList(), execution);
		scheduler.advanceTimeTo(20, TimeUnit.SECONDS);
		assertEquals(Arrays.asList("First", "Second", "Third"), execution);
	}
}
