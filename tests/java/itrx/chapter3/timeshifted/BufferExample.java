package itrx.chapter3.timeshifted;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.junit.Test;

import rx.Observable;
import rx.observers.TestSubscriber;
import rx.schedulers.Schedulers;
import rx.schedulers.TestScheduler;

public class BufferExample {

	public void exampleByCount() {
		Observable.range(0, 10)
		    .buffer(4)
		    .subscribe(System.out::println);
		
		// [0, 1, 2, 3]
		// [4, 5, 6, 7]
		// [8, 9]
	}
	
	public void exampleByTime() {
		Observable.interval(100, TimeUnit.MILLISECONDS).take(10)
		    .buffer(250, TimeUnit.MILLISECONDS)
		    .subscribe(System.out::println);
		
		// [0, 1]
		// [2, 3]
		// [4, 5, 6]
		// [7, 8]
		// [9]
	}
	
	public void exampleByCountAndTime() {
		Observable.interval(100, TimeUnit.MILLISECONDS)
		    .take(10)
		    .buffer(250, TimeUnit.MILLISECONDS, 2)
		    .subscribe(System.out::println);
		
		// [0, 1]
		// []
		// [2, 3]
		// []
		// [4, 5]
		// [6]
		// [7, 8]
		// []
		// [9]
	}
	
	public void exampleWithSignal() {
		Observable.interval(100, TimeUnit.MILLISECONDS).take(10)
		    .buffer(Observable.interval(250, TimeUnit.MILLISECONDS))
		    .subscribe(System.out::println);
		
		// [0, 1]
		// [2, 3]
		// [4, 5, 6]
		// [7, 8]
		// [9]
	}
	
	public void exampleOverlappingByCount() {
		Observable.range(0,10)
		    .buffer(4, 3)
		    .subscribe(System.out::println);
		
		// [0, 1, 2, 3]
		// [3, 4, 5, 6]
		// [6, 7, 8, 9]
		// [9]
	}
	
	public void exampleOverlappingByTime() {
		Observable.interval(100, TimeUnit.MILLISECONDS).take(10)
		    .buffer(350, 200, TimeUnit.MILLISECONDS)
		    .subscribe(System.out::println);
		
		// [0, 1, 2]
		// [2, 3, 4]
		// [3, 4, 5, 6]
		// [5, 6, 7, 8]
		// [7, 8, 9]
		// [9]
	}
	
	public void exampleOverlappingBySignal() {
		Observable.interval(100, TimeUnit.MILLISECONDS).take(10)
		    .buffer(
		        Observable.interval(250, TimeUnit.MILLISECONDS),
		        i -> Observable.timer(200, TimeUnit.MILLISECONDS))
		    .subscribe(System.out::println);
		
		// [2, 3]
		// [4, 5]
		// [7, 8]
		// [9]
	}
	
	
	//
	// Tests
	//
	
	@Test
	public void testByCount() {
		TestSubscriber<List<Integer>> tester = new TestSubscriber<>();
		
		Observable.range(0, 10)
		    .buffer(4)
		    .subscribe(tester);
		
		tester.assertReceivedOnNext(Arrays.asList(
			Arrays.asList(0, 1, 2, 3),
			Arrays.asList(4, 5, 6, 7),
			Arrays.asList(8, 9)
		));
		tester.assertTerminalEvent();
		tester.assertNoErrors();
	}
	
	@Test
	public void testByTime() {
		TestSubscriber<List<Long>> tester = new TestSubscriber<>();
		TestScheduler scheduler = Schedulers.test();
		
		Observable.interval(100, TimeUnit.MILLISECONDS, scheduler).take(10)
		    .buffer(250, TimeUnit.MILLISECONDS, scheduler)
		    .subscribe(tester);
		
		scheduler.advanceTimeBy(1000, TimeUnit.MILLISECONDS);
		
		tester.assertReceivedOnNext(Arrays.asList(
			Arrays.asList(0L, 1L),
			Arrays.asList(2L, 3L),
			Arrays.asList(4L, 5L, 6L),
			Arrays.asList(7L, 8L),
			Arrays.asList(9L)
		));
		tester.assertTerminalEvent();
		tester.assertNoErrors();
	}
	
