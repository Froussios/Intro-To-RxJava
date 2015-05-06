package itrx.chapter3.sideeffects;

import java.util.Arrays;

import org.junit.Test;

import rx.Observable;
import rx.observers.TestSubscriber;

public class MutablePipelineTest {

	private static class Data {
	    public int id;
	    public String name;
	    public Data(int id, String name) {
	        this.id = id;
	        this.name = name;
	    }
	}

	public void example() {
		Observable<Data> data = Observable.just(
			    new Data(1, "Microsoft"),
			    new Data(2, "Netflix")
			);

		data.subscribe(d -> d.name = "Garbage");
		data.subscribe(d -> System.out.println(d.id + ": " + d.name));
		
//		1: Garbage
//		2: Garbage
	}
	
	@Test
	public void test() {
		TestSubscriber<String> tester = new TestSubscriber<>();
		
		Observable<Data> data = Observable.just(
			    new Data(1, "Microsoft"),
			    new Data(2, "Netflix")
			);

		data.subscribe(d -> d.name = "Garbage");
		data.map(d -> d.name)
			.subscribe(tester);
		
		tester.assertReceivedOnNext(Arrays.asList("Garbage", "Garbage"));
		tester.assertTerminalEvent();
		tester.assertNoErrors();
	}
}
