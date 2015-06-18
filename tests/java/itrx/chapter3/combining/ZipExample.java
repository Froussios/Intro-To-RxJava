package itrx.chapter3.combining;

import java.util.Arrays;
import java.util.concurrent.TimeUnit;

import org.junit.Test;

import rx.Observable;
import rx.observers.TestSubscriber;
import rx.schedulers.Schedulers;
import rx.schedulers.TestScheduler;

public class ZipExample {
	
	public void example() {
		Observable.zip(
		        Observable.interval(100, TimeUnit.MILLISECONDS)
		            .doOnNext(i -> System.out.println("Left emits " + i)),
		        Observable.interval(150, TimeUnit.MILLISECONDS)
		            .doOnNext(i -> System.out.println("Right emits " + i)),
		        (i1,i2) -> i1 + " - " + i2
		    )
		    .take(6)
		    .subscribe(System.out::println);
		
		// Left emits
		// Right emits
		// 0 - 0
		// Left emits
		// Right emits
		// Left emits
		// 1 - 1
		// Left emits
		// Right emits
		// 2 - 2
		// Left emits
		// Left emits
		// Right emits
		// 3 - 3
		// Left emits
		// Right emits
		// 4 - 4
		// Left emits
		// Right emits
		// Left emits
		// 5 - 5
	}
	
	public void exampleZipMultiple() {
		Observable.zip(
				Observable.interval(100, TimeUnit.MILLISECONDS),
				Observable.interval(150, TimeUnit.MILLISECONDS),
				Observable.interval(050, TimeUnit.MILLISECONDS),
				(i1,i2,i3) -> i1 + " - " + i2 + " - " + i3)
			.take(6)
			.subscribe(System.out::println);
		
		// 0 - 0 - 0
		// 1 - 1 - 1
		// 2 - 2 - 2
		// 3 - 3 - 3
		// 4 - 4 - 4
		// 5 - 5 - 5
	}
	
	public void exampleZipUneven() {
		Observable.zip(
				Observable.range(0, 5),
				Observable.range(0, 3),
				Observable.range(0, 8),
				(i1,i2,i3) -> i1 + " - " + i2 + " - " + i3)
			.count()
			.subscribe(System.out::println);
		
		// 3
	}
	
	public void exampleZipWith() {
		Observable.interval(100, TimeUnit.MILLISECONDS)
			.zipWith(
				Observable.interval(150, TimeUnit.MILLISECONDS), 
				(i1,i2) -> i1 + " - " + i2)
			.take(6)
			.subscribe(System.out::println);
		
		// 0 - 0
		// 1 - 1
		// 2 - 2
		// 3 - 3
		// 4 - 4
		// 5 - 5
	}
	
	public void exampleZipWithIterable() {
		Observable.range(0, 5)
			.zipWith(
				Arrays.asList(0,2,4,6,8),
				(i1,i2) -> i1 + " - " + i2)
			.subscribe(System.out::println);
		
		// 0 - 0
		// 1 - 2
		// 2 - 4
		// 3 - 6
		// 4 - 8
	}
	
	
	//
	// Test
	//
	
	@Test
	public void test() {
		TestScheduler scheduler = Schedulers.test();
		TestSubscriber<String> tester = new TestSubscriber<>();
		
		Observable.zip(
		        Observable.interval(100, TimeUnit.MILLISECONDS, scheduler),
		        Observable.interval(150, TimeUnit.MILLISECONDS, scheduler),
		        (i1,i2) -> i1 + " - " + i2
		    )
		    .subscribe(tester);
		
		scheduler.advanceTimeBy(600, TimeUnit.MILLISECONDS);
		tester.assertReceivedOnNext(Arrays.asList(
			"0 - 0",
			"1 - 1",
			"2 - 2",
			"3 - 3"
		));
		tester.assertNoErrors();
		
		tester.unsubscribe();
	}
	
	@Test
	public void testZipMultiple() {
		TestSubscriber<String> tester = new TestSubscriber<>();
		TestScheduler scheduler = Schedulers.test();
		
		Observable.zip(
				Observable.interval(100, TimeUnit.MILLISECONDS, scheduler),
				Observable.interval(150, TimeUnit.MILLISECONDS, scheduler),
				Observable.interval(050, TimeUnit.MILLISECONDS, scheduler),
				(i1,i2,i3) -> i1 + " - " + i2 + " - " + i3)
			.subscribe(tester);
		
		scheduler.advanceTimeBy(600, TimeUnit.MILLISECONDS);
		tester.assertReceivedOnNext(Arrays.asList(
			"0 - 0 - 0",
			"1 - 1 - 1",
			"2 - 2 - 2",
			"3 - 3 - 3"
		));
		tester.assertNoErrors();
		
		tester.unsubscribe();
	}
	
	@Test
	public void testZipUneven() {
		TestSubscriber<Integer> tester = new TestSubscriber<>();
		
		Observable.zip(
				Observable.range(0, 5),
				Observable.range(0, 3),
				Observable.range(0, 8),
				(i1,i2,i3) -> i1 + " - " + i2 + " - " + i3)
			.count()
			.subscribe(tester);
		
		tester.assertReceivedOnNext(Arrays.asList(3));
		
		tester.unsubscribe();
	}
	
	@Test
	public void testZipWith() {
		TestSubscriber<String> tester = new TestSubscriber<>();
		TestScheduler scheduler = Schedulers.test();
		
		Observable.interval(100, TimeUnit.MILLISECONDS, scheduler)
			.zipWith(
				Observable.interval(150, TimeUnit.MILLISECONDS, scheduler), 
				(i1,i2) -> i1 + " - " + i2)
			.subscribe(tester);
		
		scheduler.advanceTimeBy(600, TimeUnit.MILLISECONDS);
		tester.assertReceivedOnNext(Arrays.asList(
			"0 - 0",
			"1 - 1",
			"2 - 2",
			"3 - 3"
		));
		tester.assertNoErrors();
		
		tester.unsubscribe();
	}
	
	@Test
	public void testZipWithIterable() {
		TestSubscriber<String> tester = new TestSubscriber<>();
		
		Observable.range(0, 5)
			.zipWith(
				Arrays.asList(0,2,4,6,8),
				(i1,i2) -> i1 + " - " + i2)
			.subscribe(tester);
		
		tester.assertReceivedOnNext(Arrays.asList(
			"0 - 0",
			"1 - 2",
			"2 - 4",
			"3 - 6",
			"4 - 8"
		));
		tester.assertTerminalEvent();
		tester.assertNoErrors();
		
		tester.unsubscribe();
	}

}
