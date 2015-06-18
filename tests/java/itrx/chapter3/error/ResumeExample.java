package itrx.chapter3.error;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;

import java.util.Arrays;

import org.junit.Test;

import rx.Observable;
import rx.Subscriber;
import rx.observers.TestSubscriber;

public class ResumeExample {
	
	private static class PrintSubscriber extends Subscriber<Object>{
	    private final String name;
	    public PrintSubscriber(String name) {
	        this.name = name;
	    }
	    @Override
	    public void onCompleted() {
	        System.out.println(name + ": Completed");
	    }
	    @Override
	    public void onError(Throwable e) {
	        System.out.println(name + ": Error: " + e);
	    }
	    @Override
	    public void onNext(Object v) {
	        System.out.println(name + ": " + v);
	    }
	}
	
	public void exampleOnErrorReturn() {
		Observable<String> values = Observable.create(o -> {
		    o.onNext("Rx");
		    o.onNext("is");
		    o.onError(new Exception("adjective unknown"));
		});

		values
		    .onErrorReturn(e -> "Error: " + e.getMessage())
		    .subscribe(v -> System.out.println(v));
		
		// Rx
		// is
		// Error: adjective unknown
	}
	
	public void exampleOnErrorResumeNext() {
		Observable<Integer> values = Observable.create(o -> {
		    o.onNext(1);
		    o.onNext(2);
		    o.onError(new Exception("Oops"));
		});

		values
		    .onErrorResumeNext(Observable.just(Integer.MAX_VALUE))
		    .subscribe(new PrintSubscriber("with onError: "));
		
		// with onError: 1
		// with onError: 2
		// with onError: 2147483647
		// with onError: Completed
	}
	
	public void exampleOnErrorResumeNextRethrow() {
		Observable<Integer> values = Observable.create(o -> {
		    o.onNext(1);
		    o.onNext(2);
		    o.onError(new Exception("Oops"));
		});

		values
			.onErrorResumeNext(e -> Observable.error(new UnsupportedOperationException(e)))
		    .subscribe(new PrintSubscriber("with onError: "));
		
		// with onError: : 1
		// with onError: : 2
		// with onError: : Error: java.lang.UnsupportedOperationException: java.lang.Exception: Oops
	}
	
	public void exampleOnExceptionResumeNext() {
		Observable<String> values = Observable.create(o -> {
		    o.onNext("Rx");
		    o.onNext("is");
		    o.onError(new Exception()); // this will be caught
		});

		values
		    .onExceptionResumeNext(Observable.just("hard"))
		    .subscribe(v -> System.out.println(v));
		
		// Rx
		// is
		// hard
	}
	
	@SuppressWarnings("serial")
	public void exampleOnExceptionResumeNextNoException() {
		Observable<String> values = Observable.create(o -> {
		    o.onNext("Rx");
		    o.onNext("is");
		    o.onError(new Throwable() {}); // this won't be caught
		});

		values
		    .onExceptionResumeNext(Observable.just("hard"))
		    .subscribe(v -> System.out.println(v));
		
		// Rx
		// is
		// uncaught exception
	}
	
	
	//
	// Tests
	//
	
	@Test
	public void testOnErrorReturn() {
		TestSubscriber<String> tester = new TestSubscriber<>();
		
		Observable<String> values = Observable.create(o -> {
		    o.onNext("Rx");
		    o.onNext("is");
		    o.onError(new Exception("adjective unknown"));
		});

		values
		    .onErrorReturn(e -> "Error: " + e.getMessage())
		    .subscribe(tester);
		
		tester.assertReceivedOnNext(Arrays.asList(
				"Rx",
				"is",
				"Error: adjective unknown"));
		tester.assertTerminalEvent();
		tester.assertNoErrors();
	}
	
	@Test
	public void testerOnErrorResumeNext() {
		TestSubscriber<Integer> tester = new TestSubscriber<>();
		
		Observable<Integer> values = Observable.create(o -> {
		    o.onNext(1);
		    o.onNext(2);
		    o.onError(new Exception("Oops"));
		});

		values
		    .onErrorResumeNext(Observable.just(Integer.MAX_VALUE))
		    .subscribe(tester);
		
		tester.assertReceivedOnNext(Arrays.asList(1,2,Integer.MAX_VALUE));
		tester.assertTerminalEvent();
		tester.assertNoErrors();
	}
	
	@Test
	public void testOnErrorResumeNextRethrow() {
		TestSubscriber<Integer> tester = new TestSubscriber<>();
		
		Observable<Integer> values = Observable.create(o -> {
		    o.onNext(1);
		    o.onNext(2);
		    o.onError(new Exception("Oops"));
		});

		values
			.onErrorResumeNext(e -> Observable.error(new UnsupportedOperationException(e)))
		    .subscribe(tester);
		
		tester.assertReceivedOnNext(Arrays.asList(1,2));
		tester.assertTerminalEvent();
		assertThat(tester.getOnErrorEvents().get(0),
				org.hamcrest.CoreMatchers.instanceOf(UnsupportedOperationException.class));
	}
	
	@Test
	public void testOnExceptionResumeNext() {
		TestSubscriber<String> tester = new TestSubscriber<>();
		
		Observable<String> values = Observable.create(o -> {
		    o.onNext("Rx");
		    o.onNext("is");
		    o.onError(new Exception()); // this will be caught
		});

		values
		    .onExceptionResumeNext(Observable.just("hard"))
		    .subscribe(tester);
		
		tester.assertReceivedOnNext(Arrays.asList("Rx","is","hard"));
		tester.assertTerminalEvent();
		tester.assertNoErrors();
	}
	
	@SuppressWarnings("serial")
	@Test
	public void testOnExceptionResumeNextNoException() {
		TestSubscriber<String> tester = new TestSubscriber<>();
		
		Observable<String> values = Observable.create(o -> {
		    o.onNext("Rx");
		    o.onNext("is");
		    o.onError(new Throwable() {}); // this won't be caught
		});

		values
		    .onExceptionResumeNext(Observable.just("hard"))
		    .subscribe(tester);
		
		tester.assertTerminalEvent();
		assertEquals(tester.getOnErrorEvents().size(), 1);
	}

}
