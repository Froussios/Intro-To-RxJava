package itrx.chapter4.coincidence;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.junit.Test;

import rx.Observable;
import rx.observers.TestSubscriber;
import rx.schedulers.Schedulers;
import rx.schedulers.TestScheduler;

public class WindowTest {
	
	public static void main(String[] args) throws InterruptedException {
		new WindowTest().exampleBySignal();
		Thread.sleep(60000);
	}
	
	public void exampleParallel() {
		Observable
		    .merge(
		        Observable.range(0, 5)
		            .window(3,1))
		    .subscribe(System.out::println);
		
//		0
//		1
//		1
//		2
//		2
//		2
//		3
//		3
//		3
//		4
//		4
//		4
	}
	
	public void exampleByCount() {
		Observable.range(0, 5)
			.window(3, 1)
			.flatMap(o -> o.toList())
			.subscribe(System.out::println);
		
//		[0, 1, 2]
//		[1, 2, 3]
//		[2, 3, 4]
//		[3, 4]
//		[4]

	}
	
	public void exampleByTime() {
		Observable.interval(100, TimeUnit.MILLISECONDS)
			.take(5)
			.window(250, 100, TimeUnit.MILLISECONDS)
			.flatMap(o -> o.toList())
			.subscribe(System.out::println);
		
//		[0, 1]
//		[0, 1, 2]
//		[1, 2, 3]
//		[2, 3, 4]
//		[3, 4]
//		[4]
	}
	
	public void exampleBySignal() {
		Observable.interval(100, TimeUnit.MILLISECONDS)
			.take(5)
			.window(
				Observable.interval(100, TimeUnit.MILLISECONDS),
				o -> Observable.timer(250, TimeUnit.MILLISECONDS))
			.flatMap(o -> o.toList())
			.subscribe(System.out::println);
		
//		[1, 2]
//		[2, 3]
//		[3, 4]
//		[4]
//		[]
	}
	
	
	//
	// Tests
	//
	
	@Test
	public void testParallel() {
		TestSubscriber<Integer> tester = new TestSubscriber<>();
		
		Observable
		    .merge(
		        Observable.range(0, 5)
		            .window(3,1))
		    .subscribe(tester);
		
		tester.assertReceivedOnNext(Arrays.asList(0, 1, 1, 2, 2, 2, 3, 3, 3, 4, 4, 4));
	}
	
	@Test
	public void testByCount() {
		TestSubscriber<List<Integer>> tester = new TestSubscriber<>();
		
		Observable.range(0, 5)
			.window(3, 1)
			.flatMap(o -> o.toList())
			.subscribe(tester);

		tester.assertReceivedOnNext(Arrays.asList(
			Arrays.asList(0, 1, 2),
			Arrays.asList(1, 2, 3),
			Arrays.asList(2, 3, 4),
			Arrays.asList(3, 4),
			Arrays.asList(4)
		));
	}
	
	@Test
	public void testByTime() {
		TestSubscriber<List<Long>> tester = new TestSubscriber<>();
		TestScheduler scheduler = Schedulers.test();
		
		Observable.interval(100, TimeUnit.MILLISECONDS, scheduler)
			.take(5)
			.window(250, 100, TimeUnit.MILLISECONDS, scheduler)
			.flatMap(o -> o.toList())
			.subscribe(tester);
		
		scheduler.advanceTimeTo(500, TimeUnit.MILLISECONDS);
		tester.assertReceivedOnNext(Arrays.asList(
			Arrays.asList(0L, 1L),
			Arrays.asList(0L, 1L, 2L),
			Arrays.asList(1L, 2L, 3L),
			Arrays.asList(2L, 3L, 4L),
			Arrays.asList(3L, 4L),
			Arrays.asList(4L)
		));
	}
	
	@Test
	public void testBySignal() {
		TestSubscriber<List<Long>> tester = new TestSubscriber<>();
		TestScheduler scheduler = Schedulers.test();
		
		Observable.interval(100, TimeUnit.MILLISECONDS, scheduler)
			.take(5)
			.window(
				Observable.interval(100, TimeUnit.MILLISECONDS, scheduler),
				o -> Observable.timer(250, TimeUnit.MILLISECONDS, scheduler))
			.flatMap(o -> o.toList())
			.subscribe(tester);
		
		scheduler.advanceTimeTo(500, TimeUnit.MILLISECONDS);
		tester.assertReceivedOnNext(Arrays.asList(
			Arrays.asList(0L, 1L, 2L),
			Arrays.asList(1L, 2L, 3L),
			Arrays.asList(2L, 3L, 4L),
			Arrays.asList(3L, 4L),
			Arrays.asList(4L)
		));
	}
}
