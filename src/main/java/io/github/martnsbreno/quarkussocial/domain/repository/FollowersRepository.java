package io.github.martnsbreno.quarkussocial.domain.repository;

import io.github.martnsbreno.quarkussocial.domain.model.Follower;
import io.github.martnsbreno.quarkussocial.domain.model.User;
import io.quarkus.hibernate.orm.panache.PanacheQuery;
import io.quarkus.hibernate.orm.panache.PanacheRepository;
import io.quarkus.panache.common.Parameters;

import javax.enterprise.context.ApplicationScoped;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@ApplicationScoped
public class FollowersRepository implements PanacheRepository<Follower> {

    public boolean follows(User follower, User user) {
        Map<String, Object> params = Parameters
                .with("follower", follower).and("user", user).map();

        PanacheQuery<Follower> query = find("follower = :follower and user = :user", params);
        Optional<Follower> result = query.firstResultOptional();

        return result.isPresent();
    }

    public List<Follower> findByUser (Long userId) {
        PanacheQuery<Follower> query = find("user.id", userId);
        return query.list();
    }

    public void deleteByFollowerAndUser(Long followerId, Long userid) {
        var params = Parameters.with("userid", userid).and("followerId", followerId)
                .map();

        delete("follower.id =:followerId and user.id =:userid", params);
    }
}
