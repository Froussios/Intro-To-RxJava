package itrx.chapter3.combining;

import static org.junit.Assert.assertTrue;

import java.util.concurrent.TimeUnit;

import org.junit.Test;

import rx.Observable;
import rx.Subscription;
import rx.observers.TestSubscriber;
import rx.schedulers.Schedulers;
import rx.schedulers.TestScheduler;

public class MergeExample {
	
	public void example() {
		Observable.merge(
		        Observable.interval(250, TimeUnit.MILLISECONDS).map(i -> "First"),
		        Observable.interval(150, TimeUnit.MILLISECONDS).map(i -> "Second"))
		    .take(10)
		    .subscribe(System.out::println);
		
		// Second
		// First
		// Second
		// Second
		// First
		// Second
		// Second
		// First
		// Second
		// First
	}
	
	public void exampleMergeWith() {
		Observable.interval(250, TimeUnit.MILLISECONDS).map(i -> "First")
			.mergeWith(Observable.interval(150, TimeUnit.MILLISECONDS).map(i -> "Second"))
			.take(10)
		    .subscribe(System.out::println);
		
		// Second
		// First
		// Second
		// Second
		// First
		// Second
		// First
		// Second
		// Second
		// First
	}
	
	
	//
	// Test
	//
	
	@Test
	public void test() {
		TestScheduler scheduler = Schedulers.test();
		TestSubscriber<String> tester = new TestSubscriber<>();
		
		Subscription subscription = Observable.merge(
		        Observable.interval(250, TimeUnit.MILLISECONDS, scheduler).map(i -> "First"),
		        Observable.interval(150, TimeUnit.MILLISECONDS, scheduler).map(i -> "Second"))
		    .take(10)
			.distinctUntilChanged()
			.subscribe(tester);
		
		// Each time that merge switches between the two sources, 
		// distinctUntilChanged allows one more value through.
		// If more that 2 values comes through, merge is going back and forth
		scheduler.advanceTimeBy(1000, TimeUnit.MILLISECONDS);
		assertTrue(tester.getOnNextEvents().size() > 2);
		
		subscription.unsubscribe();
	}
	
	@Test
	public void testMergeWith() {
		TestScheduler scheduler = Schedulers.test();
		TestSubscriber<String> tester = new TestSubscriber<>();
		
		Subscription subscription = Observable.interval(250, TimeUnit.MILLISECONDS, scheduler).map(i -> "First")
			.mergeWith(Observable.interval(150, TimeUnit.MILLISECONDS, scheduler).map(i -> "Second"))
			.distinctUntilChanged()
		    .subscribe(tester);
		
		// Each time that merge switches between the two sources, 
		// distinctUntilChanged allows one more value through.
		// If more that 2 values comes through, merge is going back and forth
		scheduler.advanceTimeBy(1000, TimeUnit.MILLISECONDS);
		assertTrue(tester.getOnNextEvents().size() > 2);
		
		subscription.unsubscribe();
	}

}
