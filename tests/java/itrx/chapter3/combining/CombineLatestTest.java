package itrx.chapter3.combining;

import java.util.Arrays;
import java.util.concurrent.TimeUnit;

import org.junit.Test;

import rx.Observable;
import rx.observers.TestSubscriber;
import rx.schedulers.Schedulers;
import rx.schedulers.TestScheduler;

public class CombineLatestTest {

	public void example() {
		Observable.combineLatest(
		        Observable.interval(100, TimeUnit.MILLISECONDS)
		            .doOnNext(i -> System.out.println("Left emits")),
		        Observable.interval(150, TimeUnit.MILLISECONDS)
		            .doOnNext(i -> System.out.println("Right emits")),
		        (i1,i2) -> i1 + " - " + i2
		    )
		    .take(6)
		    .subscribe(System.out::println);
		
//		Left emits
//		Right emits
//		0 - 0
//		Left emits
//		1 - 0
//		Left emits
//		2 - 0
//		Right emits
//		2 - 1
//		Left emits
//		3 - 1
//		Right emits
//		3 - 2
	}
	
	
	//
	// Test
	//
	
	@Test
	public void test() {
		TestScheduler scheduler = Schedulers.test();
		TestSubscriber<String> tester = new TestSubscriber<>();
		
		Observable.combineLatest(
		        Observable.interval(100, TimeUnit.MILLISECONDS, scheduler),
		        Observable.interval(150, TimeUnit.MILLISECONDS, scheduler),
		        (i1,i2) -> i1 + " - " + i2
		    )
		    .subscribe(tester);
		
		scheduler.advanceTimeTo(100, TimeUnit.MILLISECONDS);
		scheduler.advanceTimeTo(150, TimeUnit.MILLISECONDS);
		scheduler.advanceTimeTo(200, TimeUnit.MILLISECONDS);
		scheduler.advanceTimeTo(300, TimeUnit.MILLISECONDS);
		
		tester.assertReceivedOnNext(Arrays.asList(
			"0 - 0",
			"1 - 0",
			"1 - 1",
			"2 - 1"
		));
	}

}
