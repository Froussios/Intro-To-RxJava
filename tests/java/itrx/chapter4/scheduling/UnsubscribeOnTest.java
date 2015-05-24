package itrx.chapter4.scheduling;

import static org.junit.Assert.*;

import java.util.Arrays;

import org.junit.Test;

import rx.Observable;
import rx.schedulers.Schedulers;

public class UnsubscribeOnTest {

	public static void example() {
		Observable<Object> source = Observable.using(
			() -> {
				System.out.println("Subscribed on " + Thread.currentThread().getId());
				return Arrays.asList(1,2);
			},
			(ints) -> {
				System.out.println("Producing on " + Thread.currentThread().getId());
				return Observable.from(ints);
			},
			(ints) -> {
				System.out.println("Unubscribed on " + Thread.currentThread().getId());
			}
		);
		
		source
			.unsubscribeOn(Schedulers.newThread())
			.subscribe(System.out::println);
		
		// Subscribed on 1
		// Producing on 1
		// 1
		// 2
		// Unubscribed on 11
	}
	
	
	//
	// Test
	//
	
	@Test
	public void test() {
		long[] threads = {0, 0, 0};
		
		Observable<Object> source = Observable.using(
				() -> {
					threads[0] = Thread.currentThread().getId();
					return Arrays.asList(1,2);
				},
				(ints) -> {
					threads[1] = Thread.currentThread().getId();
					return Observable.from(ints);
				},
				(ints) -> {
					threads[2] = Thread.currentThread().getId();
				}
			);
			
			source
				.unsubscribeOn(Schedulers.newThread())
				.subscribe();
			
			assertEquals(threads[0], threads[1]);
			assertNotEquals(threads[0], threads[2]);
	}

}
