package itrx.chapter3.hotandcold;

import java.util.Arrays;
import java.util.concurrent.TimeUnit;

import org.junit.Test;

import rx.Observable;
import rx.observers.TestSubscriber;
import rx.schedulers.Schedulers;
import rx.schedulers.TestScheduler;

public class ColdExample {

	public void example() throws InterruptedException {
		Observable<Long> cold = 
				Observable
					.interval(200, TimeUnit.MILLISECONDS)
					.take(5);

		cold.subscribe(i -> System.out.println("First: " + i));
		Thread.sleep(500);
		cold.subscribe(i -> System.out.println("Second: " + i));
		
		// First: 0
		// First: 1
		// First: 2
		// Second: 0
		// First: 3
		// Second: 1
		// First: 4
		// Second: 2
		// Second: 3
		// Second: 4
	}
	
	
	//
	// Test
	//
	
	@Test
	public void test() {
		TestScheduler scheduler = Schedulers.test();
		TestSubscriber<Long> tester1 = new TestSubscriber<>();
		TestSubscriber<Long> tester2 = new TestSubscriber<>();
		
		Observable<Long> cold = 
				Observable
					.interval(200, TimeUnit.MILLISECONDS, scheduler)
					.take(5);

		cold.subscribe(tester1);
		tester1.assertReceivedOnNext(Arrays.asList());
		tester2.assertReceivedOnNext(Arrays.asList());
		
		scheduler.advanceTimeTo(500, TimeUnit.MILLISECONDS);
		cold.subscribe(tester2);
		tester1.assertReceivedOnNext(Arrays.asList(0L, 1L));
		tester2.assertReceivedOnNext(Arrays.asList());
		
		scheduler.advanceTimeTo(1000, TimeUnit.MILLISECONDS);
		tester1.assertReceivedOnNext(Arrays.asList(0L, 1L, 2L, 3L, 4L));
		tester2.assertReceivedOnNext(Arrays.asList(0L, 1L));
		
		scheduler.advanceTimeTo(1500, TimeUnit.MILLISECONDS);
		tester1.assertReceivedOnNext(Arrays.asList(0L, 1L, 2L, 3L, 4L));
		tester1.assertReceivedOnNext(Arrays.asList(0L, 1L, 2L, 3L, 4L));
	}

}
