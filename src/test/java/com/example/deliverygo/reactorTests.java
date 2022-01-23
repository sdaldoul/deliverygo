package com.example.deliverygo;

import java.time.Duration;
import java.util.concurrent.Callable;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.stream.IntStream;
import org.junit.jupiter.api.Test;
import reactor.core.publisher.Flux;
import reactor.core.publisher.FluxSink;
import reactor.core.publisher.SynchronousSink;


//Read Me https://www.vinsguru.com/reactor-flux-create-vs-generate/
public class reactorTests {

	@Test
	public void fluxCreateTest() {
		//ReactorDebugAgent.init();
		//ReactorDebugAgent.processExistingClasses();
		Flux<Integer> integerFlux = Flux.create((FluxSink<Integer> fluxSink) -> {
			IntStream.range(0, 5).peek(i -> System.out.println("going to emit - " + i)).forEach(fluxSink::next);
		});
		//First observer. takes 1 ms to process each element
		integerFlux.delayElements(Duration.ofMillis(1)).subscribe(i -> System.out.println("First :: " + i));

		//Second observer. takes 2 ms to process each element
		integerFlux.delayElements(Duration.ofMillis(2)).subscribe(i -> System.out.println("Second:: " + i));
	}

	@Test
	public void fluxGenerateTest() throws Exception {
		AtomicInteger atomicInteger = new AtomicInteger();

		//Flux generate sequence
		Flux<Integer> integerFlux = Flux.generate((SynchronousSink<Integer> synchronousSink) -> {
			System.out.println("Flux generate");
			synchronousSink.next(atomicInteger.getAndIncrement());
		});

		//observer
		integerFlux.delayElements(Duration.ofMillis(50)).subscribe(i -> System.out.println("First consumed ::" + i));
		integerFlux.delayElements(Duration.ofMillis(50)).subscribe(i -> System.out.println("SECOND consumed ::" + i));

		Thread.sleep(3000L);
	}

	@Test
	public void fluxSinkConsumerShareTest() throws Exception {

		//create an instance of FluxSink implementation
		FluxSinkImpl fluxSinkConsumer = new FluxSinkImpl();

		//create method can accept this instance
		Flux<Integer> integerFlux = Flux.create(fluxSinkConsumer).delayElements(Duration.ofMillis(1)).share();
		integerFlux.delayElements(Duration.ofMillis(1)).subscribe(i -> System.out.println("First :: " + i));
		integerFlux.delayElements(Duration.ofMillis(2)).subscribe(i -> System.out.println("Second:: " + i));

		//We emit elements here
		IntStream.range(0, 5).forEach(fluxSinkConsumer::publishEvent);

		Thread.sleep(2000L);

	}

	@Test
	public void multiThreadAsynchronousEmittingTest() throws Exception {
		// multiThreadAsynchronousEmittingTest

		//create an instance of FluxSink implementation
		FluxSinkImpl fluxSinkConsumer = new FluxSinkImpl();

		//create method can accept this instance
		Flux<Integer> integerFlux = Flux.create(fluxSinkConsumer).delayElements(Duration.ofMillis(1)).share();
		integerFlux.delayElements(Duration.ofMillis(1)).subscribe(i -> System.out.println("First :: " + i));
		integerFlux.delayElements(Duration.ofMillis(2)).subscribe(i -> System.out.println("Second:: " + i));

		//We emit elements here
		Runnable runnable = () -> {
			IntStream.range(0, 5)
					.forEach(fluxSinkConsumer::publishEvent);
		};

		for (int i = 0; i < 3; i++) {
			new Thread(runnable).start();
		}

		Thread.sleep(2000L);
	}

	@Test
	public void generateTest() throws Exception{
		AtomicInteger atomicInteger = new AtomicInteger();

		//Flux generate sequence
		Flux<Integer> integerFlux = Flux.generate((SynchronousSink<Integer> synchronousSink) -> {
			System.out.println("Flux generate");
			synchronousSink.next(atomicInteger.getAndIncrement());
		});

		//observer
		integerFlux.delayElements(Duration.ofMillis(10)).subscribe(i -> System.out.println("First consumed ::" + i));
		integerFlux.delayElements(Duration.ofMillis(10)).subscribe(i -> System.out.println("Second consumed ::" + i));
		Thread.sleep(2000L);
	}

	@Test
	public void generateWithStateTest() throws Exception {
		//To supply an initial state
		Callable<Integer> initialState = () -> 65;

		//BiFunction to consume the state, emit value, change state
		BiFunction<Integer, SynchronousSink<Character>, Integer> generator = (state, sink) -> {
			char value = (char) state.intValue();
			sink.next(value);
			if (value == 'Z') {
				sink.complete();
			}
			return state + 1;
		};

		//Flux which accepts initialstate and bifunction as arg
		Flux<Character> charFlux = Flux.generate(initialState, generator);

		//Observer
		charFlux.delayElements(Duration.ofMillis(50)).subscribe(i -> System.out.println("Consumed A::" + i));
		charFlux.delayElements(Duration.ofMillis(50)).subscribe(i -> System.out.println("Consumed B::" + i));

		Thread.sleep(2000L);
	}


	public class FluxSinkImpl implements Consumer<FluxSink<Integer>> {

		private FluxSink<Integer> fluxSink;

		@Override
		public void accept(FluxSink<Integer> integerFluxSink) {
			this.fluxSink = integerFluxSink;
		}

		public void publishEvent(int event) {
			this.fluxSink.next(event);
		}

	}
}
