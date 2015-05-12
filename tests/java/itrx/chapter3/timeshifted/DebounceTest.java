package itrx.chapter3.timeshifted;

import java.util.Arrays;
import java.util.concurrent.TimeUnit;

import org.junit.Test;

import rx.Observable;
import rx.observers.TestSubscriber;
import rx.schedulers.Schedulers;
import rx.schedulers.TestScheduler;

public class DebounceTest {

	public void exampleDebounce() {
		Observable.concat(
		        Observable.interval(100, TimeUnit.MILLISECONDS).take(3),
		        Observable.interval(500, TimeUnit.MILLISECONDS).take(3),
		        Observable.interval(100, TimeUnit.MILLISECONDS).take(3)
		    )
		    .scan(0, (acc, v) -> acc+1)
		    .debounce(150, TimeUnit.MILLISECONDS)
		    .subscribe(System.out::println);
		
		// 3
		// 4
		// 5
		// 9
	}
	
	public void exampleDebounceDynamic() {
		Observable.concat(
		        Observable.interval(100, TimeUnit.MILLISECONDS).take(3),
		        Observable.interval(500, TimeUnit.MILLISECONDS).take(3),
		        Observable.interval(100, TimeUnit.MILLISECONDS).take(3)
		    )
		    .scan(0, (acc, v) -> acc+1)
		    .debounce(i -> Observable.timer(i * 50, TimeUnit.MILLISECONDS))
		    .subscribe(System.out::println);
		
		// 1
		// 3
		// 4
		// 5
		// 9
	}
	
	
	//
	// Test
	//
	
	@Test
	public void testDebounce() {
		TestScheduler scheduler = Schedulers.test();
		TestSubscriber<Integer> tester = new TestSubscriber<>();
		
		Observable.concat(
		        Observable.interval(100, TimeUnit.MILLISECONDS, scheduler).take(3),
		        Observable.interval(500, TimeUnit.MILLISECONDS, scheduler).take(3),
		        Observable.interval(100, TimeUnit.MILLISECONDS, scheduler).take(3)
		    )
		    .scan(0, (acc, v) -> acc+1)
		    .debounce(150, TimeUnit.MILLISECONDS, scheduler)
		    .subscribe(tester);
		
		scheduler.advanceTimeBy(2100, TimeUnit.MILLISECONDS);
		tester.assertReceivedOnNext(Arrays.asList(3, 4, 5, 9));
	}
	
	@Test
	public void testDebounceDynamic() {
		TestScheduler scheduler = Schedulers.test();
		TestSubscriber<Integer> tester = new TestSubscriber<>();
		
		Observable.concat(
		        Observable.interval(100, TimeUnit.MILLISECONDS, scheduler).take(3),
		        Observable.interval(500, TimeUnit.MILLISECONDS, scheduler).take(3),
		        Observable.interval(100, TimeUnit.MILLISECONDS, scheduler).take(3)
		    )
		    .scan(0, (acc, v) -> acc+1)
		    .debounce(i -> Observable.timer(i * 50, TimeUnit.MILLISECONDS, scheduler))
		    .subscribe(tester);
		
		scheduler.advanceTimeBy(2100, TimeUnit.MILLISECONDS);
		tester.assertReceivedOnNext(Arrays.asList(1, 2, 3, 4, 5, 9));
	}

}
