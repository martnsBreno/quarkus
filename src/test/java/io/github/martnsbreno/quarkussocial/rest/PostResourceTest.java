package io.github.martnsbreno.quarkussocial.rest;

import io.github.martnsbreno.quarkussocial.domain.model.Follower;
import io.github.martnsbreno.quarkussocial.domain.model.Post;
import io.github.martnsbreno.quarkussocial.domain.model.User;
import io.github.martnsbreno.quarkussocial.domain.repository.FollowersRepository;
import io.github.martnsbreno.quarkussocial.domain.repository.PostRepository;
import io.github.martnsbreno.quarkussocial.domain.repository.UserRepository;
import io.github.martnsbreno.quarkussocial.rest.dto.CreatePostRequest;
import io.quarkus.test.common.http.TestHTTPEndpoint;
import io.quarkus.test.junit.QuarkusTest;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import javax.inject.Inject;
import javax.transaction.Transactional;
import java.util.HashMap;
import java.util.Map;

import static io.restassured.RestAssured.given;

@QuarkusTest
@TestHTTPEndpoint(PostResource.class)
class PostResourceTest {

    @Inject
    UserRepository userRepository;
    @Inject
    FollowersRepository followerRepository;
    @Inject
    PostRepository postRepository;

    Long userId;
    Long userNotFollowerId;
    Long userFollowerId;
    Long postId;

    @Transactional
    @BeforeEach
    public void setUpUser() {
        var user = new User();
        user.setName("Cyber");
        user.setAge(22);
        userRepository.persist(user);
        userId = user.getId();

        Post post = new Post();
        post.setText("Hello");
        post.setUser(user);
        postRepository.persist(post);
        postId = post.getId();

        var userNotFollower = new User();
        userNotFollower.setAge(33);
        userNotFollower.setName("Cicrano");
        userRepository.persist(userNotFollower);
        userNotFollowerId = userNotFollower.getId();

        //usuário seguidor
        var userFollower = new User();
        userFollower.setAge(31);
        userFollower.setName("Terceiro");
        userRepository.persist(userFollower);
        userFollowerId = userFollower.getId();

        Follower follower = new Follower();
        follower.setUser(user);
        follower.setFollower(userFollower);
        followerRepository.persist(follower);
    }

    @Test
    @DisplayName("should create a post for an user")
    public void createPostTest() {
        var postRequest = new CreatePostRequest();
        postRequest.setText("Olá Mundo!");
        var userId = 1;
        given()
                .contentType(ContentType.JSON)
                .body(postRequest)
                .pathParam("userid", userId)
                .when().post()
                .then()
                .statusCode(201);
    }

    @Test
    @DisplayName("should return 404 for a invalid user")
    public void createPostTestWithInvalidUserId() {
        var postRequest = new CreatePostRequest();
        postRequest.setText("Olá Mundo!");
        var invalidUserId = 9999;
        given()
                .contentType(ContentType.JSON)
                .body(postRequest)
                .pathParam("userid", invalidUserId)
                .when().post()
                .then()
                .statusCode(404);
    }


    @Test
    @DisplayName("Should return bad request if a follower id is not specified")
    public void listPostFollowerIdIsNotGiven() {
        var userid = 1;

        given().pathParam("userid", userid)
                .when().get()
                .then().statusCode(400);
    }

    @Test
    @DisplayName("Should return forbidden if follower isnt a follower")
    public void listPostNotAFollower() {
        given().pathParam("userid", userId).header("followerId", userNotFollowerId)
                .when().get()
                .then().statusCode(403);
    }

    @Test
    @DisplayName("Should return ok if everything is correct")
    public void ListPostsOk() {
        given().pathParam("userid", userId).header("followerId", userFollowerId)
                .when().get()
                .then().statusCode(200);
    }
    @Test
    @DisplayName("Should return method not allowed if post id is invalid")
    public void deleteWithUserInvalid() {
        given().pathParam("userid", userId).when().delete().then().statusCode(405);
    }
}