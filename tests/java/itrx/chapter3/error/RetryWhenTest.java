package itrx.chapter3.error;

import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.util.Arrays;
import java.util.concurrent.TimeUnit;

import org.junit.Test;

import rx.Observable;
import rx.observers.TestSubscriber;
import rx.schedulers.Schedulers;
import rx.schedulers.TestScheduler;

public class RetryWhenTest {
	
	public static void main(String[] args) throws IOException {
		new RetryWhenTest().example();
		System.in.read();
	}
	
	public void example() {
		Observable<Integer> source = Observable.create(o -> {
			o.onNext(1);
			o.onNext(2);
			o.onError(new Exception("Failed"));
		});
		
		source.retryWhen((o) -> o
				.take(2)
				.delay(100, TimeUnit.MILLISECONDS)
				.concatWith(Observable.error(new Exception("Done"))))
			.timeInterval()
			.subscribe(
				System.out::println,
				System.out::println);
		
		// TimeInterval [intervalInMilliseconds=17, value=1]
		// TimeInterval [intervalInMilliseconds=0, value=2]
		// TimeInterval [intervalInMilliseconds=102, value=1]
		// TimeInterval [intervalInMilliseconds=0, value=2]
		// TimeInterval [intervalInMilliseconds=102, value=1]
		// TimeInterval [intervalInMilliseconds=0, value=2]
	}
	
	
	//
	// Test
	//
	
	@Test
	public void test() {
		TestScheduler scheduler = Schedulers.test();
		TestSubscriber<Long> intervals = new TestSubscriber<>();
		
		Observable<Integer> source = Observable.create(o -> {
			o.onNext(1);
			o.onNext(2);
			o.onError(new Exception("Failed"));
		});
		source.retryWhen((o) -> o
				.take(2)
				.delay(100, TimeUnit.MILLISECONDS, scheduler)
				.concatWith(Observable.error(new Exception("Done"))), scheduler)
			.timeInterval(scheduler)
			.map(i -> i.getIntervalInMilliseconds())
			.subscribe(intervals);
		
		scheduler.advanceTimeBy(200, TimeUnit.MILLISECONDS);
		intervals.assertReceivedOnNext(Arrays.asList(0L, 0L, 100L, 0L, 100L, 0L));
		intervals.assertTerminalEvent();
		assertEquals(1, intervals.getOnErrorEvents().size());
	}
}
