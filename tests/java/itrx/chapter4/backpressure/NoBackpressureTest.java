package itrx.chapter4.backpressure;

import static org.hamcrest.CoreMatchers.instanceOf;
import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.junit.Test;

import rx.Observable;
import rx.observers.TestSubscriber;
import rx.schedulers.Schedulers;
import rx.schedulers.TestScheduler;

public class NoBackpressureTest {

	public void exampleSynchronous() {
		// Produce
		Observable<Integer> producer = Observable.create(o -> {
		    o.onNext(1);
		    o.onNext(2);
		    o.onCompleted();
		});
		// Consume
		producer.subscribe(i -> {
		    try {
		        Thread.sleep(1000);
		        System.out.println(i);
		    } catch (Exception e) { }
		});
		
		// 1
		// 2
	}
	
	public void exampleNoBackpressure() {
		Observable.interval(1, TimeUnit.MILLISECONDS)
		    .observeOn(Schedulers.newThread())
		    .subscribe(
		        i -> {
		            System.out.println(i);
		            try {
		                Thread.sleep(100);
		            } catch (Exception e) { }
		        },
		        System.out::println);
		
		// 0
		// 1
		// rx.exceptions.MissingBackpressureException
	}
	
	
	//
	// Tests
	//
	
	@Test
	public void testSynchronous() {
		List<String> execution = new ArrayList<String>();
		
		// Produce
		Observable<Integer> producer = Observable.create(o -> {
			execution.add("Producing 1");
		    o.onNext(1);
		    execution.add("Producing 2");
		    o.onNext(2);
		    o.onCompleted();
		});
		// Consume
		producer.subscribe(i -> execution.add("Processed " + i));
		
		assertEquals(
			Arrays.asList(
				"Producing 1",
				"Processed 1",
				"Producing 2",
				"Processed 2"
			),
			execution);
	}
	
	@Test
	public void testNoBackpressure() {
		TestScheduler scheduler = Schedulers.test();
		TestSubscriber<Long> tester = new TestSubscriber<Long>() {
			@Override
			public void onNext(Long t) {
				scheduler.advanceTimeBy(100, TimeUnit.MILLISECONDS);
				super.onNext(t);
			}
		};
		
		Observable.interval(1, TimeUnit.MILLISECONDS, scheduler)
		    .observeOn(scheduler)
		    .subscribe(tester);
		
		scheduler.advanceTimeBy(10, TimeUnit.MILLISECONDS);
		assertThat(
				tester.getOnErrorEvents().get(0),
				instanceOf(rx.exceptions.MissingBackpressureException.class));
		
	}

}
