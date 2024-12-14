package com.freelancer.app.controllers;

import com.freelancer.app.helpers.FreelancePlatformHelper;
import com.freelancer.app.models.Bid;
import com.freelancer.app.models.Job;
import com.freelancer.app.models.User;
import com.freelancer.app.services.BidService;
import com.freelancer.app.services.JobService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.validation.Valid;

@Controller
@RequestMapping("/bid")
public class BidController extends AbstractController{

    @Autowired
    BidService bidService;

    @Autowired
    JobService jobService;


    @PostMapping("/save")
    public String saveBid(@Valid Bid bid, @RequestParam("job_id") Long jobId){

        User user = super.getCurrentUser();

        if( user == null){
            return "redirect:/";
        }

        Job job = jobService.get(jobId);
        
        String created = FreelancePlatformHelper.getCurrentMySQLDate();

        bid.setJob(job);
        bid.setUser(user);
        bid.setCreated(created);

        bidService.save(bid);
        return "redirect:/job/view/" + job.getId();
    }
    
    @GetMapping("/accept/{bidId}")
    public String acceptBid(Model model, @PathVariable("bidId") long bidId) throws Exception{
    	
    	Bid bid = bidService.get(bidId);
    	
    	User me = getCurrentUser();
    	
    	// If bid was not found or if I am NOT owner of the job for which bid is placed. 
    	if( bid == null || me.getId() != bid.getJob().getAuthor().getId() ) {
//    		return "redirect:" + request.getHeader("Referer");
    		throw new Exception("No bid or no permissions");
    	}
    	
    	boolean saved = bidService.acceptBid(bid);
    	
    	if( ! saved ) {
    		throw new Exception("Bid can't be saved!");
    	}
    	
    	return "redirect:/message/job_room/" + bid.getJob().getId() + "/" + bid.getUser().getId();
    	
    }
    
    @GetMapping("/my-contracts")
    public String myContracts(Model model) throws Exception{

    	User me = getCurrentUser();
    	
    	Set<Bid> contracts = new HashSet<Bid>( bidService.findByUser(me) );
    	    	
    	contracts.removeIf(bid -> {
    		return bid.getAccepted() == 0;
    	});
    	
    	model.addAttribute("contracts", contracts);
    	model.addAttribute("me", me);
    	
    	return "frontend/bid/my_contracts";
    }
    
    @GetMapping("close/{bidId}")
    public String close(Model model, @PathVariable("bidId") long bidId) throws Exception{
    	
    	User me = getCurrentUser();
    	Bid bid = bidService.get(bidId);
    	
    	if(bid.getClosed() == 1) {
    		return "redirect:/feedback/view/" + bidId;
    	}

		//if I am NOT owner of the job for which bid is placed.
    	if( bid.getJob().getAuthor().getId() != me.getId()) {
    		throw new Exception("You can't close this bid! You aren't owner of the job.");
    	}
    	 
    	bid.setClosed(1);
    	
    	bidService.save(bid);
    	
    	return "redirect:/feedback/" + bidId;
    }
}
