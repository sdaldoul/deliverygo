package com.example.deliverygo;

import com.example.deliverygo.model.Order;
import com.example.deliverygo.model.Product;
import com.example.deliverygo.model.Proposal;
import com.example.deliverygo.repository.OrderRepository;
import com.example.deliverygo.repository.ProductRepository;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Arrays;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.data.mongodb.core.CollectionOptions;
import org.springframework.data.mongodb.core.ReactiveMongoOperations;
import reactor.core.publisher.Flux;

@SpringBootApplication
public class DeliverygoApplication {

	public static void main(String[] args) {
		SpringApplication.run(DeliverygoApplication.class, args);
	}

	@Bean
	CommandLineRunner init(ReactiveMongoOperations operations,
			ProductRepository repository,
			OrderRepository orderRepository) {
		return args -> {

	/*		CollectionOptions options = CollectionOptions.empty()
					.capped().size(5242880)
					.maxDocuments(5000);
			operations.createCollection("Product", options);*/

			Flux<Product> productFlux = Flux.just(
					new Product(null, "Big Latte", 2.99),
					new Product(null, "Big Decaf", 2.49),
					new Product(null, "Green Tea", 1.99))
					.flatMap(repository::save);

			productFlux
					.thenMany(repository.findAll())
					.subscribe(System.out::println);

			// insert orders
			Flux<Order> orderFlux = Flux.just(
					Order.builder().productName("DOLIPRAN").
							orderCreationDateTime(LocalDateTime.now()).
							whenYouNeedIt(LocalDate.now()).
							readyToPay("30DTN").
							cityToCollect("sousse").
							proposals(Arrays.asList(
									Proposal.builder().
									wantToGetPaid("35DTN").
									proposalCityToCollect("Monastir").
									whenYouCanDeliverIt(LocalDate.now()).
									build())).
							build(),
					Order.builder().productName("FERVEX").
							orderCreationDateTime(LocalDateTime.now()).
							whenYouNeedIt(LocalDate.now()).
							readyToPay("33DTN").
							cityToCollect("Benzart").
							proposals(Arrays.asList(
									Proposal.builder().
											wantToGetPaid("35DTN").
											proposalCityToCollect("Sfax").
											whenYouCanDeliverIt(LocalDate.now()).
											build())).
							build())
					.flatMap(orderRepository::save);

			orderFlux
					.thenMany(orderRepository.findAll())
					.subscribe(System.out::println);

            /*operations.collectionExists(Product.class)
                    .flatMap(exists -> exists ? operations.dropCollection(Product.class) : Mono.just(exists))
					.thenMany(v -> operations.createCollection(Product.class))
					.thenMany(productFlux)
					.thenMany(repository.findAll())
					.subscribe(System.out::println);*/
		};
	}

}
