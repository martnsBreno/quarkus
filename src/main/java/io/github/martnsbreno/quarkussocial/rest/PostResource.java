package io.github.martnsbreno.quarkussocial.rest;

import io.github.martnsbreno.quarkussocial.domain.model.Post;
import io.github.martnsbreno.quarkussocial.domain.model.User;
import io.github.martnsbreno.quarkussocial.domain.repository.FollowersRepository;
import io.github.martnsbreno.quarkussocial.domain.repository.PostRepository;
import io.github.martnsbreno.quarkussocial.domain.repository.UserRepository;
import io.github.martnsbreno.quarkussocial.rest.dto.CreatePostRequest;
import io.github.martnsbreno.quarkussocial.rest.dto.PostResponse;
import io.quarkus.hibernate.orm.panache.PanacheQuery;
import io.quarkus.panache.common.Sort;

import javax.inject.Inject;
import javax.transaction.Transactional;
import javax.validation.Validator;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Path("users/{userid}/posts")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class PostResource {

    private UserRepository userRepository;
    private PostRepository postRepository;
    private FollowersRepository followersRepository;
    private Validator validator;

    @Inject
    public PostResource(UserRepository userRepository,
                        PostRepository postRepository,
                        FollowersRepository followersRepository) {
        this.userRepository = userRepository;
        this.postRepository = postRepository;
        this.followersRepository = followersRepository;
    }

    @POST
    @Transactional
    public Response savePost(@PathParam("userid") Long userid,
                             CreatePostRequest postRequest) {
        User user = userRepository.findById(userid);
        if (user != null) {
            Post post = new Post();
            post.setText(postRequest.getText());
            post.setUser(user);
            post.setDateTime(LocalDateTime.now());
            postRepository.persist(post);
            return Response.status(Response.Status.CREATED).build();
        }

        return Response.status(Response.Status.NOT_FOUND).build();
    }

    @GET
    public Response listPosts(@PathParam("userid") Long userid,
                              @HeaderParam("followerId") Long followerId) {
        User user = userRepository.findById(userid);

        if (followerId == null) return Response.status(Response.Status.BAD_REQUEST)
                    .entity("You need to provide the follower id").build();

        User follower = userRepository.findById(followerId);

        boolean follows = followersRepository.follows(follower, user);

        if (!follows) {
            return Response.status(Response.Status.FORBIDDEN)
                    .entity("You cant see these posts because you dont follow the user") .build();
        }

        if (user != null) {
            PanacheQuery<Post> posts = postRepository
                    .find("user", Sort.by("dateTime", Sort.Direction.Descending), user);
            List<Post> list = posts.list();

            List<PostResponse> postResponseList = list.stream().map(
                    post -> PostResponse.fromEntity(post)).collect(Collectors.toList());

            return Response.ok(postResponseList).build();
        }
        return Response.status(Response.Status.NOT_FOUND).build();
    }

    @DELETE
    @Path("{id}")
    @Transactional
    public Response deletePosts(@PathParam("userid") Long userid, @PathParam("id") Long postid) {
        User user = userRepository.findById(userid);
        Post post1 = postRepository.findById(postid);

        if (user == null || post1 == null) return Response.status(Response.Status.NOT_FOUND).build();

        if (Objects.equals(userid, post1.getUser().getId())) {
            postRepository.deleteById(post1.getId());
            return Response.noContent().build();
        }
        return Response.status(Response.Status.BAD_REQUEST).build();
    }
}

