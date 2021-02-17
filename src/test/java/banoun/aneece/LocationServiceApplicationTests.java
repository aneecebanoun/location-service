package banoun.aneece;

import static banoun.aneece.constants.Constants.LOCALHOST;
import static banoun.aneece.constants.Constants.REMOTE_SERVICE_CITY;
import static banoun.aneece.constants.Constants.REMOTE_SERVICE_CITY_USERS_URL;
import static banoun.aneece.constants.Constants.REMOTE_SERVICE_USERS_URL;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestClientException;
import org.springframework.web.util.UriComponentsBuilder;

import com.google.gson.Gson;

import banoun.aneece.dto.User;
import banoun.aneece.services.RemoteLocationService;
import reactor.core.publisher.Flux;

@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
class LocationServiceApplicationTests {

	@LocalServerPort
	private int port;

	@Autowired
	private TestRestTemplate restTemplate;
	@Autowired
	private Gson gson;

	@MockBean
	private RemoteLocationService remoteLocationService;

	@Value(REMOTE_SERVICE_USERS_URL)
	private String usersUrl;
	@Value(REMOTE_SERVICE_CITY_USERS_URL)
	private String cityUsersUrl;
	@Value(REMOTE_SERVICE_CITY)
	private String city;

	@Test
	public void remoteUsersTest() throws RestClientException, MalformedURLException {
		ResponseEntity<String> response = restTemplate.getForEntity(new URL(usersUrl).toString(), String.class);
		assertEquals(response.getStatusCode(), HttpStatus.OK);
	}

	@Test
	public void filteredUsersTest() throws RestClientException, MalformedURLException {
		// this location to be filtered latitude 71.5489435, longitude 0.3860497
		String remoteServiceJson = "[{\"id\":266,\"first_name\":\"Ancell\",\"last_name\":\"Garnsworthy\",\"email\":\"agarnsworthy7d@seattletimes.com\",\"ip_address\":\"67.4.69.137\",\"latitude\":51.6553959,\"longitude\":0.0572553},{\"id\":554,\"first_name\":\"Phyllys\",\"last_name\":\"Hebbs\",\"email\":\"phebbsfd@umn.edu\",\"ip_address\":\"100.89.186.13\",\"latitude\":51.5489435,\"longitude\":0.3860497\n},{\"id\":0,\"first_name\":\"Outsider\",\"last_name\":\"Outsider\",\"email\":\"out@umn.edu\",\"ip_address\":\"150.89.186.13\",\"latitude\":71.5489435,\"longitude\":0.3860497\n}]";
		User[] list = gson.fromJson(remoteServiceJson, User[].class);
		Mockito.when(remoteLocationService.getUsersFromRemoteApi()).thenReturn(list);

		// result with one user filtered
		String expectedJson = "[{\"id\":266,\"first_name\":\"Ancell\",\"last_name\":\"Garnsworthy\",\"email\":\"agarnsworthy7d@seattletimes.com\",\"ip_address\":\"67.4.69.137\",\"latitude\":51.6553959,\"longitude\":0.0572553},{\"id\":554,\"first_name\":\"Phyllys\",\"last_name\":\"Hebbs\",\"email\":\"phebbsfd@umn.edu\",\"ip_address\":\"100.89.186.13\",\"latitude\":51.5489435,\"longitude\":0.3860497}]";
		ResponseEntity<String> response = restTemplate
				.getForEntity(new URL(LOCALHOST + port + "/api/v1/location/filteredUsers").toString(), String.class);
		assertEquals(expectedJson, response.getBody());
	}

