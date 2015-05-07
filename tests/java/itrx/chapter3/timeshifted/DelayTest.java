package itrx.chapter3.timeshifted;

import java.util.Arrays;
import java.util.concurrent.TimeUnit;

import org.junit.Test;

import rx.Observable;
import rx.observers.TestSubscriber;
import rx.schedulers.Schedulers;
import rx.schedulers.TestScheduler;

public class DelayTest {

	public void exampleDelay() {
		Observable.interval(100, TimeUnit.MILLISECONDS)
		    .delay(i -> Observable.timer(i * 100, TimeUnit.MILLISECONDS))
		    .timeInterval()
		    .take(5)
		    .subscribe(System.out::println);
		
//		TimeInterval [intervalInMilliseconds=152, value=0]
//		TimeInterval [intervalInMilliseconds=173, value=1]
//		TimeInterval [intervalInMilliseconds=199, value=2]
//		TimeInterval [intervalInMilliseconds=201, value=3]
//		TimeInterval [intervalInMilliseconds=199, value=4]
	}
	
	public void exampleDelaySubscription() {
		Observable.interval(100, TimeUnit.MILLISECONDS)
		    .delaySubscription(1000, TimeUnit.MILLISECONDS)
		    .timeInterval()
		    .take(5)
		    .subscribe(System.out::println);
		
//		TimeInterval [intervalInMilliseconds=1114, value=0]
//		TimeInterval [intervalInMilliseconds=92, value=1]
//		TimeInterval [intervalInMilliseconds=101, value=2]
//		TimeInterval [intervalInMilliseconds=100, value=3]
//		TimeInterval [intervalInMilliseconds=99, value=4]
	}
	
	public void exampleDelaySubscriptionWithSignal() {
		Observable.interval(100, TimeUnit.MILLISECONDS)
		    .delaySubscription(() -> Observable.timer(1000, TimeUnit.MILLISECONDS))
		    .timeInterval()
		    .take(5)
		    .subscribe(System.out::println);
		
//		TimeInterval [intervalInMilliseconds=1114, value=0]
//		TimeInterval [intervalInMilliseconds=92, value=1]
//		TimeInterval [intervalInMilliseconds=101, value=2]
//		TimeInterval [intervalInMilliseconds=100, value=3]
//		TimeInterval [intervalInMilliseconds=99, value=4]
	}
	
	
	//
	// Tests
	//
	
	@Test
	public void testDelay() {
		TestScheduler scheduler = Schedulers.test();
		TestSubscriber<Long> tester = new TestSubscriber<>();
		
		Observable.interval(100, TimeUnit.MILLISECONDS, scheduler)
		    .delay(i -> Observable.timer(i * 100, TimeUnit.MILLISECONDS, scheduler))
		    .timeInterval(scheduler)
		    .map(i -> i.getIntervalInMilliseconds())
		    .take(5)
		    .subscribe(tester);
		
		scheduler.advanceTimeBy(1000, TimeUnit.MILLISECONDS);
		tester.assertReceivedOnNext(Arrays.asList(100L, 200L, 200L, 200L, 200L));
		tester.assertTerminalEvent();
		tester.assertNoErrors();
	}
	
	@Test
	public void testDelaySubscription() {
		TestScheduler scheduler = Schedulers.test();
		TestSubscriber<Long> tester = new TestSubscriber<>();
		
		Observable.interval(100, TimeUnit.MILLISECONDS, scheduler)
		    .delaySubscription(1000, TimeUnit.MILLISECONDS, scheduler)
		    .timeInterval(scheduler)
		    .take(5)
		    .map(i -> i.getIntervalInMilliseconds())
		    .subscribe(tester);
		
		scheduler.advanceTimeBy(1500, TimeUnit.MILLISECONDS);
		tester.assertReceivedOnNext(Arrays.asList(1100L, 100L, 100L, 100L, 100L));
		tester.assertTerminalEvent();
		tester.assertNoErrors();
	}
	
	@Test
	public void testDelaySubscriptionWithSignal() {
		TestScheduler scheduler = Schedulers.test();
		TestSubscriber<Long> tester = new TestSubscriber<>();
		
		Observable.interval(100, TimeUnit.MILLISECONDS, scheduler)
		    .delaySubscription(() -> Observable.timer(1000, TimeUnit.MILLISECONDS, scheduler))
		    .timeInterval(scheduler)
		    .take(5)
		    .map(i -> i.getIntervalInMilliseconds())
		    .subscribe(tester);
		
		scheduler.advanceTimeBy(1500, TimeUnit.MILLISECONDS);
		tester.assertReceivedOnNext(Arrays.asList(1100L, 100L, 100L, 100L, 100L));
		tester.assertTerminalEvent();
		tester.assertNoErrors();
	}
}
