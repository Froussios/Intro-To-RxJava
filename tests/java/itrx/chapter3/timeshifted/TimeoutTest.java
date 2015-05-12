package itrx.chapter3.timeshifted;

import java.util.Arrays;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import static org.junit.Assert.*;
import org.junit.Test;
import static org.hamcrest.CoreMatchers.*;

import rx.Observable;
import rx.observers.TestSubscriber;
import rx.schedulers.Schedulers;
import rx.schedulers.TestScheduler;

public class TimeoutTest {

	public void exampleTimeout() {
		Observable.concat(
		        Observable.interval(100, TimeUnit.MILLISECONDS).take(3),
		        Observable.interval(500, TimeUnit.MILLISECONDS).take(3),
		        Observable.interval(100, TimeUnit.MILLISECONDS).take(3)
		    )
		    .scan(0, (acc, v) -> acc+1)
		    .timeout(200, TimeUnit.MILLISECONDS)
		    .subscribe(
		        System.out::println,
		        System.out::println);
		
		// 0
		// 1
		// 2
		// 3
		// java.util.concurrent.TimeoutException
	}
	
	public void exampleTimeoutWithResume() {
		Observable.concat(
		        Observable.interval(100, TimeUnit.MILLISECONDS).take(3),
		        Observable.interval(500, TimeUnit.MILLISECONDS).take(3),
		        Observable.interval(100, TimeUnit.MILLISECONDS).take(3)
		    )
		    .scan(0, (acc, v) -> acc+1)
		    .timeout(200, TimeUnit.MILLISECONDS, Observable.just(-1))
		    .subscribe(
		        System.out::println,
		        System.out::println);
		
		// 0
		// 1
		// 2
		// 3
		// -1
	}
	
	public void exampleTimeoutPerItem() {
		Observable.concat(
		        Observable.interval(100, TimeUnit.MILLISECONDS).take(3),
		        Observable.interval(500, TimeUnit.MILLISECONDS).take(3),
		        Observable.interval(100, TimeUnit.MILLISECONDS).take(3)
		    )
		    .scan(0, (acc, v) -> acc+1)
		    .timeout(i -> Observable.timer(200, TimeUnit.MILLISECONDS))
		    .subscribe(
		        System.out::println,
		        System.out::println);
		
		// 0
		// 1
		// 2
		// 3
		// java.util.concurrent.TimeoutException
	}
	
	public void exampleTimeoutPerItemWithResume() {
		Observable.concat(
		        Observable.interval(100, TimeUnit.MILLISECONDS).take(3),
		        Observable.interval(500, TimeUnit.MILLISECONDS).take(3),
		        Observable.interval(100, TimeUnit.MILLISECONDS).take(3)
		    )
		    .scan(0, (acc, v) -> acc+1)
		    .timeout(i -> Observable.timer(200, TimeUnit.MILLISECONDS), Observable.just(-1))
		    .subscribe(
		        System.out::println,
		        System.out::println);
		
		// 0
		// 1
		// 2
		// 3
		// -1
	}
	
	
	//
	// Tests
	//
	
	@Test
	public void testTimeout() {
		TestScheduler scheduler = Schedulers.test();
		TestSubscriber<Integer> tester = new TestSubscriber<>();
		
		Observable.concat(
		        Observable.interval(100, TimeUnit.MILLISECONDS, scheduler).take(3),
		        Observable.interval(500, TimeUnit.MILLISECONDS, scheduler).take(3),
		        Observable.interval(100, TimeUnit.MILLISECONDS, scheduler).take(3)
		    )
		    .scan(0, (acc, v) -> acc+1)
		    .timeout(200, TimeUnit.MILLISECONDS, scheduler)
		    .subscribe(tester);
		
		scheduler.advanceTimeBy(2300, TimeUnit.MILLISECONDS);
		tester.assertReceivedOnNext(Arrays.asList(0, 1, 2, 3));
		assertEquals("Observable emits one error",
				1, tester.getOnErrorEvents().size());
		assertThat("Observable times out",
				tester.getOnErrorEvents().get(0), 
				instanceOf(TimeoutException.class));
	}
	
	@Test
	public void testTimeoutWithResume() {
		TestScheduler scheduler = Schedulers.test();
		TestSubscriber<Integer> tester = new TestSubscriber<>();
		
		Observable.concat(
		        Observable.interval(100, TimeUnit.MILLISECONDS, scheduler).take(3),
		        Observable.interval(500, TimeUnit.MILLISECONDS, scheduler).take(3),
		        Observable.interval(100, TimeUnit.MILLISECONDS, scheduler).take(3)
		    )
		    .scan(0, (acc, v) -> acc+1)
		    .timeout(200, TimeUnit.MILLISECONDS, Observable.just(-1), scheduler)
		    .subscribe(tester);
		
		scheduler.advanceTimeBy(2300, TimeUnit.MILLISECONDS);
		tester.assertReceivedOnNext(Arrays.asList(0, 1, 2, 3, -1));
	}
	
	@Test
	public void testTimeoutPerItem() {
		TestScheduler scheduler = Schedulers.test();
		TestSubscriber<Integer> tester = new TestSubscriber<>();
		
		Observable.concat(
		        Observable.interval(100, TimeUnit.MILLISECONDS, scheduler).take(3),
		        Observable.interval(500, TimeUnit.MILLISECONDS, scheduler).take(3),
		        Observable.interval(100, TimeUnit.MILLISECONDS, scheduler).take(3)
		    )
		    .scan(0, (acc, v) -> acc+1)
		    .timeout(i -> Observable.timer(200, TimeUnit.MILLISECONDS, scheduler))
		    .subscribe(tester);
		
		scheduler.advanceTimeBy(2300, TimeUnit.MILLISECONDS);
		tester.assertReceivedOnNext(Arrays.asList(0, 1, 2, 3));
		assertEquals("Observable emits one error",
				1, tester.getOnErrorEvents().size());
		assertThat("Observable times out",
				tester.getOnErrorEvents().get(0), 
				instanceOf(TimeoutException.class));
	}
	
	@Test
	public void testTimeoutPerItemWithResume() {
		TestScheduler scheduler = Schedulers.test();
		TestSubscriber<Integer> tester = new TestSubscriber<>();
		
		Observable.concat(
		        Observable.interval(100, TimeUnit.MILLISECONDS, scheduler).take(3),
		        Observable.interval(500, TimeUnit.MILLISECONDS, scheduler).take(3),
		        Observable.interval(100, TimeUnit.MILLISECONDS, scheduler).take(3)
		    )
		    .scan(0, (acc, v) -> acc+1)
		    .timeout(i -> Observable.timer(200, TimeUnit.MILLISECONDS, scheduler), Observable.just(-1))
		    .subscribe(tester);
		
		scheduler.advanceTimeBy(2300, TimeUnit.MILLISECONDS);
		tester.assertReceivedOnNext(Arrays.asList(0, 1, 2, 3, -1));
	}

}
