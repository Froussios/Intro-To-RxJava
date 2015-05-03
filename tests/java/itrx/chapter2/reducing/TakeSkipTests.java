package itrx.chapter2.reducing;

import static org.junit.Assert.*;

import java.util.Arrays;
import java.util.concurrent.TimeUnit;

import org.junit.Test;

import rx.Observable;
import rx.Subscription;
import rx.observers.TestSubscriber;
import rx.schedulers.Schedulers;
import rx.schedulers.TestScheduler;

public class TakeSkipTests {

	
	public void exampleTake() {
		Observable<Integer> values = Observable.range(0, 5);
		
		Subscription first2 = values
		    .take(2)
		    .subscribe(
		        v -> System.out.println(v),
		        e -> System.out.println("Error: " + e),
		        () -> System.out.println("Completed")
		    );
		
//		0
//		1
//		Completed
	}
	
	public void exampleSkip() {
		Observable<Integer> values = Observable.range(0, 5);

		Subscription subscription = values
		    .skip(2)
		    .subscribe(
		        v -> System.out.println(v),
		        e -> System.out.println("Error: " + e),
		        () -> System.out.println("Completed")
		    );
		
//		2
//		3
//		4
//		Completed
	}
	
	public void exampleTakeTime() {
		Observable<Long> values = Observable.interval(100, TimeUnit.MILLISECONDS);

		Subscription subscription = values
		    .take(250, TimeUnit.MILLISECONDS)
		    .subscribe(
		        v -> System.out.println(v),
		        e -> System.out.println("Error: " + e),
		        () -> System.out.println("Completed")
		    );
		
//		0
//		1
//		Completed
	}
	
	public void exampleSkipTime() {
		Observable<Long> values = Observable.interval(100, TimeUnit.MILLISECONDS);

		Subscription subscription = values
		    .skip(250, TimeUnit.MILLISECONDS)
		    .subscribe(
		        v -> System.out.println(v),
		        e -> System.out.println("Error: " + e),
		        () -> System.out.println("Completed")
		    );
		
//		2
//		3
//		4
//		Completed
	}
	
	public void exampleTakeWhile() {
		Observable<Long> values = Observable.interval(100, TimeUnit.MILLISECONDS);

		Subscription subscription = values
		    .takeWhile(v -> v < 2)
		    .subscribe(
		        v -> System.out.println(v),
		        e -> System.out.println("Error: " + e),
		        () -> System.out.println("Completed")
		    );
		
//		0
//		1
//		Completed
	}
	
	public void exampleSkipWhile() {
		Observable<Long> values = Observable.interval(100, TimeUnit.MILLISECONDS);

		Subscription subscription = values
		    .skipWhile(v -> v < 2)
		    .subscribe(
		        v -> System.out.println(v),
		        e -> System.out.println("Error: " + e),
		        () -> System.out.println("Completed")
		    );
		
//		2
//		3
//		4
//		...
	}
	
	public void exampleSkipLast() {
		Observable<Integer> values = Observable.range(0,5);

		Subscription subscription = values
		    .skipLast(2)
		    .subscribe(
		        v -> System.out.println(v),
		        e -> System.out.println("Error: " + e),
		        () -> System.out.println("Completed")
		    );
		
//		0
//		1
//		2
//		Completed
	}
	
	public void exampleTakeLast() {
		Observable<Integer> values = Observable.range(0,5);

		Subscription subscription = values
		    .takeLast(2)
		    .subscribe(
		        v -> System.out.println(v),
		        e -> System.out.println("Error: " + e),
		        () -> System.out.println("Completed")
		    );
		
//		3
//		4
//		...
	}
	
	public void exampleTakeUntil() {
		Observable<Long> values = Observable.interval(100,TimeUnit.MILLISECONDS);
		Observable<Long> cutoff = Observable.timer(250, TimeUnit.MILLISECONDS);

		Subscription subscription = values
		    .takeUntil(cutoff)
		    .subscribe(
		        v -> System.out.println(v),
		        e -> System.out.println("Error: " + e),
		        () -> System.out.println("Completed")
		    );
		
//		0
//		1
//		Completed
	}
	
	public void exampleSkipUntil() {
		Observable<Long> values = Observable.interval(100,TimeUnit.MILLISECONDS);
		Observable<Long> cutoff = Observable.timer(250, TimeUnit.MILLISECONDS);

		Subscription subscription = values
		    .skipUntil(cutoff)
		    .subscribe(
		        v -> System.out.println(v),
		        e -> System.out.println("Error: " + e),
		        () -> System.out.println("Completed")
		    );
		
//		2
//		3
//		4
//		...
	}
	
	
	
	@Test
	public void testTake() {
		TestSubscriber<Integer> tester = new TestSubscriber<Integer>();
		
		Observable<Integer> values = Observable.range(0, 5);
		
		Subscription first2 = values
			    .take(2)
			    .subscribe(tester);
		
		tester.assertReceivedOnNext(Arrays.asList(0,1));
		tester.assertTerminalEvent();
		tester.assertNoErrors();
	}
	
