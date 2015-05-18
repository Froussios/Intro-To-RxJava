package itrx.chapter2.transforming;

import java.util.Arrays;
import java.util.concurrent.TimeUnit;

import org.junit.Test;

import rx.Observable;
import rx.observers.TestSubscriber;
import rx.schedulers.Schedulers;
import rx.schedulers.TestScheduler;

public class ConcatMapTest {
	
	public void exampleConcatMap() {
		Observable.just(100, 150)
		    .concatMap(i ->
		        Observable.interval(i, TimeUnit.MILLISECONDS)
		            .map(v -> i)
		            .take(3))
		    .subscribe(
	    		System.out::println,
	    		System.out::println,
	    		() -> System.out.println("Completed"));
		
		// 100
		// 100
		// 100
		// 150
		// 150
		// 150
		// Completed
	}
	
	
	//
	// Test
	//
	
	@Test
	public void testConcatMap() {
		TestSubscriber<Integer> tester = new TestSubscriber<>();
		TestScheduler scheduler = Schedulers.test();
		
		Observable.just(100, 150)
		    .concatMap(i ->
		        Observable.interval(i, TimeUnit.MILLISECONDS, scheduler)
		            .map(v -> i)
		            .take(3)
		    )
		    .subscribe(tester);
		
		scheduler.advanceTimeBy(750, TimeUnit.MILLISECONDS);		
		tester.assertReceivedOnNext(Arrays.asList(100, 100, 100, 150, 150, 150));
		tester.assertTerminalEvent();
		tester.assertNoErrors();
	}

}
