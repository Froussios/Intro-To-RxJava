package itrx.chapter3.timeshifted;

import java.util.Arrays;
import java.util.concurrent.TimeUnit;

import org.junit.Test;

import rx.Observable;
import rx.observers.TestSubscriber;
import rx.schedulers.Schedulers;
import rx.schedulers.TestScheduler;

public class SampleExample {

	public void exampleSample() {
		Observable.interval(150, TimeUnit.MILLISECONDS)
		    .sample(1, TimeUnit.SECONDS)
		    .take(3)
		    .subscribe(System.out::println);
		
		// 5
		// 12
		// 18
	}
	
	
	//
	// Test
	//
	
	@Test
	public void testSample() {
		TestScheduler scheduler = Schedulers.test();
		TestSubscriber<Long> tester = new TestSubscriber<>();
		
		Observable.interval(150, TimeUnit.MILLISECONDS, scheduler)
		    .sample(1, TimeUnit.SECONDS, scheduler)
		    .take(3)
		    .subscribe(tester);
		
		scheduler.advanceTimeBy(3, TimeUnit.SECONDS);
		tester.assertReceivedOnNext(Arrays.asList(5L, 12L, 18L));
		tester.assertTerminalEvent();
		tester.assertNoErrors();
	}
}