	@Test
	public void testSkip() {
		TestSubscriber<Integer> tester = new TestSubscriber<Integer>();
		
		Observable<Integer> values = Observable.range(0, 5);
		
		Subscription subscription = values
			    .skip(2)
			    .subscribe(tester);
		
		tester.assertReceivedOnNext(Arrays.asList(2,3,4));
		tester.assertTerminalEvent();
		tester.assertNoErrors();
	}
	
	@Test
	public void testTakeTime() {
		TestScheduler scheduler = Schedulers.test();
		TestSubscriber<Long> tester = new TestSubscriber<Long>();
		
		Observable<Long> values = Observable.interval(100, TimeUnit.MILLISECONDS ,scheduler);

		Subscription subscription = values
		    .take(250, TimeUnit.MILLISECONDS, scheduler)
		    .subscribe(tester);
		
		scheduler.advanceTimeBy(1000, TimeUnit.MILLISECONDS);
		
		tester.assertReceivedOnNext(Arrays.asList(0L,1L));
		tester.assertTerminalEvent();
		tester.assertNoErrors();
	}
	
	@Test
	public void testSkipTime() {
		TestScheduler scheduler = Schedulers.test();
		TestSubscriber<Long> tester = new TestSubscriber<Long>();
		
		Observable<Long> values = Observable.interval(100, TimeUnit.MILLISECONDS, scheduler);

		Subscription subscription = values
		    .skip(250, TimeUnit.MILLISECONDS, scheduler)
		    .subscribe(tester);
		
		scheduler.advanceTimeBy(550, TimeUnit.MILLISECONDS);
		
		tester.assertReceivedOnNext(Arrays.asList(2L, 3L, 4L));
		tester.assertNoErrors();
	}
	
	@Test
	public void testTakeWhile() {
		TestScheduler scheduler = Schedulers.test();
		TestSubscriber<Long> tester = new TestSubscriber<Long>();
		
		Observable<Long> values = Observable.interval(100, TimeUnit.MILLISECONDS, scheduler);

		Subscription subscription = values
		    .takeWhile(v -> v < 2)
		    .subscribe(tester);
		
		scheduler.advanceTimeBy(550, TimeUnit.MILLISECONDS);
		
		tester.assertReceivedOnNext(Arrays.asList(0L, 1L));
		tester.assertTerminalEvent();
		tester.assertNoErrors();
	}
	
	@Test
	public void testSkipWhile() {
		TestScheduler scheduler = Schedulers.test();
		TestSubscriber<Long> tester = new TestSubscriber<Long>();
		
		Observable<Long> values = Observable.interval(100, TimeUnit.MILLISECONDS, scheduler);

		Subscription subscription = values
		    .skipWhile(v -> v < 2)
		    .subscribe(tester);
		
		scheduler.advanceTimeBy(550, TimeUnit.MILLISECONDS);
		
		tester.assertReceivedOnNext(Arrays.asList(2L, 3L, 4L));
		tester.assertNoErrors();
	}
	
	@Test
	public void testerSkipLast() {
		TestSubscriber<Integer> tester = new TestSubscriber<Integer>();
		
		Observable<Integer> values = Observable.range(0,5);

		Subscription subscription = values
		    .skipLast(2)
		    .subscribe(tester);
		
		tester.assertReceivedOnNext(Arrays.asList(0,1,2));
		tester.assertTerminalEvent();
		tester.assertNoErrors();
	}
	
	@Test
	public void testTakeLast() {
		TestSubscriber<Integer> tester = new TestSubscriber<Integer>();
		
		Observable<Integer> values = Observable.range(0,5);

		Subscription subscription = values
		    .takeLast(2)
		    .subscribe(tester);
		
		tester.assertReceivedOnNext(Arrays.asList(3,4));
		tester.assertTerminalEvent();
		tester.assertNoErrors();
	}
	
	@Test
	public void testTakeUntil() {
		TestScheduler scheduler = Schedulers.test();
		TestSubscriber<Long> tester = new TestSubscriber<Long>();
		
		Observable<Long> values = Observable.interval(100,TimeUnit.MILLISECONDS, scheduler);
		Observable<Long> cutoff = Observable.timer(250, TimeUnit.MILLISECONDS, scheduler);

		Subscription subscription = values
		    .takeUntil(cutoff)
		    .subscribe(tester);
		
		scheduler.advanceTimeBy(550, TimeUnit.MILLISECONDS);
		
		tester.assertReceivedOnNext(Arrays.asList(0L,1L));
		tester.assertTerminalEvent();
		tester.assertNoErrors();
	}
	
	@Test
	public void testSkipUntil() {
		TestScheduler scheduler = Schedulers.test();
		TestSubscriber<Long> tester = new TestSubscriber<Long>();
		
		Observable<Long> values = Observable.interval(100,TimeUnit.MILLISECONDS, scheduler);
		Observable<Long> cutoff = Observable.timer(250, TimeUnit.MILLISECONDS, scheduler);

		Subscription subscription = values
		    .skipUntil(cutoff)
		    .subscribe(tester);
		
		scheduler.advanceTimeBy(550, TimeUnit.MILLISECONDS);
		
		tester.assertReceivedOnNext(Arrays.asList(2L,3L,4L));
		tester.assertNoErrors();
	}

}
