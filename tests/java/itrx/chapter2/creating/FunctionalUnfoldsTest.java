package itrx.chapter2.creating;

import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.util.Arrays;
import java.util.concurrent.TimeUnit;

import org.junit.Test;

import rx.Observable;
import rx.Subscription;
import rx.observers.TestSubscriber;
import rx.schedulers.Schedulers;
import rx.schedulers.TestScheduler;

public class FunctionalUnfoldsTest {
	
	public void exampleRange() {
		Observable<Integer> values = Observable.range(10, 15);
		values.subscribe(System.out::println);
		
		// 10
		// ...
		// 24
	}
	
	public void exampleInterval() throws IOException {
		Observable<Long> values = Observable.interval(1000, TimeUnit.MILLISECONDS);
		values.subscribe(
		    v -> System.out.println("Received: " + v),
		    e -> System.out.println("Error: " + e),
		    () -> System.out.println("Completed")
		);
		System.in.read();
		
		// Received: 0
		// Received: 1
		// Received: 2
		// Received: 3
		// ...
	}
	
	public void exampleTimer() throws IOException {
		Observable<Long> values = Observable.timer(1, TimeUnit.SECONDS);
		values.subscribe(
		    v -> System.out.println("Received: " + v),
		    e -> System.out.println("Error: " + e),
		    () -> System.out.println("Completed")
		);
		System.in.read();
		
		// Received: 0
		// Completed
	}
	
	public void exampleTimerWithRepeat() throws IOException {
		Observable<Long> values = Observable.timer(2, 1, TimeUnit.SECONDS);
		values.subscribe(
		    v -> System.out.println("Received: " + v),
		    e -> System.out.println("Error: " + e),
		    () -> System.out.println("Completed")
		);
		System.in.read();
		
		// Received: 0
		// Received: 1
		// Received: 2
		// ...
	}
	
	
	//
	// Tests
	//

	@Test
	public void testRange() {
		TestSubscriber<Integer> tester = new TestSubscriber<Integer>();
		
		Observable<Integer> values = Observable.range(10, 15);
		values.subscribe(tester);
		
		tester.assertReceivedOnNext(Arrays.asList(10,11,12,13,14,15,16,17,18,19,20,21,22,23,24));
		tester.assertTerminalEvent();
		tester.assertNoErrors();
	}
	
	@Test
	public void testInterval() {
		TestSubscriber<Long> tester = new TestSubscriber<Long>();
		TestScheduler scheduler = Schedulers.test();
		
		Observable<Long> values = Observable.interval(1000, TimeUnit.MILLISECONDS, scheduler);
		Subscription subscription = values.subscribe(tester);
		scheduler.advanceTimeBy(4500, TimeUnit.MILLISECONDS);
		
		tester.assertReceivedOnNext(Arrays.asList(0L, 1L, 2L, 3L));
		tester.assertNoErrors();
		assertEquals(tester.getOnCompletedEvents().size(), 0);
		
		subscription.unsubscribe();
	}
	
	@Test
	public void testTimer() {
		TestSubscriber<Long> tester = new TestSubscriber<Long>();
		TestScheduler scheduler = Schedulers.test();
		
		Observable<Long> values = Observable.timer(1, TimeUnit.SECONDS, scheduler);
		Subscription subscription = values.subscribe(tester);
		scheduler.advanceTimeBy(1, TimeUnit.SECONDS);
		
		tester.assertReceivedOnNext(Arrays.asList(0L));
		tester.assertNoErrors();
		tester.assertTerminalEvent();
		
		subscription.unsubscribe();
	}
	
	@Test
	public void testTimerWithRepeat() {
		TestSubscriber<Long> tester = new TestSubscriber<Long>();
		TestScheduler scheduler = Schedulers.test();
		
		Observable<Long> values = Observable.timer(2, 1, TimeUnit.SECONDS, scheduler);
		Subscription subscription = values.subscribe(tester);
		
		scheduler.advanceTimeBy(6, TimeUnit.SECONDS);
		
		tester.assertReceivedOnNext(Arrays.asList(0L,1L,2L));
		tester.assertNoErrors();
		assertEquals(tester.getOnCompletedEvents().size(), 0); // Hasn't terminated
		
		subscription.unsubscribe();
	}

}
