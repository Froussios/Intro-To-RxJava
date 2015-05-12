package itrx.chapter3.error;

import static org.junit.Assert.assertEquals;

import java.util.Arrays;

import org.junit.Test;

import rx.Observable;
import rx.observers.TestSubscriber;

public class UsingTest {

	public void exampleUsing() {
		Observable<Character> values = Observable.using(
		    () -> {
		        String resource = "MyResource";
		        System.out.println("Leased: " + resource);
		        return resource;
		    },
		    (resource) -> {
		        return Observable.create(o -> {
		            for (Character c : resource.toCharArray())
		                o.onNext(c);
		            o.onCompleted();
		        });
		    },
		    (resource) -> System.out.println("Disposed: " + resource));

		values
		    .subscribe(
		        v -> System.out.println(v),
		        e -> System.out.println(e));
		
		// Leased: MyResource
		// M
		// y
		// R
		// e
		// s
		// o
		// u
		// r
		// c
		// e
		// Disposed: MyResource
	}
	
	
	//
	// Test
	//
	
	@Test
	public void testUsing() {
		TestSubscriber<Character> tester = new TestSubscriber<>();
		String[] leaseRelease = {"", ""};
		
		Observable<Character> values = Observable.using(
		    () -> {
		        String resource = "MyResource";
		        leaseRelease[0] = resource;
		        return resource;
		    },
		    (resource) -> {
		        return Observable.create(o -> {
		            for (Character c : resource.toCharArray())
		                o.onNext(c);
		            o.onCompleted();
		        });
		    },
		    (resource) -> leaseRelease[1] = resource);

		values
		    .subscribe(tester);
		
		assertEquals(leaseRelease[0], leaseRelease[1]);
		tester.assertReceivedOnNext(Arrays.asList('M','y','R','e','s','o','u','r','c','e'));
		tester.assertTerminalEvent();
		tester.assertNoErrors();
	}

}
