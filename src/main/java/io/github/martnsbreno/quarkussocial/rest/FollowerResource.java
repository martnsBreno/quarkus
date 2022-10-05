package io.github.martnsbreno.quarkussocial.rest;

import io.github.martnsbreno.quarkussocial.domain.model.Follower;
import io.github.martnsbreno.quarkussocial.domain.model.User;
import io.github.martnsbreno.quarkussocial.domain.repository.FollowersRepository;
import io.github.martnsbreno.quarkussocial.domain.repository.UserRepository;
import io.github.martnsbreno.quarkussocial.rest.dto.FollowerRequest;
import io.github.martnsbreno.quarkussocial.rest.dto.FollowerResponse;
import io.github.martnsbreno.quarkussocial.rest.dto.FollowersPerUser;

import javax.inject.Inject;
import javax.transaction.Transactional;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.List;
import java.util.stream.Collectors;

@Path("/users/{userid}/followers")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class FollowerResource {
    private FollowersRepository followersRepository;
    private UserRepository userRepository;

    @Inject
    public FollowerResource(FollowersRepository followersRepository,
                            UserRepository userRepository) {
        this.followersRepository = followersRepository;
        this.userRepository = userRepository;
    }

    @PUT
    @Transactional
    public Response followUser(@PathParam("userid") Long id,
                               FollowerRequest request) {

        if (id.equals(request.getFollowerId())) {
            return Response.status(Response.Status.CONFLICT).entity(" You can not follow yourself ").build();
        }

        User user = userRepository.findById(id);
        if (user == null) return Response.status(Response.Status.NOT_FOUND).build();

        User follower = userRepository.findById(request.getFollowerId());

        boolean follows = followersRepository.follows(follower, user);

        if (!follows) {
            Follower entity = new Follower();
            entity.setUser(user);
            entity.setFollower(follower);
            followersRepository.persist(entity);
        }

        return Response.status(Response.Status.NO_CONTENT).build();
    }

    @GET
    public Response listFollowers(@PathParam("userid") Long userId) {

        var user = userRepository.findById(userId);
        if (user == null) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }

        List<Follower> list = followersRepository.findByUser(userId);
        FollowersPerUser responseObject = new FollowersPerUser();
        responseObject.setFollowersCount(list.size());

        List<FollowerResponse> followersList = list.stream()
                .map(FollowerResponse::new).collect(Collectors.toList());

        responseObject.setContent(followersList);
        return Response.ok(responseObject).build();
    }
    @DELETE
    @Transactional
    public Response unfollowUser(
            @PathParam("userid") Long userid, @QueryParam("followerId") Long followerId) {
        var user = userRepository.findById(userid);
        if (user == null) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }

        followersRepository.deleteByFollowerAndUser(followerId, userid);

            return Response.status(Response.Status.NO_CONTENT).build();
    }
}
