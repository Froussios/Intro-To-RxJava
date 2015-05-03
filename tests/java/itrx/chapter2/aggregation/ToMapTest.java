package itrx.chapter2.aggregation;

import static org.junit.Assert.*;

import java.util.*;

import org.junit.Test;

import rx.Observable;
import rx.Subscriber;
import rx.Subscription;
import rx.observers.TestSubscriber;
import rx.schedulers.Schedulers;
import rx.schedulers.TestScheduler;
import rx.subjects.ReplaySubject;
import rx.subjects.Subject;

public class ToMapTest {
	
	private class PrintSubscriber extends Subscriber<Object>{
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

	private static class Person {
	    public final String name;
	    public final Integer age;
	    public Person(String name, int age) {
	        this.name = name;
	        this.age = age;
	    }
	    
	    @Override
	    public boolean equals(Object obj) {
	    	if (obj instanceof Person) {
	    		Person o = (Person) obj;
	    		return this.name == o.name &&
	    				this.age == o.age;
	    	}
	    	return false;
	    }
	}
	
	
	public void  exampleToMap() {
		Observable<Person> values = Observable.just(
			    new Person("Will", 25),
			    new Person("Nick", 40),
			    new Person("Saul", 35)
			);

		values
		    .toMap(person -> person.name)
		    .subscribe(new PrintSubscriber("toMap"));
			
//		toMap: {Saul=Person@7cd84586, Nick=Person@30dae81, Will=Person@1b2c6ec2}
//		toMap: Completed
	}
	
	public void exampleToMapWithSelector() {
		Observable<Person> values = Observable.just(
			    new Person("Will", 25),
			    new Person("Nick", 40),
			    new Person("Saul", 35)
			);

		values
		    .toMap(
		        person -> person.name,
		        person -> person.age)
		    .subscribe(new PrintSubscriber("toMap"));
		
//		toMap: {Saul=35, Nick=40, Will=25}
//		toMap: Completed
	}
	
	public void exampleToMapWithCustomContainer() {
		Observable<Person> values = Observable.just(
			    new Person("Will", 25),
			    new Person("Nick", 40),
			    new Person("Saul", 35)
			);

		values
		    .toMap(
		        person -> person.name,
		        person -> person.age,
		        () -> new HashMap<String, Integer>())
		    .subscribe(new PrintSubscriber("toMap"));
		
//		toMap: {Saul=35, Nick=40, Will=25}
//		toMap: Completed
	}
	
	public void exampleToMultimap() {
		Observable<Person> values = Observable.just(
			    new Person("Will", 35),
			    new Person("Nick", 40),
			    new Person("Saul", 35)
			);

		values
		    .toMultimap(
		        person -> person.age,
		        person -> person.name)
		    .subscribe(new PrintSubscriber("toMap"));
		
//		toMap: {35=[Will, Saul], 40=[Nick]}
//		toMap: Completed
	}
	
	public void exampleToMultimapWithCustomContainers() {
		Observable<Person> values = Observable.just(
			    new Person("Will", 35),
			    new Person("Nick", 40),
			    new Person("Saul", 35)
			);

		values
		    .toMultimap(
		        person -> person.age,
		        person -> person.name,
		        () -> new HashMap(),
		        (key) -> new ArrayList())
		    .subscribe(new PrintSubscriber("toMap"));
		
//		toMap: {35=[Will, Saul], 40=[Nick]}
//		toMap: Completed
	}
	
	
	//
	// Tests
	//
	
	@Test
	public void  testToMap() {
		TestSubscriber<Map<String, Person>> tester = new TestSubscriber<>();
		
		Person will = new Person("Will", 25);
		Person nick = new Person("Nick", 40);
	    Person saul = new Person("Saul", 35);
		
		Observable<Person> values = Observable.just(
			   will, nick, saul
			);

		values
		    .toMap(person -> person.name)
		    .subscribe(tester);
		
		assertEquals(tester.getOnNextEvents(), Arrays.asList(new HashMap<String, Person>() {{
				this.put(will.name, will);
				this.put(nick.name, nick);
				this.put(saul.name, saul);
		}}));
		tester.assertTerminalEvent();
		tester.assertNoErrors();
		
//		toMap: {Saul=Person@7cd84586, Nick=Person@30dae81, Will=Person@1b2c6ec2}
//		toMap: Completed
	}
	
	@Test
	public void testToMapWithSelector() {
		TestSubscriber<Map<String, Integer>> tester = new TestSubscriber<>();
		
		Person will = new Person("Will", 25);
		Person nick = new Person("Nick", 40);
	    Person saul = new Person("Saul", 35);
		
		Observable<Person> values = Observable.just(
			   will, nick, saul
			);

		values
		    .toMap(
		        person -> person.name,
		        person -> person.age)
		    .subscribe(tester);
		
		assertEquals(tester.getOnNextEvents(), Arrays.asList(new HashMap<String, Integer>() {{
			this.put(will.name, will.age);
			this.put(nick.name, nick.age);
			this.put(saul.name, saul.age);
		}}));
		tester.assertTerminalEvent();
		tester.assertNoErrors();
	}
	
	@Test
	public void testToMapWithCustomContainer() {
		TestSubscriber<Map<String, Integer>> tester = new TestSubscriber<>();
		
		Person will = new Person("Will", 25);
		Person nick = new Person("Nick", 40);
	    Person saul = new Person("Saul", 35);
		
		Observable<Person> values = Observable.just(
			   will, nick, saul
			);

		values
		    .toMap(
		        person -> person.name,
		        person -> person.age,
		        () -> new HashMap<String, Integer>())
		    .subscribe(tester);
		
		assertEquals(tester.getOnNextEvents(), Arrays.asList(new HashMap<String, Integer>() {{
			this.put(will.name, will.age);
			this.put(nick.name, nick.age);
			this.put(saul.name, saul.age);
		}}));
		tester.assertTerminalEvent();
		tester.assertNoErrors();
	}
	
	@Test
	public void testToMultimap() {
		TestSubscriber<Map<Integer, Collection<String>>> tester = new TestSubscriber<>();
		
		Person will = new Person("Will", 35);
		Person nick = new Person("Nick", 40);
	    Person saul = new Person("Saul", 35);
		
		Observable<Person> values = Observable.just(
			   will, nick, saul
			);

		values
		    .toMultimap(
		        person -> person.age,
		        person -> person.name)
		    .subscribe(tester);
		
		assertEquals(tester.getOnNextEvents(), Arrays.asList(new HashMap<Integer, Collection<String>>() {{
			this.put(35, Arrays.asList(will.name, saul.name));
			this.put(40, Arrays.asList(nick.name));
		}}));
		tester.assertTerminalEvent();
		tester.assertNoErrors();
	}
	
	@Test
	public void testToMultimapWithCustomContainers() {
		TestSubscriber<Map<Integer, Collection<String>>> tester = new TestSubscriber<>();
		
		Person will = new Person("Will", 35);
		Person nick = new Person("Nick", 40);
	    Person saul = new Person("Saul", 35);
		
		Observable<Person> values = Observable.just(
			   will, nick, saul
			);

		values
		    .toMultimap(
		        person -> person.age,
		        person -> person.name,
		        () -> new HashMap(),
		        (key) -> new ArrayList())
		    .subscribe(tester);
		
		assertEquals(tester.getOnNextEvents(), Arrays.asList(new HashMap<Integer, Collection<String>>() {{
			this.put(35, Arrays.asList(will.name, saul.name));
			this.put(40, Arrays.asList(nick.name));
		}}));
		tester.assertTerminalEvent();
		tester.assertNoErrors();
	}
	
}


