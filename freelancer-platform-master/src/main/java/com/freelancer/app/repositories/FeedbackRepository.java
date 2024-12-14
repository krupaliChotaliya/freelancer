package com.freelancer.app.repositories;


import com.freelancer.app.models.Bid;
import com.freelancer.app.models.Feedback;
import com.freelancer.app.models.Job;
import com.freelancer.app.models.User;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository("feedbackRepository")
public interface FeedbackRepository extends JpaRepository<Feedback, Long> {
    
	Feedback findByBid(Bid bid);

    @Query("SELECT f"
			+ " FROM Feedback f "
			+ " JOIN f.bid b "
			+ " JOIN b.job j "
			+ " JOIN j.author u"
			+ " WHERE u = :user ")
	List<Feedback> findByClient(@Param("user") User user);

    @Query("SELECT f"
			+ " FROM Feedback f "
			+ " JOIN f.bid b "
			+ " JOIN b.job j "
			+ " WHERE j = :job ")
	Feedback findByJob(@Param("job") Job job);

	List<Feedback> findByBid(List<Bid> bids);
	
}