package itrx.chapter3.combining;

import static org.junit.Assert.*;

import java.util.concurrent.TimeUnit;

import org.junit.Test;

import rx.Observable;
import rx.observers.TestSubscriber;
import rx.schedulers.Schedulers;
import rx.schedulers.TestScheduler;

public class MergeTest {

	public void example() {
		Observable.merge(
		        Observable.interval(250, TimeUnit.MILLISECONDS).map(i -> "First"),
		        Observable.interval(150, TimeUnit.MILLISECONDS).map(i -> "Second"))
		    .take(10)
		    .subscribe(System.out::println);
		
//		Second
//		First
//		Second
//		Second
//		First
//		Second
//		Second
//		First
//		Second
//		First
	}
	
	
	//
	// Test
	//
	
	@Test
	public void test() {
		TestScheduler scheduler = Schedulers.test();
		TestSubscriber<Integer> tester = new TestSubscriber<>();
		
		Observable<String> values = Observable.merge(
		        Observable.interval(250, TimeUnit.MILLISECONDS, scheduler).map(i -> "First"),
		        Observable.interval(150, TimeUnit.MILLISECONDS, scheduler).map(i -> "Second"))
		    .take(10);
		
		values
			.distinctUntilChanged()
			.count()
			.subscribe(tester);
		// Each time that merge switches between the two sources, 
		// distinctUntilChanged allows one more value through.
		// If more that 2 values comes through, merge is going back and forth
		
		scheduler.advanceTimeBy(1000, TimeUnit.MILLISECONDS);
		
		assertTrue(tester.getOnNextEvents().get(0) > 2);
	}

}
