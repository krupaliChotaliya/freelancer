package com.freelancer.app.services;

import com.freelancer.app.models.Bid;
import com.freelancer.app.models.Feedback;
import com.freelancer.app.models.Job;
import com.freelancer.app.models.User;
import com.freelancer.app.repositories.FeedbackRepository;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class FeedbackService {

	@Autowired
    FeedbackRepository feedbackRepository;

	public Feedback save(Feedback feedback) {
		return feedbackRepository.save(feedback);
	}

	public Feedback get(Long id) {
		return feedbackRepository.findOne(id);
	}
    
    public Feedback findByBid(Bid bid){
        return feedbackRepository.findByBid(bid);
    }

	public List<Feedback> findByClient(User user) {
		return feedbackRepository.findByClient(user);
	}
	
	public Feedback findByJob(Job job) {
		return feedbackRepository.findByJob(job);
	}
	
	public List<Feedback> findByBids(List<Bid> bids){
		return feedbackRepository.findByBid(bids);
	}

}
