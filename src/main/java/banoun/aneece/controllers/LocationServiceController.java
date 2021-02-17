package banoun.aneece.controllers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import banoun.aneece.dto.User;
import banoun.aneece.exceptions.LocationException;
import banoun.aneece.services.Locationservice;
import banoun.aneece.services.RemoteLocationService;
import reactor.core.publisher.Flux;

@RestController
@RequestMapping(path = "/api/v1/location")
public class LocationServiceController {

	@Autowired
	private Locationservice locationservice;
	@Autowired
	private RemoteLocationService remoteLocationService;

	@RequestMapping(path = "/filteredUsers", method = RequestMethod.GET)
	public List<User> filteredUsers() {
		try {
			// creating 2 services to allow mocking the remote service
			User[] usersFromRemoteApi = remoteLocationService.getUsersFromRemoteApi();
			if(usersFromRemoteApi == null || usersFromRemoteApi.length == 0) {
				throw new LocationException("Remote service error");
			}
			List<User> usersWithinFiftyMilesFromCentralLondon = locationservice
					.usersWithinFiftyMilesFromCentralLondon(usersFromRemoteApi);
			return usersWithinFiftyMilesFromCentralLondon;
		} catch (Throwable t) {
			throw new LocationException(t);
		}
	}

	@RequestMapping(path = "/cityUsers", method = RequestMethod.GET)
	public User[] cityUsers() {
		try {
			User[] cityUsersFromRemoteApi = remoteLocationService.getCityUsersFromRemoteApi();
			if (cityUsersFromRemoteApi == null || cityUsersFromRemoteApi.length == 0) {
				throw new LocationException("Remote service serror");
			}
			return cityUsersFromRemoteApi;
		} catch (Throwable t) {
			throw new LocationException(t);
		}
	}

	
	@RequestMapping(produces = MediaType.APPLICATION_STREAM_JSON_VALUE, path = "/filteredUsersAsync", method = RequestMethod.GET)
	public Flux<User> filteredUsersAsync() {
		try {
			// creating 2 services to allow mocking the remote service
			Flux<User> usersFromRemoteApi = remoteLocationService.getUsersFromRemoteApiAsync();
			if (usersFromRemoteApi == null) {
				throw new LocationException("Remote service error");
			}
			return usersFromRemoteApi;
		} catch (Throwable t) {
			throw new LocationException(t);
		}
	}
	
	@RequestMapping(produces =  MediaType.APPLICATION_STREAM_JSON_VALUE, path = "/cityUsersAsync", method = RequestMethod.GET)
	public Flux<User> cityUsersAsync() {
		return remoteLocationService.getCityUsersFromRemoteApiAsync();
	}
	
}
