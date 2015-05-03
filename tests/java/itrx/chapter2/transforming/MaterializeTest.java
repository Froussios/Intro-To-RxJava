package itrx.chapter2.transforming;

import java.util.Arrays;

import org.junit.Test;

import rx.Notification;
import rx.Observable;
import rx.Subscriber;
import rx.observers.TestSubscriber;

public class MaterializeTest {
	
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

	
	public void exampleMaterialize() {
		Observable<Integer> values = Observable.range(0,3);

		values.take(3)
		    .materialize()
		    .subscribe(new PrintSubscriber("Materialize"));
		
//		Materialize: [rx.Notification@a4c802e9 OnNext 0]
//		Materialize: [rx.Notification@a4c802ea OnNext 1]
//		Materialize: [rx.Notification@a4c802eb OnNext 2]
//		Materialize: [rx.Notification@18d48ace OnCompleted]
//		Materialize: Completed
	}
	
	
	//
	// Tests
	//
	
	@Test
	public void testMaterialize() {
		TestSubscriber<Notification<Integer>> tester = new TestSubscriber<>();
		
		Observable<Integer> values = Observable.range(0,3);

		values.take(3)
		    .materialize()
		    .subscribe(tester);
		
		tester.assertReceivedOnNext(Arrays.asList(
				Notification.createOnNext(0),
				Notification.createOnNext(1),
				Notification.createOnNext(2),
				Notification.createOnCompleted()
		));
		tester.assertTerminalEvent();
		tester.assertNoErrors();
	}
	
}
