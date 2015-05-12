package itrx.chapter3.combining;

import static org.junit.Assert.*;

import java.util.Arrays;
import java.util.concurrent.TimeUnit;

import org.junit.Test;

import rx.Observable;
import rx.observers.TestSubscriber;
import rx.schedulers.Schedulers;
import rx.schedulers.TestScheduler;

public class SwitchOnNext {

	public void example() {
		Observable.switchOnNext(
		    Observable.interval(100, TimeUnit.MILLISECONDS)
		        .map(i -> 
		            Observable.interval(30, TimeUnit.MILLISECONDS)
		                .map(i2 -> i)
		        )
		    )
		    .take(10)
		    .subscribe(System.out::println);
		
		// 0
		// 0
		// 0
		// 1
		// 1
		// 1
		// 2
		// 2
		// 2
		// 3
	}
	
	
	//
	// Test
	//
	
	@Test
	public void test() {
		TestSubscriber<Object> tester = new TestSubscriber<>();
		TestScheduler scheduler = Schedulers.test();
		
		Observable.switchOnNext(
		    Observable.interval(100, TimeUnit.MILLISECONDS, scheduler)
		        .map(i -> 
		            Observable.interval(30, TimeUnit.MILLISECONDS, scheduler)
		                .map(i2 -> i)
		        )
		    )
		    .distinctUntilChanged()
		    .subscribe(tester);
		
		scheduler.advanceTimeBy(500, TimeUnit.MILLISECONDS);
		tester.assertReceivedOnNext(Arrays.asList(0L,1L,2L,3L));
		tester.assertNoErrors();
		assertEquals(tester.getOnCompletedEvents().size(), 0);
	}

}
