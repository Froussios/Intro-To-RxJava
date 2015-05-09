package itrx.chapter3.leaving;


import static org.junit.Assert.*;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import org.junit.Test;

import rx.Observable;

public class FutureTest {

	public void exampleFuture() {
		Observable<Long> values = Observable.timer(500, TimeUnit.MILLISECONDS);

		values.subscribe(v -> System.out.println("Emitted: " + v));

		Future<Long> future = values.toBlocking().toFuture();
		try {
			System.out.println(future.get());
		} catch (InterruptedException e) {
			e.printStackTrace();
		} catch (ExecutionException e) {
			e.printStackTrace();
		}
		
//		Emitted: 0
//		0
	}
	
	
	//
	//
	
	@Test
	public void testFuture() throws InterruptedException, ExecutionException {
		Observable<Integer> sequence = Observable.just(0);
		Future<Integer> future = sequence.toBlocking().toFuture();
		int value = future.get();
		assertEquals(0, value);
	}

}