	@Test
	public void filteredUsersAsyncTest() throws RestClientException, MalformedURLException {
		String remoteServiceJson = "{\"id\":0,\"first_name\":\"Outsider\",\"last_name\":\"Outsider\",\"email\":\"out@umn.edu\",\"ip_address\":\"150.89.186.13\",\"latitude\":71.5489435,\"longitude\":0.3860497\n}";
		User list = gson.fromJson(remoteServiceJson, User.class);
		Mockito.when(remoteLocationService.getUsersFromRemoteApiAsync()).thenReturn(Flux.just(list));
		ResponseEntity<String> response = restTemplate
				.getForEntity(new URL(LOCALHOST + port + "/api/v1/location/filteredUsersAsync").toString(), String.class);
		assertEquals(response.getStatusCode(), HttpStatus.OK);
	}

	@Test
	public void filteredUsersAsyncErrorTest() throws RestClientException, MalformedURLException {
		Mockito.when(remoteLocationService.getUsersFromRemoteApi()).thenReturn(null);
		ResponseEntity<String> response = restTemplate
				.getForEntity(new URL(LOCALHOST + port + "/api/v1/location/filteredUsersAsync").toString(), String.class);
		assertEquals(response.getStatusCode(), HttpStatus.BAD_GATEWAY);
	}

	@Test
	public void filteredUsersErrorTest() throws RestClientException, MalformedURLException {
		Mockito.when(remoteLocationService.getUsersFromRemoteApi()).thenReturn(null);
		ResponseEntity<String> response = restTemplate
				.getForEntity(new URL(LOCALHOST + port + "/api/v1/location/filteredUsers").toString(), String.class);
		assertEquals(response.getStatusCode(), HttpStatus.BAD_GATEWAY);
	}

	@Test
	public void remoteCityUsersTest() throws RestClientException, MalformedURLException {
		Map<String, String> urlParams = new HashMap<>();
		urlParams.put("city", city);
		UriComponentsBuilder builder = UriComponentsBuilder.fromUriString(cityUsersUrl);
		ResponseEntity<String> response = restTemplate.getForEntity(builder.buildAndExpand(urlParams).toUri(),
				String.class);
		assertEquals(response.getStatusCode(), HttpStatus.OK);
	}

	@Test
	public void cityUserTest() throws RestClientException, MalformedURLException {
		String remoteServiceJson = "[{\"id\":135,\"first_name\":\"Mechelle\",\"last_name\":\"Boam\",\"email\":\"mboam3q@thetimes.co.uk\",\"ip_address\":\"113.71.242.187\",\"latitude\":-6.5115909,\"longitude\":105.652983}]";
		User[] list = gson.fromJson(remoteServiceJson, User[].class);
		Mockito.when(remoteLocationService.getCityUsersFromRemoteApi()).thenReturn(list);
		ResponseEntity<String> response = restTemplate.getForEntity(new URL(LOCALHOST + port + "/api/v1/location/cityUsers").toString(),
				String.class);
		assertEquals(remoteServiceJson, response.getBody());
	}

	@Test
	public void cityUserAsyncTest() throws RestClientException, MalformedURLException {
		String remoteServiceJson = "{\"id\":135,\"first_name\":\"Mechelle\",\"last_name\":\"Boam\",\"email\":\"mboam3q@thetimes.co.uk\",\"ip_address\":\"113.71.242.187\",\"latitude\":-6.5115909,\"longitude\":105.652983}";
		User list = gson.fromJson(remoteServiceJson, User.class);
		Mockito.when(remoteLocationService.getCityUsersFromRemoteApiAsync()).thenReturn(Flux.just(list));
		ResponseEntity<String> response = restTemplate
				.getForEntity(new URL(LOCALHOST + port + "/api/v1/location/cityUsersAsync").toString(), String.class);
		assertEquals(remoteServiceJson, response.getBody().trim());
	}

	@Test
	public void cityUserErrorTest() throws RestClientException, MalformedURLException {
		Mockito.when(remoteLocationService.getCityUsersFromRemoteApi()).thenReturn(null);
		ResponseEntity<String> response = restTemplate.getForEntity(new URL(LOCALHOST + port + "/api/v1/location/cityUsers").toString(),
				String.class);
		assertEquals(response.getStatusCode(), HttpStatus.BAD_GATEWAY);
	}

}
