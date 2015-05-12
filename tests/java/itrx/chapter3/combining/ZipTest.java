package itrx.chapter3.combining;

import java.util.Arrays;
import java.util.concurrent.TimeUnit;

import org.junit.Test;

import rx.Observable;
import rx.observers.TestSubscriber;
import rx.schedulers.Schedulers;
import rx.schedulers.TestScheduler;

public class ZipTest {

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
	}

}
