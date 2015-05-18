package itrx.chapter3.combining;

import java.util.Arrays;
import java.util.concurrent.TimeUnit;

import org.junit.Test;

import rx.Observable;
import rx.observers.TestSubscriber;
import rx.schedulers.Schedulers;
import rx.schedulers.TestScheduler;

public class AmbTest {

	public void exampleAmb() {
		Observable.amb(
		        Observable.timer(100, TimeUnit.MILLISECONDS).map(i -> "First"),
		        Observable.timer(50, TimeUnit.MILLISECONDS).map(i -> "Second"))
		    .subscribe(System.out::println);
		
		// Second
	}
	
	public void exampleAmbWith() {
		Observable.timer(100, TimeUnit.MILLISECONDS).map(i -> "First")
				.ambWith(Observable.timer(50, TimeUnit.MILLISECONDS).map(i -> "Second"))
				.ambWith(Observable.timer(70, TimeUnit.MILLISECONDS).map(i -> "Third"))
			.subscribe(System.out::println);
        
		// Second
	}
	
	
	//
	// Test
	//
	
	@Test
	public void testAmb() {
		TestSubscriber<String> tester = new TestSubscriber<>();
		TestScheduler scheduler = Schedulers.test();
		
		Observable.amb(
		        Observable.timer(100, TimeUnit.MILLISECONDS, scheduler).map(i -> "First"),
		        Observable.timer(50, TimeUnit.MILLISECONDS, scheduler).map(i -> "Second"))
		    .subscribe(tester);
	    
	    scheduler.advanceTimeBy(100, TimeUnit.MILLISECONDS);
	    tester.assertReceivedOnNext(Arrays.asList("Second"));
	    tester.assertTerminalEvent();
	    tester.assertNoErrors();
	}
	
	@Test
	public void testAmbWith() {
		TestSubscriber<String> tester = new TestSubscriber<>();
		TestScheduler scheduler = Schedulers.test();
		
		Observable.timer(100, TimeUnit.MILLISECONDS, scheduler).map(i -> "First")
				.ambWith(Observable.timer(50, TimeUnit.MILLISECONDS, scheduler).map(i -> "Second"))
				.ambWith(Observable.timer(70, TimeUnit.MILLISECONDS, scheduler).map(i -> "Third"))
			.subscribe(tester);
        
		scheduler.advanceTimeBy(100, TimeUnit.MILLISECONDS);
	    tester.assertReceivedOnNext(Arrays.asList("Second"));
	    tester.assertTerminalEvent();
	    tester.assertNoErrors();
	}

}
