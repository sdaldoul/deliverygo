package com.example.deliverygo;

import com.example.deliverygo.entity.CryptoUser;
import com.example.deliverygo.entity.Order;
import com.example.deliverygo.entity.Proposal;
import com.example.deliverygo.model.UserProfile;
import com.example.deliverygo.repository.CryptoUserRepository;
import com.example.deliverygo.repository.OrderRepository;
import com.example.deliverygo.repository.ProposalRepository;
import com.example.deliverygo.service.UserRegistrationService;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.data.mongodb.core.ReactiveMongoOperations;
import org.springframework.security.crypto.password.PasswordEncoder;
import reactor.core.publisher.Flux;
import reactor.tools.agent.ReactorDebugAgent;

@SpringBootApplication
@Slf4j
public class DeliverygoApplication {

	public static void main(String[] args) {
		ReactorDebugAgent.init();
		SpringApplication.run(DeliverygoApplication.class, args);
	}

	@Bean
	CommandLineRunner init(ReactiveMongoOperations operations,
			ProposalRepository repository,
			CryptoUserRepository cryptoUserRepository,
			OrderRepository orderRepository,
			UserRegistrationService registrationService,
			PasswordEncoder encoder) {
		return args -> {

	/* CollectionOptions options = CollectionOptions.empty()
					.capped().size(5242880)
					.maxDocuments(5000);
			operations.createCollection("Product", options);*/
			     /*operations.collectionExists(Product.class)
                    .flatMap(exists -> exists ? operations.dropCollection(Product.class) : Mono.just(exists))
					.thenMany(v -> operations.createCollection(Product.class))
					.thenMany(productFlux)
					.thenMany(repository.findAll())
					.subscribe(System.out::println);*/

			// insert orders 1 ************************ //
			List<Proposal> proposalList1 = Arrays.asList(
					Proposal
							.builder()
							.user(UserProfile.builder().pseudo("SLAH").build())
							.proposalCreationDateTime(LocalDateTime.now().plusDays(1))
							.wantToGetPaid("35DTN")
							.proposalCityToCollect("Monastir")
							.whenYouCanDeliverIt(LocalDate.now())
							.build());

			Order order1 = Order
					.builder()
					.user(UserProfile.builder().pseudo("ELYAS").build())
					.productName("DOLIPRAN")
					.orderCreationDateTime(LocalDateTime.now())
					.whenYouNeedIt(LocalDate.now())
					.readyToPay("30DTN")
					.cityToCollect("sousse")
					.proposals(proposalList1)
					.build();
			// insert ********************************* //

			// insert orders 2 ************************ //
			List<Proposal> proposalList2 = Arrays.asList(
					Proposal
							.builder()
							.proposalCreationDateTime(LocalDateTime.now().plusDays(3))
							.user(UserProfile.builder().pseudo("AZIZA").build())
							.wantToGetPaid("35DTN")
							.proposalCityToCollect("Sfax")
							.whenYouCanDeliverIt(LocalDate.now())
							.build());

			Order order2 = Order.builder()
					.productName("FERVEX")
					.user(UserProfile.builder().pseudo("Yassine").build())
					.orderCreationDateTime(LocalDateTime.now())
					.whenYouNeedIt(LocalDate.now())
					.readyToPay("33DTN")
					.cityToCollect("Benzart")
					.proposals(proposalList2)
					. build();
			// insert ********************************* //

			Flux<Order> orderFlux = Flux.just(order1, order2).flatMap(orderRepository::save);

			orderRepository
					.deleteAll()
					.thenMany(orderFlux)
					.thenMany(orderRepository.findAll())
					.subscribe(System.out::println);

			//save user

      //UserDto userDto = new UserDto("firstname","lastname","user","user@gmail.com","user","user",1234,1234);
			//registrationService.registerNewUser(userDto);
			log.info("######### START cryptoUser START ########");
			CryptoUser cryptoUser = new CryptoUser("user", "firstname", "lastname", "user@gmail.com", encoder.encode("user"),
					encoder.encode("1234"));
			cryptoUser.setVerified(true);
			Flux<CryptoUser> cryptoUserFlux= Flux.just(cryptoUser).flatMap(cryptoUserRepository::save);

			cryptoUserRepository
					.deleteAll()
					.thenMany(cryptoUserFlux)
					.thenMany(cryptoUserRepository.findAll())
					.subscribe(System.out::println);

			log.info("######### END cryptoUser END ########");


		};
	}

}
