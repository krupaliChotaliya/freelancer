package com.freelancer.app.controllers;

import com.freelancer.app.helpers.FreelancePlatformHelper;
import com.freelancer.app.models.*;
import com.freelancer.app.services.*;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/job")
public class JobController extends AbstractController {

    @Autowired
    JobService jobService;

    @Autowired
    CategoryService categoryService;

    @Autowired
    BidService bidService;

    @Autowired
    FeedbackService feedbackService;

    @Autowired
    private UserController userController;

    @Autowired
    private EmailService emailService;

    @Value("${freelancer.job.page_size}")
    private int jobPageSize;

//    @GetMapping
//    public String listJobs(Model model, HttpServletRequest request){
//    	
//    	String filt = request.getParameter("filter");
//    	User me = getCurrentUser();
//    	boolean isMyJobsPage = false;
//    	
//    	if( filt != null && filt.equals("myjobs") && me != null) {
//    		Map<String, Object> filter = new HashMap<>();
//    		filter.put("user", me );
//            model.addAttribute("jobs", jobService.list(filter));
//            
//            isMyJobsPage = true;
//    	} else {
//    		model.addAttribute("jobs", jobService.list());
//    	}
//    	
//    	model.addAttribute("isMyJobsPage", isMyJobsPage);
//    	
//        return "frontend/job/jobs";
//    }

    @GetMapping
    public String listJobs2(Model model, HttpServletRequest request) {
        String pageUrl = "/job?a=a"; // Warning: In the HTML template we will append &page=[page] and we need to have ? in the query string

        String filt = request.getParameter("filter");
        String pPage = request.getParameter("page");

        User me = getCurrentUser ();
        boolean isMyJobsPage = false;

        Map<String, Object> filter = new HashMap<>();

        if (filt != null && filt.equals("myjobs") && me != null) {
            filter.put("user", me);
            pageUrl = "/job?filter=myjobs";
            isMyJobsPage = true;
        }

        int pageNo = 1;
        if (pPage != null) {
            pageNo = Integer.parseInt(pPage);
        }

        Page<Job> jobsPage = jobService.findAllPaged(filter, pageNo, jobPageSize);

        model.addAttribute("is_my_jobs_page", isMyJobsPage);
        model.addAttribute("jobs_page", jobsPage);
        System.out.println("[job pages ]" + jobsPage.getTotalElements() + ">>>>>>>>>>>>>>>>>>>>>.");
        model.addAttribute("page_url", pageUrl);

        return "frontend/job/jobs";
    }

    @GetMapping({"/view/{id}", "/{id}"})
    public String viewJob(Model model, @PathVariable("id") long id) {

        Job job = jobService.get(id);

        model.addAttribute("job", job);

        // Get my bid for the job and assign to the view
        Bid myBid = null;

        // Check if logged in:
        User currentUser = super.getCurrentUser();
        if (currentUser != null) {
            myBid = bidService.getUsersBidByJob(currentUser, job);
            if (myBid != null) {
                // New line to <br>
                myBid.setProposal(FreelancePlatformHelper.nl2br(myBid.getProposal()));
            }
        }

        model.addAttribute("myBid", myBid);

        model.addAttribute("me", getCurrentUser());

        // Calculate client rate:
        long avgClientFeedback = 0;
        int totalFeedbackNo = 0;
        List<Feedback> feedbacks = feedbackService.findByClient(job.getAuthor());
        if (feedbacks.size() > 0) {
            int sum = 0;
            int no = 0;
            for (Feedback f : feedbacks) {
                sum += f.getClientRate();
                no++;
            }
            avgClientFeedback = sum / no;
            totalFeedbackNo = feedbacks.size();
        }
        // Calculate hire rate:
        List<Job> jobs = jobService.findByAuthor(job.getAuthor());
        List<Job> hiredJobs = jobService.findHiredJobsByAuthor(job.getAuthor());
        double totalJobsNo = jobs.size();
        double hiredJobsNo = hiredJobs.size();
        double hireRate = (hiredJobsNo / totalJobsNo) * 100; // percent

        model.addAttribute("average_client_feedback_rate", avgClientFeedback);
        model.addAttribute("reviews_no", totalFeedbackNo);
        model.addAttribute("bids_no", bidService.findByJob(job).size());
        model.addAttribute("hire_rate", (int) hireRate);
        model.addAttribute("jobs_no", (int) totalJobsNo);
        model.addAttribute("hired_jobs_no", (int) hiredJobsNo);

        return "frontend/job/view_job";
    }

    @GetMapping("/create")
    public String createJob(Model model) {
        model.addAttribute("categories", categoryService.list());
        return "frontend/job/create_job";
    }

    @PostMapping("/save")
    public String saveJob(
            @RequestParam(name = "id", required = false) Long id,
            @ModelAttribute Job job,
            Model model) {

        if (job.getTitle().isEmpty() /*|| 1==1*/) {
            model.addAttribute("error", "Title required");
            return "frontend/job/create_job";
        }

        // Set current loged user ID as author
        User author = super.getCurrentUser();
        if (author == null) {
            System.out.println("Please login to add a job!");
            return null;
        }
        job.setAuthor(author);

        Job savedJob = null;
        if (id != null && id > 0) {
// TODO
        } else {
            savedJob = jobService.add(job);

            //getting emails
            // Split the required skills into a list and trim any extra spaces
            List<String> requiredSkillsList = Arrays.stream(savedJob.getRequiredSkills().split(","))
                    .map(String::trim) // Trim any extra spaces around skills
                    .collect(Collectors.toList());

            // Calling the method to get users by skills
            String[] emails = getEmailsOfPeopleWithMatchingSkills(requiredSkillsList);

            EmailDetails emailDetails = new EmailDetails();
            emailDetails.setSubject("Exciting Opportunity: " + job.getTitle());
            emailDetails.setRecipients(emails);
            emailDetails.setMsgBody(
                    "Dear Freelancer,\n\n" +
                            "We have an exciting new job posting that matches your skills and expertise! Here are the details:\n\n" +
                            "Title: " + job.getTitle() + "\n" +
                            "Description: " + job.getDescription() + "\n" +
                            "Budget: $" + job.getBudget() + "\n" +
                            "Type: " + job.getType() + "\n" +
                            "Expertise Level: " + job.getExpertizeLevel() + "\n" +
                            "Required Skills: " + job.getRequiredSkills() + "\n\n" +
                            "Don't miss out on this opportunity to showcase your skills. Visit our platform to view more details and apply now!\n\n" +
                            "Best regards,\n" +
                            "The Freelancer Team"
            );


            System.out.println("[Email: ]" + emails + ">>>>>>>>>>>>>..");

            String result = emailService.sendSimpleMail(emailDetails);
            System.out.println("[EMail result ]" + result);

        }
        return "redirect:/job/view/" + savedJob.getId();
    }

    @GetMapping("/bids/{jobId}")
    public String viewBids(Model model, @PathVariable("jobId") long jobId) {

        Job job = jobService.get(jobId);

        User me = getCurrentUser();

        if (job == null || job.getAuthor().getId() != me.getId()) {
            System.out.println("Job not found or you don't have privileges");
            return "redirect:/job/view/" + jobId;
        }

        List<Bid> bids = bidService.findByJob(job);

        model.addAttribute("job", job);
        model.addAttribute("bids", bids);

        // If me != author
        return "frontend/job/view_bids";
    }

    public String[] getEmailsOfPeopleWithMatchingSkills(List<String> requiredSkills) {

        String[] emails = userController.getUsersBySkills(requiredSkills).getBody();
        return emails;

    }
}
