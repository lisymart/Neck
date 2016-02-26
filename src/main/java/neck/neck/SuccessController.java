package neck.neck;

import javax.servlet.http.HttpServletRequest;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class SuccessController {

	@RequestMapping(value = "/success", method = RequestMethod.POST)
	public ModelAndView success(HttpServletRequest request){
		String ES = request.getParameter("ES");
		return new ModelAndView("loadFile", "ES", ES);
	}
}