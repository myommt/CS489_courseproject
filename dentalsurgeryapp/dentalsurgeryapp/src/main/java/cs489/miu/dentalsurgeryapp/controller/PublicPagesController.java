package cs489.miu.dentalsurgeryapp.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * Controller for handling public pages that don't require authentication
 */
@Controller
@RequestMapping("/")
public class PublicPagesController {

    /**
     * Homepage - displays the main landing page
     */
    @GetMapping({"/", "/index", "/home"})
    public String index(Model model) {
        model.addAttribute("pageTitle", "Welcome to Dental Surgery");
        model.addAttribute("activeTab", "home");
        return "public/index";
    }

    /**
     * About Us page - displays information about the dental practice
     */
    @GetMapping("/about")
    public String about(Model model) {
        model.addAttribute("pageTitle", "About Our Dental Surgery");
        model.addAttribute("activeTab", "about");
        return "public/about";
    }

    /**
     * Services page - displays available dental services
     */
    @GetMapping("/services")
    public String services(Model model) {
        model.addAttribute("pageTitle", "Our Dental Services");
        model.addAttribute("activeTab", "services");
        return "public/services";
    }

    /**
     * Contact page - displays contact information and form
     */
    @GetMapping("/contact")
    public String contact(Model model) {
        model.addAttribute("pageTitle", "Contact Us");
        model.addAttribute("activeTab", "contact");
        return "public/contact";
    }

    /**
     * Virtual Tour page - displays virtual tour of the facility
     */
    @GetMapping("/virtual-tour")
    public String virtualTour(Model model) {
        model.addAttribute("pageTitle", "Virtual Tour");
        model.addAttribute("activeTab", "tour");
        return "public/virtual-tour";
    }

    /**
     * Emergency page - displays emergency contact information
     */
    @GetMapping("/emergency")
    public String emergency(Model model) {
        model.addAttribute("pageTitle", "Emergency Dental Care");
        model.addAttribute("activeTab", "emergency");
        return "public/emergency";
    }

    /**
     * Login page - displays login form (if using custom login page)
     */
    @GetMapping("/public/login")
    public String login(Model model) {
        model.addAttribute("pageTitle", "Login");
        return "public/login";
    }

    /**
     * Error page handler
     */
    @GetMapping("/error")
    public String error(Model model) {
        model.addAttribute("pageTitle", "Error");
        return "public/error";
    }
}