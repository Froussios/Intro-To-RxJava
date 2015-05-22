package itrx.chapter3.combining;

import java.util.Arrays;
import java.util.concurrent.TimeUnit;

import org.junit.Test;

import rx.Observable;
import rx.observers.TestSubscriber;
import rx.schedulers.Schedulers;
import rx.schedulers.TestScheduler;

public class SwitchMapTest {
	
	public void example() {
		Observable.interval(100, TimeUnit.MILLISECONDS)
			.switchMap(i -> 
				Observable.interval(30, TimeUnit.MILLISECONDS)
					.map(l -> i))
			.take(9)
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
	}
	
	
	//
	// Test
	//
	
	@Test
	public void test() {
		TestSubscriber<Object> tester = new TestSubscriber<>();
		TestScheduler scheduler = Schedulers.test();
		
		Observable.interval(100, TimeUnit.MILLISECONDS, scheduler)
			.switchMap(i -> 
				Observable.interval(30, TimeUnit.MILLISECONDS, scheduler)
					.map(l -> i))
			.take(9)
			.distinctUntilChanged()
			.subscribe(tester);
		
		scheduler.advanceTimeBy(400, TimeUnit.MILLISECONDS);
		tester.assertReceivedOnNext(Arrays.asList(0L, 1L, 2L));
		tester.assertTerminalEvent();
		tester.assertNoErrors();
	}

}
