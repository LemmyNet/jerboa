package com.jerboa.datatypes

/**
 * Aggregate data for a person.
 */
data class PersonAggregates(
  val id: Int,
  val person_id: Int,
  val post_count: Int,
  val post_score: Int,
  val comment_count: Int,
  val comment_score: Int,
)

/**
 * Aggregate data for your site.
 */
data class SiteAggregates(
  val id: Int,
  val site_id: Int,
  val users: Int,
  val posts: Int,
  val comments: Int,
  val communities: Int,
  /**
   * Active users per day.
   */
  val users_active_day: Int,
  /**
   * Active users per week.
   */
  val users_active_week: Int,
  /**
   * Active users per month.
   */
  val users_active_month: Int,
  /**
   * Active users per year.
   */
  val users_active_half_year: Int,
)

/**
 * Aggregate data for your post.
 */
data class PostAggregates(
  val id: Int,
  val post_id: Int,
  val comments: Int,
  val score: Int,
  val upvotes: Int,
  val downvotes: Int,
  /**
   * Newest comment time, limited to 2 days, to prevent necrobumping.
   */
  val newest_comment_time_necro: String,
  val newest_comment_time: String,
)

/**
 * Aggregate data for your community.
 */
data class CommunityAggregates(
  val id: Int,
  val community_id: Int,
  val subscribers: Int,
  val posts: Int,
  val comments: Int,
  /**
   * Active users per day.
   */
  val users_active_day: Int,
  /**
   * Active users per week.
   */
  val users_active_week: Int,
  /**
   * Active users per month.
   */
  val users_active_month: Int,
  /**
   * Active users per year.
   */
  val users_active_half_year: Int,
)

/**
 * Aggregate data for your comment.
 */
data class CommentAggregates(
  val id: Int,
  val comment_id: Int,
  val score: Int,
  val upvotes: Int,
  val downvotes: Int,
)
