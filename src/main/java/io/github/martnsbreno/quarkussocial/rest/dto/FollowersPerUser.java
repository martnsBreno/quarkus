package io.github.martnsbreno.quarkussocial.rest.dto;

import lombok.Data;

import java.util.List;

@Data
public class FollowersPerUser {
    private Integer followersCount;
    private List<FollowerResponse> content;
}