	@Test
	public void testByCountAndTime() {
		TestSubscriber<List<Long>> tester = new TestSubscriber<>();
		TestScheduler scheduler = Schedulers.test();
		
		Observable.interval(100, TimeUnit.MILLISECONDS, scheduler)
		    .take(10)
		    .buffer(250, TimeUnit.MILLISECONDS, 2, scheduler)
		    .subscribe(tester);
		
		scheduler.advanceTimeBy(1000, TimeUnit.MILLISECONDS);
		
		tester.assertReceivedOnNext(Arrays.asList(
			Arrays.asList(0L, 1L),
			Arrays.asList(),
			Arrays.asList(2L, 3L),
			Arrays.asList(),
			Arrays.asList(4L, 5L),
			Arrays.asList(6L),
			Arrays.asList(7L, 8L),
			Arrays.asList(),
			Arrays.asList(9L)
		));
		tester.assertTerminalEvent();
		tester.assertNoErrors();
	}
	
	@Test
	public void testWithSignal() {
		TestSubscriber<List<Long>> tester = new TestSubscriber<>();
		TestScheduler scheduler = Schedulers.test();
		
		Observable.interval(100, TimeUnit.MILLISECONDS, scheduler).take(10)
		    .buffer(Observable.interval(250, TimeUnit.MILLISECONDS, scheduler))
		    .subscribe(tester);
		
		scheduler.advanceTimeBy(1000, TimeUnit.MILLISECONDS);
		
		tester.assertReceivedOnNext(Arrays.asList(
			Arrays.asList(0L, 1L),
			Arrays.asList(2L, 3L),
			Arrays.asList(4L, 5L, 6L),
			Arrays.asList(7L, 8L),
			Arrays.asList(9L)
		));
		tester.assertTerminalEvent();
		tester.assertNoErrors();
	}
	
	@Test
	public void testOverlappingByCount() {
		TestSubscriber<List<Integer>> tester = new TestSubscriber<>();
		
		Observable.range(0,10)
		    .buffer(4, 3)
		    .subscribe(tester);
		
		tester.assertReceivedOnNext(Arrays.asList(
			Arrays.asList(0, 1, 2, 3),
			Arrays.asList(3, 4, 5, 6),
			Arrays.asList(6, 7, 8, 9),
			Arrays.asList(9)
		));
		tester.assertTerminalEvent();
		tester.assertNoErrors();
	}
	
	@Test
	public void testOverlappingByTime() {
		TestSubscriber<List<Long>> tester = new TestSubscriber<>();
		TestScheduler scheduler = Schedulers.test();
		
		Observable.interval(100, TimeUnit.MILLISECONDS, scheduler).take(10)
		    .buffer(350, 200, TimeUnit.MILLISECONDS, scheduler)
		    .subscribe(tester);
		
		scheduler.advanceTimeBy(1000, TimeUnit.MILLISECONDS);
		
		tester.assertReceivedOnNext(Arrays.asList(
			Arrays.asList(0L, 1L, 2L),
			Arrays.asList(1L, 2L, 3L, 4L),
			Arrays.asList(3L, 4L, 5L, 6L),
			Arrays.asList(5L, 6L, 7L, 8L),
			Arrays.asList(7L, 8L, 9L),
			Arrays.asList(9L)
		));
		tester.assertTerminalEvent();
		tester.assertNoErrors();
	}
	
	@Test
	public void testOverlappingBySignal() {
		TestSubscriber<List<Long>> tester = new TestSubscriber<>();
		TestScheduler scheduler = Schedulers.test();
		
		Observable.interval(100, TimeUnit.MILLISECONDS, scheduler).take(10)
		    .buffer(
		        Observable.interval(250, TimeUnit.MILLISECONDS, scheduler),
		        i -> Observable.timer(200, TimeUnit.MILLISECONDS, scheduler))
		    .subscribe(tester);
		
		scheduler.advanceTimeBy(1000, TimeUnit.MILLISECONDS);
		
		tester.assertReceivedOnNext(Arrays.asList(
			Arrays.asList(2L, 3L),
			Arrays.asList(4L, 5L),
			Arrays.asList(7L, 8L),
			Arrays.asList(9L)
		));
		tester.assertTerminalEvent();
		tester.assertNoErrors();
	}

}
