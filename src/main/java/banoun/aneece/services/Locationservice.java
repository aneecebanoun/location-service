package banoun.aneece.services;

import static banoun.aneece.constants.Constants.*;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;

import banoun.aneece.dto.User;
import reactor.core.publisher.Flux;

@Service
public class Locationservice {

	@Autowired
	private Environment env;

	public List<User> usersWithinFiftyMilesFromCentralLondon(User[] users) {
		Double radius = Double.parseDouble(env.getProperty(RADIUS_TO_CITY_CENTER));
		List<User> collect = Arrays.stream(users).filter(user -> userDistanceFromLondonCentral(user) <= radius)
				.collect(Collectors.toList());
		return collect;
	}

	public Flux<User> usersWithinFiftyMilesFromCentralLondon(Flux<User> users) {
		return users.filter(user -> usersWithinRadiusFromCity(user));
	}

	public boolean usersWithinRadiusFromCity(User user) {
		Double radius = Double.parseDouble(env.getProperty(RADIUS_TO_CITY_CENTER));
		return userDistanceFromLondonCentral(user) <= radius;
	}

	private Double rad(Double x) {
		return x * Math.PI / 180;
	}

	private Double userDistanceFromLondonCentral(User user) {
		Double centerLatitude = Double.parseDouble(env.getProperty(CITY_CENTER_LATITUDE));
		Double centerLongitude = Double.parseDouble(env.getProperty(CITY_CENTER_LONGITUDE));
		return userDistanceFromCentralPoint(centerLatitude, centerLongitude, user);
	}

	private Double userDistanceFromCentralPoint(Double centerLatitude, Double centerLongitude, User user) {
		Double latDistance = rad(user.getLatitude() - centerLatitude);
		Double logDistance = rad(user.getLongitude() - centerLongitude);
		Double a = Math.pow(Math.sin(latDistance / 2), 2) + Math.cos(rad(centerLatitude))
				* Math.cos(rad(user.getLatitude())) * Math.pow(Math.sin(logDistance / 2), 2);
		Double b = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
		return Double.parseDouble(env.getProperty(EARTH_RADIUS)) * b;
	}

}
