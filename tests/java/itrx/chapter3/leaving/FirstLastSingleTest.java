package itrx.chapter3.leaving;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.junit.Test;

import rx.Observable;

public class FirstLastSingleTest {

	public void exampleFirst() {
		Observable<Long> values = Observable.interval(100, TimeUnit.MILLISECONDS);

		long value = values
		    .take(5)
		    .toBlocking()
		    .first(i -> i>2);
		System.out.println(value);
		
		// 3
	}
	
	public void exampleSingleError() {
		Observable<Long> values = Observable.interval(100, TimeUnit.MILLISECONDS);

		try {
		    long value = values
		        .take(5)
		        .toBlocking()
		        .single(i -> i>2);
		    System.out.println(value);
		}
		catch (Exception e) {
		    System.out.println("Caught: " + e);
		}
		
		// Caught: java.lang.IllegalArgumentException: Sequence contains too many elements
	}
	
	
	//
	// Tests
	//
	
	@Test
	public void testFirst() throws InterruptedException {
		List<Integer> received = new ArrayList<>();
		
		Observable<Integer> values = Observable.range(0,5);

		int value = values
		    .take(5)
		    .toBlocking()
		    .first(i -> i>2);
		received.add(value);

		assertEquals(received, Arrays.asList(3));
	}
	
	@Test(expected = IllegalArgumentException.class)
	public void testSingleError() {
		Observable<Integer> values = Observable.range(0, 5);

	    long value = values
	        .take(5)
	        .toBlocking()
	        .single(i -> i>2);
	    System.out.println(value);
	}

}
