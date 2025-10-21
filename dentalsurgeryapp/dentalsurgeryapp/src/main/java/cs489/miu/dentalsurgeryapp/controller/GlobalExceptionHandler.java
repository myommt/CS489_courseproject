package cs489.miu.dentalsurgeryapp.controller;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public String handleTypeMismatch(MethodArgumentTypeMismatchException ex,
                                     HttpServletRequest request,
                                     RedirectAttributes ra) {
        String uri = request.getRequestURI();
        if (uri != null && (
                uri.startsWith("/secured/appointment/edit") ||
                uri.startsWith("/secured/appointment/view") ||
                uri.startsWith("/secured/appointment/delete")
        )) {
            ra.addFlashAttribute("errorMessage", "Invalid appointment id.");
            return "redirect:/secured/appointment/list";
        }
        // Fallback: send to dashboard
        ra.addFlashAttribute("errorMessage", "Invalid request.");
        return "redirect:/secured/index";
    }
}
