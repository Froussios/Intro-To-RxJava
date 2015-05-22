package itrx.chapter3.timeshifted;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.junit.Test;

import rx.Observable;
import rx.observers.TestSubscriber;
import rx.schedulers.Schedulers;
import rx.schedulers.TestScheduler;

public class TakeLastBufferTest {

	public void exampleByCount() {
		Observable.range(0, 5)
			.takeLastBuffer(2)
			.subscribe(System.out::println);
		
		// [3, 4]
	}
	
	public void exampleByTime() {
		Observable.interval(100, TimeUnit.MILLISECONDS)
			.take(5)
			.takeLastBuffer(200, TimeUnit.MILLISECONDS)
			.subscribe(System.out::println);
		
		// [2, 3, 4]
	}
	
	public void exampleByCountAndTime() {
		Observable.interval(100, TimeUnit.MILLISECONDS)
			.take(5)
			.takeLastBuffer(2, 200, TimeUnit.MILLISECONDS)
			.subscribe(System.out::println);
		
		// [3, 4]
	}
	
	
	//
	// Tests
	//
	
	@Test
	public void testByCount() {
		TestSubscriber<List<Integer>> tester = new TestSubscriber<>();
		
		Observable.range(0, 5)
			.takeLastBuffer(2)
			.subscribe(tester);
		
		tester.assertReceivedOnNext(Arrays.asList(
			Arrays.asList(3, 4)
		));
		tester.assertTerminalEvent();
		tester.assertNoErrors();
	}
	
	@Test
	public void testByTime() {
		TestSubscriber<List<Long>> tester = new TestSubscriber<>();
		TestScheduler scheduler = Schedulers.test();
		
		Observable.interval(100, TimeUnit.MILLISECONDS, scheduler)
			.take(5)
			.takeLastBuffer(200, TimeUnit.MILLISECONDS, scheduler)
			.subscribe(tester);
		
		scheduler.advanceTimeBy(500, TimeUnit.MILLISECONDS);
		tester.assertReceivedOnNext(Arrays.asList(
			Arrays.asList(2L, 3L, 4L)
		));
		tester.assertTerminalEvent();
		tester.assertNoErrors();
	}
	
	@Test
	public void testByCountAndTime() {
		TestSubscriber<List<Long>> tester = new TestSubscriber<>();
		TestScheduler scheduler = Schedulers.test();
		
		Observable.interval(100, TimeUnit.MILLISECONDS, scheduler)
			.take(5)
			.takeLastBuffer(2, 200, TimeUnit.MILLISECONDS, scheduler)
			.subscribe(tester);
		
		scheduler.advanceTimeBy(500, TimeUnit.MILLISECONDS);
		tester.assertReceivedOnNext(Arrays.asList(
			Arrays.asList(3L, 4L)
		));
		tester.assertTerminalEvent();
		tester.assertNoErrors();
	}
}
