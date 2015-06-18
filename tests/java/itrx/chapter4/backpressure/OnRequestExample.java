package itrx.chapter4.backpressure;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.Test;

import rx.Observable;

public class OnRequestExample {
	
	public void exampleOnRequest() {
		Observable.range(0, 3)
			.doOnRequest(i -> System.out.println("Requested " + i))
			.subscribe(System.out::println);
		
		// Requested 9223372036854775807
		// 0
		// 1
		// 2
	}
	
	public void exampleOnRequestZip() {
		Observable.range(0, 300)
			.doOnRequest(i -> System.out.println("Requested " + i))
			.zipWith(
					Observable.range(10, 300),
					(i1, i2) -> i1 + " - " + i2)
			.take(300)
			.subscribe();
		
		// Requested 128
		// Requested 90
		// Requested 90
		// Requested 90
		
	}
	
	public void exampleOnRequestManual() {
		ControlledPullSubscriber<Integer> puller = 
				new ControlledPullSubscriber<Integer>(System.out::println);
		
		Observable.range(0, 3)
			.doOnRequest(i -> System.out.println("Requested " + i))
			.subscribe(puller);
		
		puller.requestMore(2);
		puller.requestMore(1);
		
		// Requested 0
		// Requested 2
		// 0
		// 1
		// Requested 1
		// 2
	}
	
	
	//
	// Tests
	//
	
	@Test
	public void testOnRequest() {
		List<Long> requests = new ArrayList<Long>();
		
		Observable.range(0, 3)
			.doOnRequest(requests::add)
			.subscribe();
		
		assertEquals(Arrays.asList(Long.MAX_VALUE), requests);
	}
	
	@Test
	public void testOnRequestZip() {
		List<Long> requests = new ArrayList<Long>();
		
		Observable.range(0, 300)
			.doOnRequest(requests::add)
			.zipWith(
					Observable.range(10, 300),
					(i1, i2) -> i1 + " - " + i2)
			.take(300)
			.subscribe();
		
		assertTrue("zip makes subsequent requests",
				requests.size() > 1);
		assertEquals("zip uses a buffer of 128", 
				requests.get(0), new Long(128));
	}
	
	@Test
	public void testOnRequestManual() {
		List<Integer> received = new ArrayList<Integer>();
		List<Long> requests = new ArrayList<Long>();
		
		ControlledPullSubscriber<Integer> puller = 
				new ControlledPullSubscriber<Integer>(received::add);
		
		Observable.range(0, 3)
			.doOnRequest(requests::add)
			.subscribe(puller);
		
		assertEquals(Arrays.asList(0L), requests);
		assertEquals(Arrays.asList(), received);
		puller.requestMore(2);
		assertEquals(Arrays.asList(0L, 2L), requests);
		assertEquals(Arrays.asList(0, 1), received);
		puller.requestMore(1);
		assertEquals(Arrays.asList(0L, 2L, 1L), requests);
		assertEquals(Arrays.asList(0, 1, 2), received);
	}

}
