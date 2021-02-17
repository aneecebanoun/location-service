package banoun.aneece.services;

import static banoun.aneece.constants.Constants.*;
import java.time.Duration;
import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.util.UriComponentsBuilder;

import banoun.aneece.dto.User;
import reactor.core.publisher.Flux;

@Service
public class RemoteLocationService {

	@Autowired
	private RestTemplate restTemplate;
	@Autowired
	private Locationservice locationservice;
	@Autowired
	private Environment env;

	@Value(REMOTE_SERVICE_USERS_URL)
	private String usersUrl;
	@Value(REMOTE_SERVICE_CITY_USERS_URL)
	private String cityUsersUrl;
	@Value(REMOTE_SERVICE_CITY)
	private String city;

	public User[] getUsersFromRemoteApi() {
		return restTemplate.getForObject(usersUrl, User[].class);
	}

	public User[] getCityUsersFromRemoteApi() {
		Map<String, String> urlParams = new HashMap<>();
		urlParams.put("city", city);
		UriComponentsBuilder builder = UriComponentsBuilder.fromUriString(cityUsersUrl);
		return restTemplate.getForObject(builder.buildAndExpand(urlParams).toUri(), User[].class);
	}

	public Flux<User> getUsersFromRemoteApiAsync() {
		WebClient wc = WebClient.create(usersUrl);
		Long serviceDelay = Long.parseLong(env.getProperty(SERVICE_DELAY_DURATION_IN_MS));
		return wc.get().retrieve().bodyToFlux(User.class)
				.filter(user -> locationservice.usersWithinRadiusFromCity(user))
				.delayElements(Duration.ofMillis(serviceDelay));
	}

	public Flux<User> getCityUsersFromRemoteApiAsync() {
		Map<String, String> urlParams = new HashMap<>();
		urlParams.put("city", city);
		UriComponentsBuilder builder = UriComponentsBuilder.fromUriString(cityUsersUrl);
		WebClient wc = WebClient.create(builder.buildAndExpand(urlParams).toUriString());
		Long serviceDelay = Long.parseLong(env.getProperty(SERVICE_DELAY_DURATION_IN_MS));
		return wc.get().retrieve().bodyToFlux(User.class).delayElements(Duration.ofMillis(serviceDelay));
	}

}
