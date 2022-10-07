package io.github.martnsbreno.quarkussocial.rest;

import io.github.martnsbreno.quarkussocial.domain.model.User;
import io.github.martnsbreno.quarkussocial.domain.repository.FollowersRepository;
import io.github.martnsbreno.quarkussocial.domain.repository.UserRepository;
import io.github.martnsbreno.quarkussocial.rest.dto.FollowerRequest;
import io.quarkus.test.common.http.TestHTTPEndpoint;
import io.quarkus.test.junit.QuarkusTest;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.*;

import javax.inject.Inject;
import javax.transaction.Transactional;

import static io.restassured.RestAssured.given;
@QuarkusTest
@TestHTTPEndpoint(FollowerResource.class)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class FollowerResourceTest {

    Long userId;
    Long followerId;
    @Inject
    UserRepository userRepository;
    @Inject
    FollowersRepository followersRepository;

    @BeforeEach
    @Transactional
    void setUp() {
        var user = new User();
        user.setAge(30);
        user.setName("Fulano");
        userRepository.persist(user);
        userId = user.getId();

        var follower = new User();
        follower.setAge(33);
        follower.setName("Ciclano");
        userRepository.persist(follower);
        followerId = follower.getId();
    }

    @Test
    @DisplayName("should return 409 when followerid is equal to user id")
    @Order(1)
    public void sameUserAsFollowerTest() {

        var body = new FollowerRequest();
        body.setFollowerId(userId);

        given().contentType(ContentType.JSON).body(body).pathParam("userid", userId)
                .when().put()
                .then().statusCode(409);
    }

    @Test
    @DisplayName("should return 404 when user is not found")
    @Order(2)
    public void userNotFoundTest() {

        var body = new FollowerRequest();
        body.setFollowerId(userId);

        var userNotFoundId = 999;

        given().contentType(ContentType.JSON).body(body).pathParam("userid", userNotFoundId)
                .when().put()
                .then().statusCode(404);
    }

    @Test
    @DisplayName("should follow a user")
    @Transactional
    @Order(3)
    public void followingUser() {

        var body = new FollowerRequest();
        body.setFollowerId(followerId);

        given().contentType(ContentType.JSON).body(body).pathParam("userid", userId)
                .when().put()
                .then().statusCode(204);
    }

    @Test
    @DisplayName("should return 404 when user is not found")
    @Order(4)
    public void userNotFoundTestGet() {
        var userNotFoundId = 999;

        given().contentType(ContentType.JSON).pathParam("userid", userNotFoundId)
                .when().get()
                .then().statusCode(404);
    }

    @Test
    @DisplayName("should return a list of followers")
    @Order(5)
    public void returnListOfFollowers() {
        given().contentType(ContentType.JSON).pathParam("userid", userId)
                .when().get()
                .then().statusCode(200);
    }


}